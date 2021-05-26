package com.xuliang.tc.core.transaction;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.exception.TxException;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.tc.core.TransactionControl;
import com.xuliang.tc.core.TransactionMsgSenger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author xuliang
 * @desc: 开始事务
 */
@Service(value = "control_lcn_create")
@Slf4j
public class TransactionCreateTransaction implements TransactionControl {

    @Autowired
    TransactionMsgSenger transactionMsgSenger;


    /**
     * 业务代码执行前
     * 1,创建事物组
     *
     * @param transactionLocalContext transactionLocalContext
     * @throws TxException TransactionException
     */
    @Override
    public void execute(TransactionLocalContext transactionLocalContext) throws Exception {

        log.info("create transaction group > groupId: {} ", transactionLocalContext.getGroupId());
        long t1 = System.currentTimeMillis();
        //创建事物组
        MessageDto result = transactionMsgSenger.createGroup(transactionLocalContext.getGroupId());
        if (result == null) {
            throw new TxException("create transaction fail. groupId:"+transactionLocalContext.getGroupId()+"");
        }
        long t2 = System.currentTimeMillis();
        log.info("create transaction group success time:{}ms,result:{}",  (t2 - t1),result);

    }
}
