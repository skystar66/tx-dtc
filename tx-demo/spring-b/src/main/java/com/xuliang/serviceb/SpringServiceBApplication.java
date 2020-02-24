package com.xuliang.serviceb;

import com.xuliang.tc.config.EnableDistributedTransaction;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@SpringBootApplication(scanBasePackages = {"com.xuliang","com.xuliang.common.spring","com.xuliang.tracing"})
@EnableDiscoveryClient
@EnableDistributedTransaction
public class SpringServiceBApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringServiceBApplication.class, args);

    }
}
