package com.xuliang.tc.core.strategy;

import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.tc.core.commit.Commitor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 事物提交策略
 *
 * @author xuliang
 * @date:2021-05-26
 */

@Slf4j
@AllArgsConstructor
public class TransactionCommitorStrategy {

    private List<Commitor> commitors;

    private Commitor getCommitor(String transactionType) {
        for (Commitor commitor : commitors) {
            if (commitor.type().equals(transactionType)) {
                return commitor;
            }
        }
        return null;
    }

    public void commit(String groupId, String transactionType, TransactionStatus state) {
        //根据不同的事务类型区分做事务提交处理.
        Commitor commitor = getCommitor(transactionType);
        log.info("commit=> groupId:{},state:{},transactionType:{}", groupId, state, transactionType);
        if (commitor != null) {
            commitor.commit(groupId, state);
        }
    }


}
