package com.xuliang.tc.core.propagation;


import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tc.core.enums.DTXPropagationState;

/**
 * Description: 事务分离器
 * Date: 2018/12/5
 *
 * @author xuliang
 */
public interface DTXPropagationResolver {




    /**
     * 判断事务状态
     *
     * @param txTransactionInfo txTransactionInfo
     * @return DTXPropagationState
     * @throws TransactionException TransactionException
     */
    DTXPropagationState resolvePropagationState(TxTransactionInfo txTransactionInfo) throws Exception;




}
