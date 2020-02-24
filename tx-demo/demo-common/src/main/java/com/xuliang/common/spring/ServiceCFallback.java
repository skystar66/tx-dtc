package com.xuliang.common.spring;

import com.xuliang.tc.core.DTXUserControls;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Date: 19-2-19 下午1:53
 *
 * @author ujued
 */
@Component
@Slf4j
public class ServiceCFallback implements ServiceCClient {

    @Override
    public String rpc(String name) {
        log.info("服务C 手动设置回滚状态 ");
        DTXUserControls.rollbackGroup(TracingContext.tracing().groupId());
        return "fallback";
    }
}
