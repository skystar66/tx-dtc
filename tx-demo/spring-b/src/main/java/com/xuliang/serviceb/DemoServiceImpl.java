package com.xuliang.serviceb;

import com.xuliang.common.db.domain.Demo;
import com.xuliang.lcn.common.util.Transactions;
import com.xuliang.tc.annotation.LcnTransaction;
import com.xuliang.tc.aspect.enums.DTXPropagation;
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
    @LcnTransaction(propagation = DTXPropagation.REQUIRED)
    @Transactional
    public String rpc(String value) {


        Demo demo = new Demo();
        demo.setGroupId(TracingContext.tracing().groupId());
        demo.setDemoField(value);
        demo.setAppName("aaa");
        demo.setCreateTime(new Date());
        demoMapper.save(demo);
        return "ok-service-b";
    }
}
