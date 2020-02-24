package com.xuliang.netty.handler;


import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.netty.content.RpcContent;
import com.xuliang.netty.dto.NettyRpcCmd;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageEncoder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author ujued
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class SendDataHandler extends MessageToMessageEncoder<NettyRpcCmd> {


    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, NettyRpcCmd cmd, List<Object> out) throws Exception {
        log.info("send->{}", cmd);
        out.add(cmd);
    }
}
