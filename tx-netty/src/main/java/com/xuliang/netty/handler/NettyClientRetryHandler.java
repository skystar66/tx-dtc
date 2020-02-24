package com.xuliang.netty.handler;


import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.listener.ClientInitCallBack;
import com.xuliang.netty.dto.NettyRpcCmd;
import com.xuliang.netty.imply.NettyContext;
import com.xuliang.netty.imply.NettyRetryConnect;
import com.xuliang.netty.imply.NettyRpcClientInitializer;
import com.xuliang.netty.manager.SocketManager;
import com.xuliang.netty.util.SnowflakeIdWorker;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.ConnectException;
import java.net.SocketAddress;
import java.util.List;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/21
 *
 * @author xuliang
 * @desc:重试handler
 */
@Slf4j
@ChannelHandler.Sharable
@Component
public class NettyClientRetryHandler extends ChannelInboundHandlerAdapter {

    @Autowired
    private ClientInitCallBack clientInitCallBack;

    private int keepSize;

    private NettyRpcCmd heartCmd;

    @Autowired
    NettyRetryConnect retryConnect;

    /**
     * 构建心跳信息
     */
    public NettyClientRetryHandler() {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new NettyRpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(MessageConstants.ACTION_HEART_CHECK
                + SnowflakeIdWorker.getInstance().nextId());
//        this.clientInitCallBack = clientInitCallBack;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        keepSize = NettyContext.currentParam(List.class).size();
        /**连接成功后回调机制，
         * 作用：回调作用
         2.1、从服务端获取机器id、分布式事物超时时间、最大等待时间等参数（客户端不能配置这些参数需要以服务端为准）
         2.2、如果服务端启动的数量大于客户端配置的服务器数量，会通过回调使得客户端连接所有的服务端。
         2.3、向TM集群注册
         */
        clientInitCallBack.connected(ctx.channel().remoteAddress().toString());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        log.error("keepSize:{},nowSize:{}", keepSize, SocketManager.getInstance().currentSize());

        SocketAddress socketAddress = ctx.channel().remoteAddress();
        log.error("socketAddress:{} ", socketAddress);
        retryConnect.reConnect(socketAddress);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("NettyClientRetryHandler - exception . ", cause);

        if (cause instanceof ConnectException) {
            int size = SocketManager.getInstance().currentSize();
            Thread.sleep(1000 * 15);
            log.error("current size:{}  ", size);
            log.error("try connect tx-manager:{} ", ctx.channel().remoteAddress());
            retryConnect.reConnect(ctx.channel().remoteAddress());
        }
        //发送数据包检测是否断开连接.
        ctx.writeAndFlush(heartCmd);

    }
}
