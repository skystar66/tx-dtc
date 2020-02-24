package com.xuliang.lcn.txmsg.dto;


import com.alibaba.fastjson.JSON;
import lombok.Data;

import java.io.Serializable;

/**
 * Description: rpc 通讯 dto
 * Company: 玩吧
 * Date: 2018/12/10
 *
 * @author xuliang
 */
public abstract class RpcCmd implements Serializable {


    /**
     * 指令唯一标识
     * 当存在key时 需要对方唯一指令
     */
    private String key;

    /**
     * 请求的消息内容体
     */
    private MessageDto msg;

    /**
     * 远程标识关键字 netty 默认为 指定host
     * 默认分配 64457 个端口，形式为：ip:ranDomProt
     * todo remoteKey ??
     */
    private String remoteKey;


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public MessageDto getMsg() {
        return msg;
    }

    public void setMsg(MessageDto msg) {
        this.msg = msg;
    }

    public String getRemoteKey() {
        return remoteKey;
    }

    public void setRemoteKey(String remoteKey) {
        this.remoteKey = remoteKey;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

    /**
     * get rpc result
     *
     * @return result
     * @throws RpcException RpcException
     */

    public abstract MessageDto loadResult() throws Exception;


}
