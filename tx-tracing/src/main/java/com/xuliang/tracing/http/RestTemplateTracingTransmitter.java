package com.xuliang.tracing.http;

import com.xuliang.tracing.Tracings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Objects;


/**
 * Description:
 * Date: 19-1-28 下午4:35
 *
 * @author xuliang
 */
@ConditionalOnClass(RestTemplate.class)
@Component
@Order
@Slf4j
public class RestTemplateTracingTransmitter implements ClientHttpRequestInterceptor {


    @Autowired
    public RestTemplateTracingTransmitter(@Autowired(required = false) List<RestTemplate> restTemplates) {
        if (Objects.nonNull(restTemplates)) {
            restTemplates.forEach(restTemplate -> {
                List<ClientHttpRequestInterceptor> interceptors = restTemplate.getInterceptors();
                interceptors.add(interceptors.size(), RestTemplateTracingTransmitter.this);
            });
        }
    }


    @Override
    public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes, ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
        log.info("调用 ribbon 远程接口服务 ");
        Tracings.transmit(httpRequest.getHeaders()::add);
        return clientHttpRequestExecution.execute(httpRequest, bytes);
    }
}
