package com.xuliang.tc.aspect;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

@Data
public class TransactionInfo {

    /**
     * 事务执行器
     */
    private Class targetClazz;

    /**
     * 方法
     */
    private String method;

    /**
     * 参数值
     */
    private Object[] argumentValues;

    /**
     * 参数类型
     */
    private Class[] parameterTypes;

    /**
     * 方法字符串
     */
    private String methodStr;


    public JSONObject toJsonObject() {
        String json = JSON.toJSONString(this);
        return JSON.parseObject(json);
    }


}
