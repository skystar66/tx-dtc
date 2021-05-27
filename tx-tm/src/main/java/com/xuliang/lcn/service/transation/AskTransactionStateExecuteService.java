package com.xuliang.lcn.service.transation;


import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.MessageSender;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * Description: 询问事务组状态
 * Date: 2018/12/18
 *
 * @author xuliang
 */
@Component("rpc_ask-transaction-state")
@Slf4j
public class AskTransactionStateExecuteService implements RpcExecuteService {


    @Autowired
    FastStorage fastStorage;
    @Autowired
    MessageSender messageSender;



    @Override
    public void execute(RpcCmd transactionCmd) {

        try {
            int state = fastStorage.getTransactionState(transactionCmd.getMsg().getGroupId());
            Serializable msg = state == -1 ? 0 : state;
            messageSender.senderSuccess(transactionCmd, msg);
        } catch (Exception ex) {
            log.error("action: rpc_ask-transaction-state,groupId:{} execute service error. error: {}", transactionCmd.getMsg().getGroupId(),ex);
            messageSender.senderFail(transactionCmd, ex);
        }
    }
}
