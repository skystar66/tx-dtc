package com.xuliang.tc.core.transaction;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.tc.core.TransactionControl;
import com.xuliang.tc.core.TransactionMsgSenger;
import com.xuliang.tc.core.strategy.TransactionCommitorStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


/**
 * @author xuliang
 * @desc: 开始事务
 */
@Service(value = "control_lcn_notify")
@Slf4j
public class TransactionNotifyTransaction implements TransactionControl {


    @Autowired
    TransactionMsgSenger transactionMsgSenger;


    @Autowired
    private TransactionCommitorStrategy transactionCommitorStrategy;



    @Override
    public void execute(TransactionLocalContext transactionLocalContext) throws Exception {
        log.info(
                "notify transaction group groupId: {} transaction type: {}, state: {}.",
                transactionLocalContext.getGroupId(), transactionLocalContext.getTransactionType(),
                transactionLocalContext.getSysTransactionState());
        long t1 = System.currentTimeMillis();
        MessageDto messageDto = transactionMsgSenger.notifyGroup(transactionLocalContext.getGroupId(),
                transactionLocalContext.getSysTransactionState().getStatus());
        //todo 重试策略需要统一一下！！
        while (messageDto == null) {
            //尝试再次通知.
            messageDto = transactionMsgSenger.notifyGroup(transactionLocalContext.getGroupId(),
                    transactionLocalContext.getSysTransactionState().getStatus());
        }
        int state = messageDto.loadBean(Integer.class);
        /**提交事物*/
        transactionCommitorStrategy.commit(transactionLocalContext.getGroupId(),
                transactionLocalContext.getTransactionType(),
                TransactionStatus.getTransactionStatus(state));
        long t2 = System.currentTimeMillis();
        log.info("notify transaction group success groupId:{},time:{}ms,result:{}",
                transactionLocalContext.getGroupId(),(t2 - t1), messageDto);
    }
}
