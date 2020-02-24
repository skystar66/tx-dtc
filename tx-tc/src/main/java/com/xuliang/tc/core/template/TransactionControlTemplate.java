package com.xuliang.tc.core.template;


import com.xuliang.tc.aspect.TransactionInfo;
import com.xuliang.tc.core.LCNMessenger;
import com.xuliang.tc.core.checking.LCNChecking;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TCGlobalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Description: 事务控制器处理模板
 * Date: 2018/12/20
 *
 * @author xuliang
 */
@Component
@Slf4j
public class TransactionControlTemplate {


    @Autowired
    LCNMessenger lcnMessenger;

    @Autowired
    TransactionCleanTemplate cleanTemplate;

    @Autowired
    private TCGlobalContext globalContext;


    @Autowired
    LCNChecking lcnChecking;


    /**
     * Client创建事务组操作集合
     *
     * @param groupId         groupId
     * @param unitId          unitId
     * @param transactionInfo txTrace
     * @param transactionType transactionType
     * @throws TransactionException 创建group失败时抛出
     */

    public void createGroup(String groupId, String unitId, TransactionInfo transactionInfo, String transactionType)
            throws Exception {
        //创建事物组
        try {
            // 日志
            log.info("create group > groupId: {} unitId: {} transaction type: {}",
                    groupId, unitId, transactionType);
            // 创建事务组消息
            lcnMessenger.createGroup(groupId);
            log.info(groupId, unitId, "create group over");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    /**
     * Client通知事务组操作集合
     *
     * @param groupId         groupId
     * @param unitId          unitId
     * @param transactionType transactionType
     * @param state           transactionState
     */
    public void notifyGroup(String groupId, String unitId, String transactionType, int state) {
        log.info(
                "groupId: {} unitId: {} notify group > transaction type: {}, state: {}.", groupId, unitId, transactionType, state);

        try {
            state = lcnMessenger.notifyGroup(groupId, state);
            cleanTemplate.clean(groupId, unitId, transactionType, state);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        log.info(groupId, unitId, "notify group exception state {}.", state);


    }


    /**
     * Client加入事务组操作集合
     *
     * @param groupId         groupId
     * @param unitId          unitId
     * @param transactionType transactionType
     * @param transactionInfo txTrace
     * @throws TransactionException 加入事务组失败时抛出
     */
    public void joinGroup(String groupId, String unitId, String transactionType, TransactionInfo transactionInfo) throws Exception {
        try {
            log.info("groupId: {} unitId: {} join group > transaction type: {}", groupId, unitId, transactionType);
            lcnMessenger.joinGroup(groupId, unitId, transactionType, DTXLocalContext.transactionState(globalContext.dtxState(groupId)));
            log.info("groupId: {} unitId: {} join group message over.", groupId, unitId);
            // 异步检测
            lcnChecking.startDelayCheckingAsync(groupId, unitId, transactionType);

        } catch (Exception e) {
            log.error("加入事务组出现异常:{}", e);
        }
        log.info(groupId, unitId, "join group logic over");


    }
}
