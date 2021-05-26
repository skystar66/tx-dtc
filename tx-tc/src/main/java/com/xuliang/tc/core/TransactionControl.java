package com.xuliang.tc.core;


import com.xuliang.lcn.common.context.TransactionLocalContext;

/**
 * 分布式事务控制器
 *
 * @author xuliang
 */
public interface TransactionControl {


    /**
     * 执行事物
     *
     * @param transactionLocalContext info
     */
    public void execute(TransactionLocalContext transactionLocalContext) throws Exception;


}
