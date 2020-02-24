package com.xuliang.tc.core;


import com.xuliang.tc.core.context.TxTransactionInfo;

/**
 *  LCN分布式事务控制
 * @author xuliang
 */
public interface LCNLocalControl {



    /**
     * 业务代码执行前
     *
     * @param info info
     * @throws  TransactionException TransactionException
     */
    default void preBusinessCode(TxTransactionInfo info) throws Exception {

    }

    /**
     * 执行业务代码
     *
     * @param info info
     * @return  Object Object
     * @throws Throwable Throwable
     */
    default Object doBusinessCode(TxTransactionInfo info) throws Throwable {
        return info.getBusinessCallback().call();
    }


    /**
     * 业务代码执行失败
     *
     * @param info info
     * @param throwable throwable
     * @throws TransactionException TransactionException
     */
    default void onBusinessCodeError(TxTransactionInfo info, Throwable throwable) throws Exception {

    }

    /**
     * 业务代码执行成功
     *
     * @param info info
     * @param result result
     * @throws TransactionException TransactionException
     */
    default void onBusinessCodeSuccess(TxTransactionInfo info, Object result) throws Exception {

    }

    /**
     * 清场
     *
     * @param info info
     */
    default void postBusinessCode(TxTransactionInfo info) {

    }



}
