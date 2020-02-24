package com.xuliang.netty.handler.init;

import com.xuliang.lcn.txmsg.dto.ManagerProperties;
import com.xuliang.netty.handler.*;
import com.xuliang.netty.handler.protocol.ObjectSerializerDecoder;
import com.xuliang.netty.handler.protocol.ObjectSerializerEncoder;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author xuliang
 * @desc:客户端 handler 初始化
 */
@Component
public class NettyRpcClientChannelInitializer extends ChannelInitializer<Channel> {

    @Autowired
    private RpcAnswerHandler rpcAnswerHandler;


    @Autowired
    private NettyClientRetryHandler nettyClientRetryHandler;

    @Autowired
    private SocketManagerInitHandler socketManagerInitHandler;

    @Autowired
    private ReciveDataHandler reciveDataHandler;


    @Override
    protected void initChannel(Channel ch) throws Exception {


        ch.pipeline().addLast(new LengthFieldPrepender(4, false));
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE,
                0, 4, 0, 4));

        ch.pipeline().addLast(new ObjectSerializerEncoder());
        ch.pipeline().addLast(new ObjectSerializerDecoder());


        ch.pipeline().addLast(reciveDataHandler);
        ch.pipeline().addLast(new SendDataHandler());
        //断线重连的handler
        ch.pipeline().addLast(nettyClientRetryHandler);
        ch.pipeline().addLast(socketManagerInitHandler);
        ch.pipeline().addLast(rpcAnswerHandler);


    }
}
