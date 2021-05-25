package com.xuliang.tc.core.template;


import com.xuliang.tc.core.checking.LCNChecking;
import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.clean.TransactionCleanService;
import com.xuliang.tc.support.TxLcnBeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description: 事务清理模板
 * Date: 2018/12/13
 *
 * @author xuliang
 */
@Component
@Slf4j
public class TransactionCleanTemplate {


    @Autowired
    TxLcnBeanHelper txLcnBeanHelper;

    @Autowired
    TCGlobalContext globalContext;

    @Autowired
    TransactionCleanService transactionCleanService;

    @Autowired
    LCNChecking lcnChecking;

    /**
     * 清理事务
     *
     * @param groupId  groupId
     * @param unitId   unitId
     * @param unitType unitType
     * @param state    transactionState
     * @throws TransactionClearException TransactionClearException
     */
    public void clean(String groupId, String unitId, String unitType, int state) throws Exception {
        log.info("groupId: {} unitId: {} clean transaction", groupId, unitId);
        try {
            cleanLCNTransation(groupId, unitId, unitType, state);
        } catch (Exception e) {
            e.printStackTrace();
        }
        log.info("groupId: {} unitId: {} clean transaction over", groupId, unitId);
    }

    /**
     * 清理事务
     *
     * @param groupId  groupId
     * @param unitId   unitId
     * @param unitType unitType
     * @param state    transactionState
     * @throws Exception TransactionClearException
     */
    public void cleanLCNTransation(String groupId, String unitId,
                                   String unitType, int state) throws Exception {

        try {
            transactionCleanService.clear(groupId, state, unitId, unitType);
        } finally {
            log.info("clear attachmentCache tx group cache data , groupId : {}", groupId);
            globalContext.clearGroup(groupId);
            //停止检测
            lcnChecking.stopDelayChecking(groupId, unitId);
        }
    }

}
