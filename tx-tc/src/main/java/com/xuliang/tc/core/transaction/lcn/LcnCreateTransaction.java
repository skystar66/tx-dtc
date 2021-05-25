package com.xuliang.tc.core.transaction.lcn;

import com.xuliang.tc.core.LCNLocalControl;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tc.core.template.TransactionControlTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @author xuliang
 * @desc: 开始事务
 */
@Service(value = "control_lcn_create")
@Slf4j
public class LcnCreateTransaction implements LCNLocalControl {


    @Autowired
    private TCGlobalContext globalContext;

    @Autowired
    TransactionControlTemplate template;

    /**
     * 业务代码执行前
     * 1,创建事物组
     * 2,设置sql代理资源
     *
     * @param info info
     * @throws TransactionException TransactionException
     */
    @Override
    public void preBusinessCode(TxTransactionInfo info) throws Exception {

        //创建事物组
        template.createGroup(info.getGroupId(), info.getUnitId(),
                info.getTransactionInfo(), info.getTransactionType());
        //设置sql 代理资源
        DTXLocalContext.makeProxy();

    }

    /**
     * 业务代码执行失败 设置状态为0
     *
     * @param info      info
     * @param throwable throwable
     * @throws TransactionException TransactionException
     */
    @Override
    public void onBusinessCodeError(TxTransactionInfo info, Throwable throwable) throws Exception {
        //业务代码执行失败，标记系统分布式任务执行状态为0
        DTXLocalContext.cur().setSysTransactionState(0);
    }

    /**
     * 业务代码执行成功 设置状态 1
     *
     * @param info   info
     * @param result result
     * @throws TransactionException TransactionException
     */
    @Override
    public void onBusinessCodeSuccess(TxTransactionInfo info, Object result) throws Exception {
        //业务代码执行成功，标记系统分布式任务执行状态为1
        DTXLocalContext.cur().setSysTransactionState(1);


    }

    /**
     * 清场
     *
     * @param info info
     */

    @Override
    public void postBusinessCode(TxTransactionInfo info) {
        // RPC close DTX group
        template.notifyGroup(
                info.getGroupId(), info.getUnitId(), info.getTransactionType(),
                //1 commit 0 rollback
                DTXLocalContext.transactionState(globalContext.dtxState(info.getGroupId())));
    }
}
