package com.xuliang.tracing;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xuliang.lcn.common.util.Maps;
import com.xuliang.tracing.utils.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Description:
 * 1. {@code fields}为 {@code null}。发起方出现，未开始事务组
 * 2. {@code fields}不为空，fields.get(TracingConstants.GROUP_ID) 是 {@code empty}。参与方出现，未开启事务组。
 * Date: 19-1-28 下午4:21
 *
 * @author xuliang
 */
@Slf4j
public class TracingContext {


    private static ThreadLocal<TracingContext> tracingContextThreadLocal = new ThreadLocal<>();

    private Map<String, String> fields;

    private TracingContext() {

    }

    /**
     * 所有的字段
     *
     * @return fields
     */
    public Map<String, String> fields() {
        return this.fields;
    }

    public static TracingContext tracing() {
        if (tracingContextThreadLocal.get() == null) {
            tracingContextThreadLocal.set(new TracingContext());
        }
        return tracingContextThreadLocal.get();
    }

    /**
     * 初始化
     */
    public static void init(Map<String, String> initFields) {
        log.info("初始化 tracing 上下文信息=======");
        // return if null fields.
        if (Objects.isNull(initFields)) {
            log.warn("init tracingContext fail. null init fields.");
            return;
        }
        TracingContext tracingContext = tracing();
        if (Objects.isNull(tracingContext.fields)) {
            tracingContext.fields = new HashMap<>();
        }
        tracingContext.fields.putAll(initFields);
    }


    /**
     * 销毁当前线程Tracing信息
     */
    public void destroy() {
        if (Objects.nonNull(tracingContextThreadLocal.get())) {
            log.info("销毁当前线程Tracing信息");
            tracingContextThreadLocal.remove();
        }
    }

    /**
     * 判断是否有事务组
     *
     * @return result
     */
    public boolean hasGroup() {
        return Objects.nonNull(fields) && fields.containsKey(TracingConstants.GROUP_ID) &&
                StringUtils.hasText(fields.get(TracingConstants.GROUP_ID));
    }

    /**
     * 开启一个事物
     */
    public void beginTransactionGroup() {
        if (hasGroup()) {
            return;
        }
        //初始化事务组
        init(Maps.newHashMap(TracingConstants.GROUP_ID,
                String.valueOf(SnowflakeIdWorker.getInstance().nextId()),
                TracingConstants.APP_MAP, "{}"));
    }

    /**
     * 获取事务组ID
     *
     * @return groupId
     */
    public String groupId() {
        if (hasGroup()) {
            return fields.get(TracingConstants.GROUP_ID);
        }
//        raiseNonGroupException();
        return "";
    }


    /**
     * 添加App
     *
     * @param serviceId serviceId
     * @param address   address
     */
    public void addApp(String serviceId, String address) {
        if (hasGroup()) {
            JSONObject map = JSON.parseObject(this.fields.get(TracingConstants.APP_MAP));
            if (map.containsKey(serviceId)) {
                return;
            }
            map.put(serviceId, address);
            this.fields.put(TracingConstants.APP_MAP, JSON.toJSONString(map));
            return;
        }
//        raiseNonGroupException();
    }

    /**
     * String Type App map.
     *
     * @return appMap
     */
    public String appMapString() {
        if (hasGroup()) {
            String appMap = Optional.ofNullable(this.fields.get(TracingConstants.APP_MAP)).orElse("");
            log.debug("App map: {}", appMap);
            return appMap;
        }
//        raiseNonGroupException();
        return "{}";
    }

    /**
     * JSON Type App map.
     *
     * @return appMap
     */
    public JSONObject appMap() {
        return JSON.parseObject(appMapString());
    }

}





