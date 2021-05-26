package com.xuliang.tc.aspect;

import com.xuliang.tc.manager.XLTransactionManager;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * LCN 事务拦截器，注解方式
 * create by xuliang on 2018/1/5
 */
@Aspect
@Component
@Slf4j
public class TransactionAspect implements Ordered {


    @Autowired
    private XLTransactionManager XLTransactionManager;


    /**
     * TX Aspect LCN
     */
    @Pointcut("@annotation(com.xuliang.tc.annotation.LcnTransaction)")
    public void lcnTransactionPointcut() {
    }


    @Around("lcnTransactionPointcut()")
    public Object runWithLcnTransaction(ProceedingJoinPoint point) throws Throwable {
        //开始执行事务
        return XLTransactionManager.runTransaction(point::proceed);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
