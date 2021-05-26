package com.xuliang.tc.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/11/29
 *
 * @author xuliang
 */
@Data
@ConfigurationProperties(prefix = "tx-lcn.client")
@Component
public class TxClientConfig {


    /**
     * manager hosts
     */
    private List<String> managerAddress;


    /**
     * Distributed Transaction Time
     * 分布式事务执行最大时间
     */
    private long dtxTime;


    /**
     * TM RPC 超时时间
     */
    private long tmRpcTimeout;

    /**
     * aspect connection order
     */
    private int resourceOrder;


    /**
     * 调用链长度等级
     */
    private int chainLevel = 3;

    /**
     * 请求发送消息的等待时间(单位:毫秒)
     */
    private int awaitTime = 1000;

    public void applyDtxTime(long dtxTime) {
        setDtxTime(dtxTime);
    }

    public void applyTmRpcTimeout(long tmRpcTimeout) {
        setTmRpcTimeout(tmRpcTimeout);
    }
}





