package com.xuliang.lcn.service.transation;


import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
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

    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        int state = fastStorage.getTransactionState(transactionCmd.getGroupId());
        return state == -1 ? 0 : state;
    }
}
