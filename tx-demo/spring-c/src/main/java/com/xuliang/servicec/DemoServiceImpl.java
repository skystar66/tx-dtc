package com.xuliang.servicec;

import com.google.common.collect.Sets;
import com.xuliang.common.db.domain.Demo;
import com.xuliang.tc.annotation.LcnTransaction;
import com.xuliang.tracing.TracingContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

    private ConcurrentHashMap<String, Set<Long>> ids = new ConcurrentHashMap<>();

    @Autowired
    public DemoServiceImpl(DemoMapper demoMapper) {
        this.demoMapper = demoMapper;
    }

    @Override
    @LcnTransaction
    @Transactional
    public String rpc(String value) {
        Demo demo = new Demo();
        demo.setDemoField(value);
        demo.setCreateTime(new Date());
        demo.setAppName("c");
        demo.setGroupId(TracingContext.tracing().groupId());
        demoMapper.save(demo);
        ids.putIfAbsent(TracingContext.tracing().groupId(), Sets.newHashSet(demo.getId()));
        ids.get(TracingContext.tracing().groupId()).add(demo.getId());
        return "ok-service-c";
    }

}
