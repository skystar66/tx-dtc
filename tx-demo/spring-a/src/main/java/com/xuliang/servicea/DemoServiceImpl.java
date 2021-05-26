package com.xuliang.servicea;

import com.xuliang.tc.annotation.LcnTransaction;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.xuliang.common.db.domain.Demo;
import com.xuliang.common.spring.ServiceBClient;
import com.xuliang.common.spring.ServiceCClient;

import java.util.Date;
import java.util.Objects;

/**
 * Description:
 * Date: 2018/12/25
 *
 * @author ujued
 */
@Service
@Slf4j
public class DemoServiceImpl implements DemoService {

    private final DemoMapper demoMapper;

    private final ServiceBClient serviceBClient;

    private final ServiceCClient serviceCClient;

    private final RestTemplate restTemplate;

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper, ServiceBClient serviceBClient, ServiceCClient serviceCClient, RestTemplate restTemplate) {
        this.demoMapper = demoMapper;
        this.serviceBClient = serviceBClient;
        this.serviceCClient = serviceCClient;
        this.restTemplate = restTemplate;
    }

    @Override
    @LcnTransaction
    public String execute(String value, String exFlag) {
        // step1. call remote ServiceD
//        String dResp = serviceBClient.rpc(value);

        log.info(">>>>>>>>> start excute bussinees");

        String dResp = restTemplate.getForObject("http://127.0.0.1:12002/rpc?value=" + value, String.class);

        // step2. call remote ServiceE
        String eResp = serviceCClient.rpc(value);

        log.info("开始执行本地事物！！！");
        // step3. execute local transaction
        Demo demo = new Demo();
        demo.setGroupId(TracingContext.tracing().groupId());
        demo.setDemoField(value);
        demo.setCreateTime(new Date());
        demo.setAppName("");
        demoMapper.save(demo);

        // 置异常标志，DTX 回滚
        if (Objects.nonNull(exFlag)) {
            throw new IllegalStateException("by exFlag");
        }
        log.info(">>>>>>>>> end excute bussinees");
        return dResp + " > " + eResp + " > " + "ok-service-a";
    }
}
