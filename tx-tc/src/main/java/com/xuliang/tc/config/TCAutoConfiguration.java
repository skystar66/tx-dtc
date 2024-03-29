package com.xuliang.tc.config;

import com.xuliang.lcn.common.runner.TxLcnApplicationRunner;
import com.xuliang.tc.core.commit.Commitor;
import com.xuliang.tc.core.strategy.TransactionCommitorStrategy;
import com.xuliang.tracing.TracingAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@Import({TracingAutoConfiguration.class})
public class TCAutoConfiguration {


    @Bean(destroyMethod = "shutdown")
    public ExecutorService executorService() {
        int coreSize = Runtime.getRuntime().availableProcessors() * 2;
        return new ThreadPoolExecutor(coreSize, coreSize, 0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>()) {
            @Override
            public void shutdown() {
                super.shutdown();
                try {
                    this.awaitTermination(10, TimeUnit.MINUTES);
                } catch (InterruptedException ignored) {
                }
            }
        };
    }


    /**
     * 自动初始化init 接口
     */
    @Bean
    public TxLcnApplicationRunner txLcnApplicationRunner(ApplicationContext applicationContext) {
        return new TxLcnApplicationRunner(applicationContext);
    }


    /**
     * 自动初始化所有Commitor
     */
    @Bean
    public TransactionCommitorStrategy transactionCommitorStrategy
    (@Autowired(required = false) List<Commitor> commitors) {
        return new TransactionCommitorStrategy(commitors);
    }

}
