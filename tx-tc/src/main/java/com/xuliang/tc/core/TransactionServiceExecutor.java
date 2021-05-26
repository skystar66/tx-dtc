package com.xuliang.tc.core;


import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import com.xuliang.lcn.common.enums.TransactionPropagationState;
import com.xuliang.tc.core.checking.TransactionChecking;
import com.xuliang.tc.helper.TxLcnBeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * LCN分布式事务业务执行器
 * Created by xulaing on 2017/6/8.
 */
@Component
@Slf4j
public class TransactionServiceExecutor {


    @Autowired
    TxLcnBeanHelper txLcnBeanHelper;
    @Autowired
    TransactionChecking transactionChecking;


    /**
     * 开始执行事物
     */
    public void tryBeginTransaction(TransactionLocalContext transactionInfo) throws Exception {

        if (transactionInfo.isState(TransactionPropagationState.CREATE)) {
            executeTransaction(transactionInfo);
        }
    }


    /**
     * 事物执行结束
     */
    public void tryEndTransaction(TransactionLocalContext transactionInfo) throws Exception {
        //是否是事物发起方
        if (transactionInfo.isStart()) {
            //最后通知事务组 由事物发起方进行通知
            transactionInfo.setTransactionState(TransactionPropagationState.NOTIFY);
        } else {
            //加入事务组
            transactionInfo.setTransactionState(TransactionPropagationState.JOIN);
        }
        executeTransaction(transactionInfo);
    }


    /**
     * 执行事物
     */
    public void executeTransaction(TransactionLocalContext transactionInfo) throws Exception {

        // 获取本地分布式事务控制器
        TransactionControl dtxLocalControl = txLcnBeanHelper.loadDTXLocalControl(transactionInfo.getTransactionType(),
                transactionInfo.getTransactionState());
        //执行事物
        dtxLocalControl.execute(transactionInfo);

    }


    /**
     * 清理事物
     */
    public void cleanTransaction() {
        //清理事物上下文
        TransactionLocalContextThreadLocal.clear();
    }


}

