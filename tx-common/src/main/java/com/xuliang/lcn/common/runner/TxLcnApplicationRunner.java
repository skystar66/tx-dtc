package com.xuliang.lcn.common.runner;


import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ApplicationContext;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Description: TxLcn Runner
 * Company: CodingApi
 * Date: 2020/1/16
 *
 * @author xuliang
 * @desc:TxLcnApplicationRunner类实现了ApplicationRunner接口， 并实现了run方法，
 * springboot在启动后会自动调用实现了ApplicationRunner接口类的run方法1
 */
public class TxLcnApplicationRunner implements ApplicationRunner, DisposableBean {


    private final ApplicationContext applicationContext;

    //执行初始化顺序
    private List<TxLcnInitializer> initializers;

    @Autowired
    public TxLcnApplicationRunner(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void destroy() throws Exception {
        for (TxLcnInitializer txLcnInitializer : initializers) {
            txLcnInitializer.destroy();
        }
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {

        /**获取容器中实现了TxLcnInitializer接口的类，并排序*/
        //获取所有注册 TxLcnInitializer 的处理器
        Map<String, TxLcnInitializer> runnerMap =
                applicationContext.getBeansOfType(TxLcnInitializer.class);

        initializers = runnerMap.values().stream().sorted(Comparator.comparing(TxLcnInitializer::order))
                .collect(Collectors.toList());

        /**循环调用txLcnInitializer的init（）方法*/
        for (TxLcnInitializer initializer : initializers) {
            initializer.init();
        }
    }
}
