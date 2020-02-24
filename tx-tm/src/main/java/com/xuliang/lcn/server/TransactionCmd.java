package com.xuliang.lcn.server;

import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.enums.LCNCmdType;
import lombok.Data;

@Data
public class TransactionCmd {


    /**
     * 业务状态
     */
    public LCNCmdType type;


    /**
     * 请求唯一key
     */
    public String requestKey;


    /**
     * 事务组id
     */
    private String groupId;


    /**
     * TxClient标识键
     */
    private String remoteKey;

    /**
     * 通讯数据
     */
    private MessageDto msg;


}
