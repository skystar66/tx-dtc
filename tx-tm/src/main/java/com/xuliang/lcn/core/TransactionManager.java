package com.xuliang.lcn.core;


/**
 * Description: 事务管理器
 * Date: 19-6-24 下午5:50
 *
 * @author xuliang
 */

public interface TransactionManager {


    /**
     * 开始分布式事务
     *
     * @param groupId 分布式事务
     * @throws TransactionException TransactionException
     */
    void begin(String groupId) throws Exception;



}
