package com.xuliang.tc.core.transaction.lcn;

import com.xuliang.lcn.common.util.Transactions;
import com.xuliang.tc.core.LCNLocalControl;
import com.xuliang.tc.core.LCNServiceExecutor;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tc.core.template.TransactionCleanTemplate;
import com.xuliang.tc.core.template.TransactionControlTemplate;
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
public class LcnJoinTransaction implements LCNLocalControl {


    @Autowired
    private TransactionCleanTemplate transactionCleanTemplate;

    @Autowired
    private TransactionControlTemplate transactionControlTemplate;


    @Override
    public void preBusinessCode(TxTransactionInfo info) throws Exception {
        DTXLocalContext.makeProxy();
    }


    @Override
    public void onBusinessCodeError(TxTransactionInfo info, Throwable throwable) throws Exception {
        try {
            transactionCleanTemplate.clean(info.getGroupId(), info.getUnitId(), info.getTransactionType(), 0);
        } catch (Exception e) {
            log.error("LCN > clean transaction error.");
        }
    }

    @Override
    public void onBusinessCodeSuccess(TxTransactionInfo info, Object result) throws Exception {
        log.info("join group: [GroupId: {},Method: {}]" , info.getGroupId(),
                info.getTransactionInfo().getMethodStr());

        // join DTX group
        transactionControlTemplate.joinGroup(info.getGroupId(), info.getUnitId(), info.getTransactionType(),
                info.getTransactionInfo());
    }

}
