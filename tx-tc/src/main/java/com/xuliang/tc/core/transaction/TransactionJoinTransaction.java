package com.xuliang.tc.core.transaction;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.lcn.common.exception.TxException;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.tc.core.TransactionControl;
import com.xuliang.tc.core.TransactionMsgSenger;
import com.xuliang.tc.core.checking.TransactionChecking;
import com.xuliang.tc.core.strategy.TransactionCommitorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Description: 加入事务组
 * Date: 2018/12/3
 *
 * @author ujued
 */
@Component("control_lcn_join")
@Slf4j
public class TransactionJoinTransaction implements TransactionControl {

    @Autowired
    TransactionMsgSenger transactionMsgSenger;
    @Autowired
    TransactionCommitorStrategy transactionCommitorStrategy;
    @Autowired
    TransactionChecking transactionChecking;


    @Override
    public void execute(TransactionLocalContext transactionLocalContext) throws Exception {
        // 发送加入事务消息
        long t1 = System.currentTimeMillis();
        log.info("join group groupId: {} unitId: {} transaction type: {}",
                transactionLocalContext.getGroupId(), transactionLocalContext.getUnitId(),
                transactionLocalContext.getTransactionType());
        MessageDto result = transactionMsgSenger.joinGroup(transactionLocalContext.getGroupId(), transactionLocalContext.getUnitId(),
                transactionLocalContext.getTransactionType(),
                transactionLocalContext.getSysTransactionState().getStatus());

        //结果为空 或者 分布式事务状态为失败，进行回滚
        if (result == null || transactionLocalContext.getSysTransactionState() ==
                TransactionStatus.FAIL) {
            //回滚当前事物
            transactionCommitorStrategy.commit(transactionLocalContext.getGroupId(),
                    transactionLocalContext.getTransactionType(),
                    TransactionStatus.FAIL);
            throw new TxException("join group fail. groupId:" + transactionLocalContext.getGroupId() + "");
        }
        long t2 = System.currentTimeMillis();
        log.info("join transaction group success time:{}ms, result:{}", (t2 - t1),result);
        // 异步检测
        transactionChecking.startDelayCheckingAsync(transactionLocalContext.getGroupId(),
                transactionLocalContext.getUnitId(), transactionLocalContext.getTransactionType());

    }

}
