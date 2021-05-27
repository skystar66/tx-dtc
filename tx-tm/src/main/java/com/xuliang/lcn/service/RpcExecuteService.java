package com.xuliang.lcn.service;


import com.xuliang.lcn.txmsg.dto.RpcCmd;

/**
 * LCN分布式事务 manager业务处理
 *
 * @author xuliang
 */
public interface RpcExecuteService {


    /**
     * 执行业务
     *
     * @param transactionCmd transactionCmd
     * @return Object
     * @throws Exception TxManagerException
     */
    void execute(RpcCmd transactionCmd) throws Exception;


}
