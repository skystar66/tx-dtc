package com.xuliang.tc.core.commit.impl;

import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.lcn.common.enums.TransactionType;
import com.xuliang.tc.core.cache.TcCache;
import com.xuliang.tc.core.commit.Commitor;
import com.xuliang.tc.core.commit.ConnectionProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author xuliang
 * @desc: lcn 事物提交
 */
@Service("lcn_commit_transaction")
@Slf4j
public class CommitorService implements Commitor {

    @Override
    public String type() {
        return TransactionType.LCN.getType();
    }

    @Override
    public void commit(String groupId, TransactionStatus state) {
        try {
            ConnectionProxy connectionProxy = TcCache.getInstance().getTransactionSqlConnection(groupId);
            if (connectionProxy != null) {
                connectionProxy.commit(state);
            }
        } catch (Exception ex) {
            log.error("transaction commit error:{}", ex);
        }
    }
}
