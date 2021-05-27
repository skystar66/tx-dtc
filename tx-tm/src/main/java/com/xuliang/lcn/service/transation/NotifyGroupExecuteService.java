package com.xuliang.lcn.service.transation;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xuliang.lcn.core.manager.LCNTransactionManager;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.MessageSender;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.params.NotifyGroupParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * Description:
 * Date: 2018/12/11
 *
 * @author xuliang
 */
@Service("rpc_notify-group")
@Slf4j
public class NotifyGroupExecuteService implements RpcExecuteService {


    @Autowired
    FastStorage fastStorage;


    @Autowired
    LCNTransactionManager lcnTransactionManager;

    @Autowired
    MessageSender messageSender;

    private static ThreadPoolExecutor executorNotify = new ThreadPoolExecutor(100,
            200,
            10L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(20 * 10000),
            new ThreadFactoryBuilder().setNameFormat("notify-pool-%d").build(),
            new ThreadPoolExecutor.DiscardOldestPolicy());


    @Override
    public void execute(RpcCmd transactionCmd) {

        /**notify 通知事务组需要保证高可用性，好性能化，单独让其使用线程池*/
        executorNotify.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    // 解析参数
                    NotifyGroupParams notifyGroupParams = transactionCmd.getMsg().loadBean(NotifyGroupParams.class);
                    int commitState = notifyGroupParams.getState();
                    // 获取事物状态 但手动回滚实现和置状态
                    int transactionState = fastStorage.getTransactionState(transactionCmd.getMsg().getGroupId());
                    if (transactionState == 0) {
                        commitState = 0;
                    }
                    //系统日志
                    log.info("notify group groupId:{} state: {}",
                            transactionCmd.getMsg().getGroupId(),
                            notifyGroupParams.getState());

                    //如果状态为1则事务成功，通知参与模块进行提交
                    if (commitState == 1) {
                        lcnTransactionManager.commit(notifyGroupParams.getGroupId());
                    } else {
                        lcnTransactionManager.rollback(notifyGroupParams.getGroupId());
                    }
                    if (transactionState == 0) {
                        log.info("group groupId:{} rollback.", transactionCmd.getMsg().getGroupId());
                    }
                    messageSender.senderSuccess(transactionCmd, commitState);
                } catch (Exception e) {
                    log.error("action: rpc_notify-group groupId:{} execute service error. error: {}", transactionCmd.getMsg().getGroupId(), e);
                    messageSender.senderFail(transactionCmd, e);
                }
            }
        });

    }
}
