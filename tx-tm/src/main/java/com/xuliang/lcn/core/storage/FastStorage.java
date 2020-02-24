package com.xuliang.lcn.core.storage;


import com.xuliang.lcn.cluster.TMProperties;

import java.util.List;

/**
 * Description: redis 快速缓存 Manager cache
 * Date: 19-1-21 下午2:53
 *
 * @author xuliang
 */
public interface FastStorage {


    /**
     * find all Manager
     * 查找所有的 tm 端 配置
     *
     * @return list
     * @throws FastStorageException fastStorageException
     */
    List<TMProperties> findTMProperties() throws Exception;

    /**
     * delete Manager address
     * 删除tm服务
     *
     * @param host            host
     * @param transactionPort transactionPort
     * @throws FastStorageException fastStorageException
     */
    void removeTMProperties(String host, int transactionPort) throws Exception;


    /**
     * save Manager address is ip:port
     * 保存tm 服务信息
     *
     * @param tmProperties ip:port
     * @throws FastStorageException fastStorageException
     */
    void saveTMProperties(TMProperties tmProperties) throws Exception;


    /*-----------------------DTX group------------------------------*/

    /**
     * init DTX group.
     * 初始化事物组信息
     * note: group info should clean by self 10 seconds after DTX time.
     *
     * @param groupId groupId
     * @throws FastStorageException fastStorageException
     */
    void initGroup(String groupId) throws Exception;


    /**
     * join DTX group.
     * 加入当前事务组
     * note: group info should clean by self 10 seconds after DTX time.
     *
     * @param groupId groupId
     * @throws FastStorageException fastStorageException
     */

    void saveTransactionUnitToGroup(String groupId, TransactionUnit transactionUnit);


    /**
     * save DTX state
     * 保存事物组状态
     * note: transaction state must clean by self 10 seconds after DTX time.
     *
     * @param groupId groupId
     * @param state   status 1 commit 0 rollback
     * @throws FastStorageException fastStorageException
     */
    void saveTransactionState(String groupId, int state) throws Exception;


    /**
     * get DTC state
     * 获取事物状态
     *
     * @param groupId groupId
     * @return int
     * @throws FastStorageException fastStorageException
     */
    int getTransactionState(String groupId) throws Exception;


    /**
     * get group all unit
     * 获取该事务组下的所有事物单元
     *
     * @param groupId groupId
     * @return list
     * @throws FastStorageException fastStorageException
     */
    List<TransactionUnit> findTransactionUnitsFromGroup(String groupId) throws Exception;


    /**
     * clear group
     * 清理事务组
     *
     * @param groupId groupId
     * @throws FastStorageException fastStorageException
     */

    void clearGroup(String groupId) throws Exception;

}
