package com.xuliang.netty.handler;


import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.netty.content.RpcContent;
import com.xuliang.netty.dto.NettyRpcCmd;
import com.xuliang.netty.em.NettyType;
import com.xuliang.netty.imply.NettyContext;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

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
public class ReciveDataHandler extends SimpleChannelInboundHandler<NettyRpcCmd> {


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, NettyRpcCmd cmd) throws Exception {

        String key = cmd.getKey();
        log.info("recive->cmd->{}", cmd);
        //心态数据包直接响应
        if (cmd.getMsg() != null &&
                MessageConstants.ACTION_HEART_CHECK.equals(cmd.getMsg().getAction())) {
            if (NettyContext.currentType().equals(NettyType.client)) {
                //设置值
                ctx.writeAndFlush(cmd);
                return;
            }
            return;
        }
        //需要响应的数据包
        if (!StringUtils.isEmpty(key)) {
            RpcContent rpcContent = cmd.loadRpcContent();
            if (rpcContent != null) {
                rpcContent.setRes(cmd.getMsg());
                rpcContent.signal();
            } else {
                // 通知执行下一个InboundHandler
                ctx.fireChannelRead(cmd);
            }
        } else {
            // 通知执行下一个InboundHandler
            ctx.fireChannelRead(cmd);
        }

    }
}
