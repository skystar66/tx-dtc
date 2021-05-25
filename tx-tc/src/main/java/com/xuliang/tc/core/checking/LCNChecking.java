package com.xuliang.tc.core.checking;


import com.xuliang.lcn.common.util.Transactions;
import com.xuliang.tc.config.TxClientConfig;
import com.xuliang.tc.core.LCNMessenger;
import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.context.TxContext;
import com.xuliang.tc.core.template.TransactionCleanTemplate;
import com.xuliang.tc.utils.TransactionStatusConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * Description: 基于JDK任务调度线程池实现的DTX检测 {}
 * Date: 2018/12/19
 *
 * @author xuliang
 * @see LCNChecking
 */
@Component
@Slf4j
public class LCNChecking {

    private static final Map<String, ScheduledFuture> delayTasks = new ConcurrentHashMap<>();


    private static final ScheduledExecutorService scheduledExecutorService
            = Executors.newScheduledThreadPool(Runtime.getRuntime().availableProcessors() * 2);


    @Autowired
    private TxClientConfig clientConfig;

    @Autowired
    private TCGlobalContext globalContext;

    @Autowired
    LCNMessenger lcnMessenger;

    @Autowired
    TransactionCleanTemplate transactionCleanTemplate;


    /**
     * 开始事务检测。设置定时器，在超时时间后做最后事务状态的确认
     *
     * @param groupId         groupId
     * @param unitId          unitId
     * @param transactionType transactionType
     */
    public void startDelayCheckingAsync(String groupId, String unitId, String transactionType) {

        log.info("groupId: {} unitId： {} start delay checking task",groupId, unitId);
        ScheduledFuture scheduledFuture = scheduledExecutorService.schedule(() -> {
            try {
                //获取事物上下文
                TxContext txContext = globalContext.txContext(groupId);
                if (null != txContext) {
                    synchronized (txContext.getLock()) {
                        //  等待事物结束，通知、
                        log.info("groupId: {} unitId: {}  checking waiting for business code finish.",groupId, unitId);
                        txContext.getLock().wait();
                    }
                }
                //询问TM事物状态
                int state = lcnMessenger.askTransactionState(groupId, unitId);
                log.info( "groupId: {} unitId: {}ask transaction state {}",groupId, unitId, state);
                if (state == -1) {
                    log.error(this.getClass().getSimpleName(), "delay clean transaction error.");
                    onAskTransactionStateException(groupId, unitId, transactionType);
                } else {
                    transactionCleanTemplate.clean(groupId, unitId, transactionType, state);
                }
            } catch (Exception e) {
                onAskTransactionStateException(groupId, unitId, transactionType);
            }
        }, clientConfig.getDtxTime(), TimeUnit.MILLISECONDS);
        delayTasks.put(groupId + unitId, scheduledFuture);
    }


    private void onAskTransactionStateException(String groupId, String unitId, String transactionType) {
        try {
            // 通知txManager 保存事物补偿记录
            lcnMessenger.reportTransactionState(groupId, unitId, TransactionStatusConstants.ASK_ERROR, 0);
            //事物回滚
            transactionCleanTemplate.cleanLCNTransation(groupId, unitId, transactionType, 0);
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
