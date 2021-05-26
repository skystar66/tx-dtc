package com.xuliang.tracing.rpc.springmvc;

import com.xuliang.tracing.RpcTracingContext;
import com.xuliang.tracing.Tracings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Description:
 * Date: 19-1-28 下午4:59
 *
 * @author ujued
 */
@ConditionalOnClass(HandlerInterceptor.class)
@Component
@Slf4j
public class SpringTracingApplier implements HandlerInterceptor, WebMvcConfigurer {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("springMvc 拦截器 preHandler");
//        Tracings.apply(request::getHeader);
        RpcTracingContext.getInstance().invoke(request::getHeader);

        return true;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(this);
    }
}
