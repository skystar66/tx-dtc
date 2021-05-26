package com.xuliang.tc.core.checking;


import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.tc.config.TxClientConfig;
import com.xuliang.tc.core.TransactionMsgSenger;
import com.xuliang.tc.core.cache.TcCache;
import com.xuliang.tc.core.strategy.TransactionCommitorStrategy;
import com.xuliang.tc.utils.TransactionStatusConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Description: 基于JDK任务调度线程池实现的事物检测 {}
 * Date: 2018/12/19
 *
 * @author xuliang
 * @see TransactionChecking
 */
@Component
@Slf4j
public class TransactionChecking {

    private static final Map<String, ScheduledFuture> delayTasks = new ConcurrentHashMap<>();


    private static final ScheduledExecutorService scheduledExecutorService
            = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);


    @Autowired
    private TxClientConfig clientConfig;


    @Autowired
    TransactionMsgSenger transactionMsgSenger;


    @Autowired
    TransactionCommitorStrategy transactionCommitorStrategy;

    /**
     * 开始事务检测。设置定时器，在超时时间后做最后事务状态的确认
     *
     * @param groupId         groupId
     * @param unitId          unitId
     * @param transactionType transactionType
     */
    public void startDelayCheckingAsync(String groupId, String unitId, String transactionType) {

        log.info("groupId: {} unitId： {} start delay checking task", groupId, unitId);
        ScheduledFuture scheduledFuture = scheduledExecutorService.schedule(() -> {
            try {
                //询问TM事物状态
                int state = transactionMsgSenger.askTransactionState(groupId, unitId);
                log.info("groupId: {} unitId: {}ask transaction state {}", groupId, unitId, state);
                if (state == -1) {
                    log.error(this.getClass().getSimpleName(), "delay clean transaction error.");
                    onAskTransactionStateException(groupId, unitId, transactionType);
                } else {
                    //事物提交
                    transactionCommitorStrategy.commit(groupId,transactionType,TransactionStatus.getTransactionStatus(state));
                    //清理sql资源
                    TcCache.getInstance().cleanTransactionConnection(groupId);
                }
            } catch (Exception e) {
                onAskTransactionStateException(groupId, unitId, transactionType);
            }
        }, clientConfig.getDtxTime(), TimeUnit.MILLISECONDS);
        delayTasks.put(groupId + unitId, scheduledFuture);
    }


    private void onAskTransactionStateException(String groupId, String unitId, String transactionType) {
        try {
            // 通知 txManager 保存事物补偿记录
            transactionMsgSenger.reportTransactionState(groupId, unitId, TransactionStatusConstants.ASK_ERROR, 0);
            //事物回滚
            transactionCommitorStrategy.commit(groupId,transactionType, TransactionStatus.FAIL);
            //清理sql缓存
            TcCache.getInstance().cleanTransactionConnection(groupId);
        } catch (Exception e) {
            log.error(groupId, unitId, "{} > clean transaction error.{}", transactionType, e);
        }
    }


    /**
     * 手动停止事务检测。确定分布式事务结果正常时手动结束检测
     *
     * @param groupId groupId
     * @param unitId  unitId
     */
    public void stopDelayChecking(String groupId, String unitId) {
        ScheduledFuture scheduledFuture = delayTasks.get(groupId + unitId);
        if (Objects.nonNull(scheduledFuture)) {
            log.info("stop checking task groupId:{},unitId:{} checking.", groupId, unitId);
            scheduledFuture.cancel(true);
            delayTasks.remove(groupId + unitId);
        }
    }


}
