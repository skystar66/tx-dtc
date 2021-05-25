package com.xuliang.tc.core.clean;


/**
 * Description:
 * Date: 2018/12/13
 *
 * @author xuliang
 */
public interface TransactionCleanService {


    /**
     * 事务清理业务
     *
     * @param groupId  groupId
     * @param state    事务状态 1 提交 0 回滚
     * @param unitId   unitId
     * @param unitType 事务类型
     * @throws Exception TransactionClearException
     */
    void clear(String groupId, int state, String unitId, String unitType) throws Exception;


}
