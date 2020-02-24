package com.xuliang.tc.service;


import com.xuliang.tc.client.TransactionCmd;

import java.io.Serializable;

/**
 * LCN分布式事务资源控制
 *
 * @author xuliang
 */
public interface RpcExecuteService {


    /**
     * 执行业务
     *
     * @param transactionCmd transactionCmd
     * @return object
     * @throws TxClientException TxClientException
     */
    Serializable execute(TransactionCmd transactionCmd) throws Exception;


}
