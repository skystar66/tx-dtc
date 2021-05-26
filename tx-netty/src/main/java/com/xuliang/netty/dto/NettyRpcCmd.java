package com.xuliang.netty.dto;

import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.netty.content.RpcCmdContext;
import com.xuliang.netty.content.RpcContent;
import com.xuliang.netty.util.SnowflakeIdWorker;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;


/**
 * Description: nettyrpc 指令通讯
 * Company: wanbaApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@Slf4j
public class NettyRpcCmd extends RpcCmd {

    private volatile transient RpcContent rpcContent;

    public String randomKey() {
        String key = String.valueOf(SnowflakeIdWorker.getInstance().nextId());
        if (RpcCmdContext.getInstance().hasKey(key)) {
            randomKey();
        } else {
            rpcContent = RpcCmdContext.getInstance().addKey(key);
        }
        return key;
    }


    @Override
    public MessageDto loadResult() throws Exception {
        MessageDto messageDto = rpcContent.getRes();
        return messageDto;
    }

    public RpcContent loadRpcContent() {
        if (rpcContent == null) {
            rpcContent = RpcCmdContext.getInstance().getKey(getKey());
        }
        return rpcContent;

    }


    public void await() {
        if (Objects.nonNull(rpcContent.getRes())) {
            return;
        }
        rpcContent.await();
    }

    public void await(long timeout) {
        if (Objects.nonNull(rpcContent.getRes())) {
            return;
        }
        rpcContent.await(timeout);
    }


}
