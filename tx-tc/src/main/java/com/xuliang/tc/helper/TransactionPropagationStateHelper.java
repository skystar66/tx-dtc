package com.xuliang.tc.helper;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import com.xuliang.lcn.common.enums.TransactionPropagation;
import com.xuliang.lcn.common.enums.TransactionPropagationState;
import lombok.extern.slf4j.Slf4j;

/**
 * Description: 事务传播逻辑处理
 * Date: 2018/12/5
 *
 * @author xuliang
 */
@Slf4j
public class TransactionPropagationStateHelper {


    private static class InstanceHolder {
        public static final TransactionPropagationStateHelper instance = new TransactionPropagationStateHelper();
    }

    public static TransactionPropagationStateHelper getInstance() {
        return InstanceHolder.instance;
    }


    /**
     * 事务状态判定
     * 事务状态在创建时是没有TransactionInfo对象的,而在JOIN状态时则是会先创建TransactionInfo
     * 对象，因此有值时返回JOIN
     *
     * @return state
     */
    public TransactionPropagationState getTransactionState() {
        TransactionLocalContext transactionInfo = TransactionLocalContextThreadLocal.current();
        if (transactionInfo == null) {
            return TransactionPropagationState.CREATE;
        } else {
            return TransactionPropagationState.JOIN;
        }
    }

    /**
     * 校验是否参加分布式事务
     */
    public boolean isExecuteTransaction(TransactionPropagation transactionPropagation) {
        // 根据事务传播，对于 SUPPORTS 不参与事务
        if (TransactionPropagation.SUPPORTS.equals(transactionPropagation)) {
            return false;
        }
        return true;
    }


}
