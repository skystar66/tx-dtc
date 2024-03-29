//package com.xuliang.servicea;
//
//import com.xuliang.lcn.common.util.Transactions;
//import com.xuliang.tc.manager.TransactionManager;
//import org.springframework.aop.framework.autoproxy.BeanNameAutoProxyCreator;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
//import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.annotation.EnableTransactionManagement;
//import org.springframework.transaction.interceptor.TransactionInterceptor;
//
//import java.util.Properties;
//
///**
// * Description: AOP方式声明分布式事务示例。service b, service c 用的注解方式，注意区分，非必须如此配置，可以注解，也可以声明式
// * Date: 19-1-13 下午2:46
// *
// * @author ujued
// */
//@Configuration
//@EnableTransactionManagement
//public class AopTypeDTXConfiguration {
//
//    /**
//     * 本地事务配置
//     *
//     * @param transactionManager
//     * @return
//     */
//    @Bean
//    @ConditionalOnMissingBean
//    public TransactionInterceptor transactionInterceptor(PlatformTransactionManager transactionManager) {
//        Properties properties = new Properties();
//        properties.setProperty("*", "PROPAGATION_REQUIRED,-Throwable");
//        TransactionInterceptor transactionInterceptor = new TransactionInterceptor();
//        transactionInterceptor.setTransactionManager(transactionManager);
//        transactionInterceptor.setTransactionAttributes(properties);
//        return transactionInterceptor;
//    }
//
//
//    @Bean
//    public BeanNameAutoProxyCreator beanNameAutoProxyCreator() {
//        BeanNameAutoProxyCreator beanNameAutoProxyCreator = new BeanNameAutoProxyCreator();
//        /**需要调整优先级，分布式事务在前，本地事务在后。*/
//        beanNameAutoProxyCreator.setInterceptorNames("txLcnInterceptor", "transactionInterceptor");
//        beanNameAutoProxyCreator.setBeanNames("*Impl");
//        return beanNameAutoProxyCreator;
//    }
//}
