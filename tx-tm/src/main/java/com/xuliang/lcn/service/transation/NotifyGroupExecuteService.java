package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.core.TransactionManager;
import com.xuliang.lcn.core.manager.LCNTransactionManager;
import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.txmsg.params.NotifyGroupParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;


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

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        try {
            // 解析参数
            NotifyGroupParams notifyGroupParams = transactionCmd.getMsg().loadBean(NotifyGroupParams.class);
            int commitState = notifyGroupParams.getState();
            // 获取事物状态 但手动回滚实现和置状态
            int transactionState = fastStorage.getTransactionState(transactionCmd.getGroupId());
            if (transactionState == 0) {
                commitState = 0;
            }
            //系统日志
            log.info(
                    transactionCmd.getGroupId(), "", "notify group state: {}", notifyGroupParams.getState());

            //如果状态为1则事务成功，通知参与模块进行提交
            if (commitState == 1) {
                lcnTransactionManager.commit(notifyGroupParams.getGroupId());
            } else {
                lcnTransactionManager.rollback(notifyGroupParams.getGroupId());
            }
            if (transactionState == 0) {
                log.info(transactionCmd.getGroupId(), "", "mandatory rollback for user.");
            }
            return commitState;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lcnTransactionManager.close(transactionCmd.getGroupId());
        }
        return null;
    }
}
