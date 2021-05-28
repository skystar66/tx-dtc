package com.xuliang.serviceb;

import com.xuliang.common.db.domain.Demo;
import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import com.xuliang.tc.annotation.LcnTransaction;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

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

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }


    @Override
    public String rpcs(String value) {
        return rpc(value);
    }

    @Override
    @LcnTransaction
    @Transactional
    public String rpc(String value) {


        Demo demo = new Demo();
        demo.setGroupId(TransactionLocalContextThreadLocal.current().getGroupId());
        demo.setDemoField(value);
        demo.setAppName("transaction-B-model");
        demo.setCreateTime(new Date());
        demoMapper.save(demo);
        //故意抛出异常
//        if (true) {
//            throw new IllegalStateException("by exFlag");
//        }
        return "ok-service-b";
    }
}
