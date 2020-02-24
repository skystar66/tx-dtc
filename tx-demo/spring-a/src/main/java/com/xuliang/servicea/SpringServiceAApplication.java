package com.xuliang.servicea;

import com.xuliang.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@SpringBootApplication(scanBasePackages = {"com.xuliang","com.xuliang.common.spring","com.xuliang.tracing"})
@EnableDiscoveryClient
@EnableDistributedTransaction
public class SpringServiceAApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServiceAApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}