package com.xuliang.tc.aspect.interceptor;

import com.xuliang.tc.aspect.DTXInfo;
import com.xuliang.tc.manager.TransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Objects;
import java.util.Properties;

/**
 * 事物方法拦截
 */
@Slf4j
public class TxLcnInterceptor implements MethodInterceptor {

    private final TransactionManager transactionManager;

    private Properties transactionAttributes;

    public TxLcnInterceptor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }


    public void setTransactionAttributes(Properties transactionAttributes) {
        this.transactionAttributes = transactionAttributes;
    }

    @Override
    public Object invoke(MethodInvocation methodInvocation) throws Throwable {
        log.info("come in txLcnInterceptor invoke , invocation : {} >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>", methodInvocation);
        DTXInfo dtxInfo = InterceptorInvocationUtils.load(methodInvocation, transactionAttributes);
        if (Objects.isNull(dtxInfo)) {
            return null;
        }
        return transactionManager.runTransaction(dtxInfo, methodInvocation::proceed);
    }
}
