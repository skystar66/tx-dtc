package com.xuliang.lcn.txmsg.params;

import lombok.Data;

import java.io.Serializable;


/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author xuliang
 */
@Data
public class InitClientParams implements Serializable {


    /**
     * 模块名称
     */
    private String appName;


    /**
     * 分布式事务执行最大时间
     */
    private long dtxTime;

    /**
     * TM RPC 超时时间
     */
    private long tmRpcTimeout;


}
