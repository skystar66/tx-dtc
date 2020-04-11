package com.xuliang.lcn.utils;

public class RedisKeyUtils {


    //redis tm 服务列表 key
    public static final String REDIS_TM_LIST = "tm.instances";

    //redis 事务组 key
    public static final String REDIS_GROUP_PREFIX = "tm:group:";


    public static final String REDIS_TOKEN_PREFIX = "tm.token";

    //事务组状态
    public static final String REDIS_GROUP_STATE = REDIS_GROUP_PREFIX + "transactionState:";
}
