package com.xuliang.tc.aspect;


import com.xuliang.lcn.common.util.Transactions;
import com.xuliang.tc.annotation.LcnTransaction;
import com.xuliang.tc.aspect.weave.DTXLogicWeaver;
import com.xuliang.tc.config.TxClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

/**
 * LCN 事务拦截器
 * create by xuliang on 2018/1/5
 */
@Aspect
@Component
@Slf4j
public class TransactionAspect implements Ordered {




    @Autowired
    private TxClientConfig txClientConfig;

    @Autowired
    private DTXLogicWeaver dtxLogicWeaver;


    /**
     * DTC Aspect (Type of LCN)
     */
    @Pointcut("@annotation(com.xuliang.tc.annotation.LcnTransaction)")
    public void lcnTransactionPointcut() {
    }




    @Around("lcnTransactionPointcut()")
    public Object runWithLcnTransaction(ProceedingJoinPoint point) throws Throwable {
        log.info("come in get around pointcut lcn tx");
        //根据ProceedingJoinPoint 从缓存中获取事务信息
        DTXInfo dtxInfo = DTXInfo.getFromCache(point);
        //获取注解
        LcnTransaction lcnTransaction = dtxInfo.getBusinessMethod().getAnnotation(LcnTransaction.class);
        //设置事务类型为LCN
        dtxInfo.setTransactionType("lcn");
        //设置事务传播行为，发起方是REQUIRED，参与方式是SUPPORTS
        dtxInfo.setDtxPropagation(lcnTransaction.propagation());
        //开始执行事务
        return dtxLogicWeaver.runTransaction(dtxInfo, point::proceed);
    }



    @Override
    public int getOrder() {
        return 0;
    }
}
