package com.xuliang.netty.handler;

import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.listener.RpcConnectionListener;
import com.xuliang.netty.dto.NettyRpcCmd;
import com.xuliang.netty.manager.SocketManager;
import com.xuliang.netty.util.SnowflakeIdWorker;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Description:
 * Company: api
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@ChannelHandler.Sharable
@Slf4j
@Component
public class SocketManagerInitHandler extends ChannelInboundHandlerAdapter {

    private RpcCmd heartCmd;


//    @Autowired
//    private RpcConnectionListener rpcConnectionListener;


    /**
     * 构造心跳消息
     */

    public SocketManagerInitHandler() {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(MessageConstants.ACTION_HEART_CHECK);
        heartCmd = new NettyRpcCmd();
        heartCmd.setMsg(messageDto);
        heartCmd.setKey(MessageConstants.ACTION_HEART_CHECK + SnowflakeIdWorker.getInstance().nextId());
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("stocketManager add channel  remote address : {}", ctx.channel().remoteAddress().toString());
        SocketManager.getInstance().addChannel(ctx.channel());

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        String removeKey = ctx.channel().remoteAddress().toString();
//        String appName = SocketManager.getInstance().getModuleName(removeKey);
        log.info("stocketManager remove channel address : {}", ctx.channel().remoteAddress().toString());
        SocketManager.getInstance().removeChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        //心跳配置
        if (IdleStateEvent.class.isAssignableFrom(evt.getClass())) {
            IdleStateEvent event = (IdleStateEvent) evt;
            if (event.state() == IdleState.READER_IDLE) {
                ctx.writeAndFlush(heartCmd);
            }
        }
    }
}
