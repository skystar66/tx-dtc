package com.xuliang.lcn.service;


import com.xuliang.lcn.server.TransactionCmd;

import java.io.Serializable;

/**
 * LCN分布式事务 manager业务处理
 * @author xuliang
 */
public interface RpcExecuteService {




    /**
     * 执行业务
     * @param transactionCmd  transactionCmd
     * @return  Object
     * @throws Exception TxManagerException
     */
    Serializable execute(TransactionCmd transactionCmd) throws Exception;



}
