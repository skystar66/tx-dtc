package com.xuliang.tc.manager;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import com.xuliang.lcn.common.enums.TransactionStatus;
import com.xuliang.lcn.common.enums.TransactionType;
import com.xuliang.tc.aspect.callback.BusinessCallback;
import com.xuliang.tc.core.TransactionServiceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Description:事物管理器
 * Company: CodingApi
 * Date: 2018/11/29
 *
 * @author xuliang
 */
@Slf4j
@Component
public class XLTransactionManager {

    @Autowired
    TransactionServiceExecutor transactionServiceExecutor;
    /**
     * 执行事务
     */
    public Object runTransaction(BusinessCallback business) throws Throwable {

        log.info("<----- run transaction start ----->");
        TransactionLocalContext transactionLocalContext
                = TransactionLocalContextThreadLocal.current();
        //当transactionLocalContext为空时，表名 是开始分布式事物，事物发起方
        if (transactionLocalContext == null) {
            transactionLocalContext = TransactionLocalContextThreadLocal.getNewTransacationContext();
        }
        //设置同一事物类型 //todo 先写LCN
        transactionLocalContext.setTransactionType(TransactionType.LCN.getType());
        Object res = null;
        try {
            //开始执行事务
            transactionServiceExecutor.tryBeginTransaction(transactionLocalContext);
            //执行业务
            res = business.call();
            //设置事物状态为1，标识成功
            transactionLocalContext.setSysTransactionState(TransactionStatus.SUCCESS);
        } catch (Exception e) {
            //设置事物状态为0，标识失败
            transactionLocalContext.setSysTransactionState(TransactionStatus.FAIL);
            throw e;
        } finally {
            //结束事物
            transactionServiceExecutor.tryEndTransaction(transactionLocalContext);
            //清理事物
            transactionServiceExecutor.cleanTransaction();
        }
        log.info("<---- run transaction end ---->");
        return res;
    }


}
