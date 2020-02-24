package com.xuliang.tc.aspect;


import com.xuliang.tc.aspect.weave.DTXResourceWeaver;
import com.xuliang.tc.config.TxClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.sql.Connection;

/**
 * create by lorne on 2018/1/5
 */
@Aspect
@Component
@Slf4j
public class DataSourceAspect implements Ordered {


    @Autowired
    private TxClientConfig txClientConfig;

    @Autowired
    private DTXResourceWeaver dtxResourceWeaver;


    /**
     * 环绕通知获取代理连接
     */
    @Around("execution(* javax.sql.DataSource.getConnection(..))")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        return dtxResourceWeaver.getConnection(() -> (Connection) point.proceed());
    }


    @Override
    public int getOrder() {
        return txClientConfig.getResourceOrder();
    }


}
