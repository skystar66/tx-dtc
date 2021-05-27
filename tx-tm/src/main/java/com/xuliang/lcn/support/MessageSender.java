package com.xuliang.lcn.support;


import com.xuliang.lcn.server.MessageCreator;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.Serializable;

/**
 * 封装消息发送
 */
@Component
@Slf4j
public class MessageSender {


    @Autowired
    RpcClient rpcClient;


    /**
     * @Description:发送成功的消息
     * @Param: [rpcCmd, message]
     * @return: void
     * @Author: xl
     * @Date: 2021/5/27
     **/
    public void senderSuccess(RpcCmd rpcCmd, Serializable message) {
        MessageDto messageDto = MessageCreator.okResponse(message, rpcCmd.getMsg().getAction());
        //对需要响应信息的请求做出响应
        if (!StringUtils.isEmpty(rpcCmd.getKey())) {
            try {
                messageDto.setGroupId(rpcCmd.getMsg().getGroupId());
                rpcCmd.setMsg(messageDto);
                rpcClient.send(rpcCmd);
            } catch (Exception ignored) {
                log.error("rpcClient request is error:{}", ignored);
            }
        }
    }

    /**
     * @Description: 发送失败的消息
     * @Param: [rpcCmd, message]
     * @return: void
     * @Author: xl
     * @Date: 2021/5/27
     **/
    public void senderFail(RpcCmd rpcCmd, Exception message) {
        MessageDto messageDto = MessageCreator.serverException(rpcCmd.getMsg().getAction());
        //对需要响应信息的请求做出响应
        if (!StringUtils.isEmpty(rpcCmd.getKey())) {
            try {
                messageDto.setGroupId(rpcCmd.getMsg().getGroupId());
                rpcCmd.setMsg(messageDto);
                rpcClient.send(rpcCmd);
            } catch (Exception ignored) {
                log.error("rpcClient request is error:{}", ignored);

            }
        }


    }

}
