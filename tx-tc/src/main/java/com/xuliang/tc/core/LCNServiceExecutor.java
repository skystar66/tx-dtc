package com.xuliang.tc.core;


import com.xuliang.tc.core.context.TCGlobalContext;
import com.xuliang.tc.core.context.TxTransactionInfo;
import com.xuliang.tc.core.enums.DTXPropagationState;
import com.xuliang.tc.core.propagation.DTXPropagationResolver;
import com.xuliang.tc.support.TxLcnBeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * LCN分布式事务业务执行器
 * Created by xulaing on 2017/6/8.
 */
@Component
@Slf4j
public class LCNServiceExecutor {


    @Autowired
    TxLcnBeanHelper txLcnBeanHelper;

    @Autowired
    TCGlobalContext tcGlobalContext;

    @Autowired
    DTXPropagationResolver dtxPropagationResolver;


    /**
     * 事务业务执行
     *
     * @param info info
     * @return Object
     * @throws Throwable Throwable
     */
    public Object transactionRunning(TxTransactionInfo info) throws Throwable {
        //获取事物类型
        String transactionType = info.getTransactionType();
        //获取事物传播状态
        DTXPropagationState propagationState = dtxPropagationResolver.resolvePropagationState(info);
        // 获取本地分布式事务控制器
        LCNLocalControl dtxLocalControl = txLcnBeanHelper.loadDTXLocalControl(transactionType, propagationState);
        //当事务等级为 supports 时，表示不加入事物
        if (dtxLocalControl == null) {
            return info.getBusinessCallback().call();
        }
        //织入事物操作
        try {
            //计入事务类型到事物的上下文
            Set<String> transactionTypeSet = tcGlobalContext.txContext(info.getGroupId()).getTransactionTypes();
            transactionTypeSet.add(transactionType);
            //执行事物传播机制，设置sql连接的代理资源
            dtxLocalControl.preBusinessCode(info);
            //业务执行前，打印日志
            log.info(" groupId : {} unitId: {} pre business code, unit type: ", info.getGroupId(), info.getUnitId(), transactionType);
            //执行业务
            Object result = dtxLocalControl.doBusinessCode(info);
            //业务执行成功
            log.info(" groupId : {} unitId: {} business success ,result : {}",info.getGroupId(),info.getUnitId(), result);
            dtxLocalControl.onBusinessCodeSuccess(info, result);
            return result;
        } catch (Exception e) {
            //业务执行失败
            log.error(" groupId : {} unitId: {} before business code error: {}", info.getGroupId(), info.getUnitId(), e);
            dtxLocalControl.onBusinessCodeError(info, e);
            throw e;
        } finally {
            //业务执行完毕
            dtxLocalControl.postBusinessCode(info);
        }
    }
}

