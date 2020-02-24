package com.xuliang.tc.core.context;


import com.xuliang.tc.core.connect.proxy.LcnConnectionProxy;

/**
 * Description:分布式事物上下文
 * Date: 19-1-22 下午6:13
 *
 * @author xuliang
 */
public interface TCGlobalContext {


    /**
     * start tx
     * 开启事务
     *
     * @return tx context info
     */
    TxContext startTx();


    /**
     * del tx info
     * 销毁事物
     */
    void destroyTx();


    /**
     * del tx info
     *
     * @param groupId groupId
     */
    void destroyTx(String groupId);

    /**
     * get context info
     *
     * @return info
     */
    TxContext txContext();


    /**
     * get tx context info by groupId
     *
     * @param groupId groupId
     * @return tx context info
     */
    TxContext txContext(String groupId);


    /**
     * has tx context
     *
     * @return bool
     */
    boolean hasTxContext();

    /**
     * 判断某个事务是否不允许提交
     *
     * @param groupId groupId
     * @return result
     */
    int dtxState(String groupId);


    /**
     * 获取 lcn proxy
     *
     * @param groupId groupId
     * @return connection proxy
     * @throws TCGlobalContextException TCGlobalContextException
     */
    LcnConnectionProxy getLcnConnection(String groupId) throws Exception;


    /**
     * set lcn connection
     *
     * @param groupId         groupId
     * @param connectionProxy connectionProxy
     */
    void setLcnConnection(String groupId, LcnConnectionProxy connectionProxy);


    /**
     * 清理事物组 group
     *
     * @param groupId groupId
     */
    void clearGroup(String groupId);

    /**
     * 设置某个事务组不允许提交
     *
     * @param groupId groupId
     */
    void setRollbackOnly(String groupId);

}
