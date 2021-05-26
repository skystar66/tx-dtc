package com.xuliang.tracing.rpc.feign;


import com.xuliang.tracing.RpcTracingContext;
import com.xuliang.tracing.Tracings;
import feign.Feign;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Description:
 * Date: 19-1-28 下午3:47
 *
 * @author xuliang
 */
@ConditionalOnClass(Feign.class)
@Component
@Order
@Slf4j
public class FeignTracingTransmitter implements RequestInterceptor {


    @Override
    public void apply(RequestTemplate requestTemplate) {

        log.info("调用 feign 远程接口服务 ");
//        Tracings.transmit(requestTemplate::header);
        RpcTracingContext.getInstance().build(requestTemplate::header);


    }
}
