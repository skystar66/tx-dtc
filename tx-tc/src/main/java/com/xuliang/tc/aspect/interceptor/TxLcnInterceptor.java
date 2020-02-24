package com.xuliang.tc.aspect.interceptor;

import com.xuliang.tc.aspect.DTXInfo;
import com.xuliang.tc.aspect.weave.DTXLogicWeaver;
import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Objects;
import java.util.Properties;

@Slf4j
public class TxLcnInterceptor implements MethodInterceptor {

    private final DTXLogicWeaver dtxLogicWeaver;

    private Properties transactionAttributes;

    public TxLcnInterceptor(DTXLogicWeaver dtxLogicWeaver) {
        this.dtxLogicWeaver = dtxLogicWeaver;
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
        return dtxLogicWeaver.runTransaction(dtxInfo, methodInvocation::proceed);
    }
}
