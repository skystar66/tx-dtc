package com.xuliang.lcn.txmsg.dto;


import lombok.Data;

/**
 * Description:
 * Company: CodingApi
 * Date: 2020/1/21
 *
 * @author xuliang
 */
@Data
public class ManagerProperties {


    private String rpcHost;

    /**
     * 端口
     */
    private int rpcPort;

    /**
     * 心态检测时间(ms)
     */
    private long checkTime;


    /**
     * 其他参数
     */
    private transient Object params;


}
