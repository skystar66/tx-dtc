package com.xuliang.netty.imply;

import com.xuliang.lcn.txmsg.RpcClientInitializer;
import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.lcn.txmsg.dto.TxManagerHost;
import com.xuliang.netty.em.NettyType;
import com.xuliang.netty.handler.init.NettyRpcClientChannelInitializer;
import com.xuliang.netty.manager.SocketManager;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@Component
@Slf4j
public class NettyRpcClientInitializer implements RpcClientInitializer, DisposableBean {


    @Autowired
    private NettyRpcClientChannelInitializer nettyRpcClientChannelInitializer;
    @Autowired
    private RpcConfig rpcConfig;

    private EventLoopGroup workerGroup;


    @Override
    public void init(List<TxManagerHost> hosts, boolean sync) {
        NettyContext.nettyType = NettyType.client;
        NettyContext.params = hosts;
        workerGroup = new NioEventLoopGroup();
        for (TxManagerHost host : hosts) {
            Optional<Future> future = connect(new InetSocketAddress(host.getHost(), host.getPort()));
            log.info("Success Connect TM Server Address : {}", host.getHost() + ":" + host.getPort());
            if (sync && future.isPresent()) {
                try {
                    future.get().get(10, TimeUnit.SECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    @Override
    public synchronized Optional<Future> connect(SocketAddress socketAddress) {
        for (int i = 0; i < rpcConfig.getReconnectCount(); i++) {
            if (SocketManager.getInstance().noConnect(socketAddress)) {
                try {
                    log.info("Try connect socket({}) - count {}", socketAddress, i + 1);
                    Bootstrap b = new Bootstrap();
                    b.group(workerGroup);
                    b.channel(NioSocketChannel.class);
                    b.option(ChannelOption.SO_KEEPALIVE, true);
                    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
                    b.handler(nettyRpcClientChannelInitializer);
                    return Optional.of(b.connect(socketAddress).syncUninterruptibly());
                } catch (Exception ex) {
                    log.warn("Connect socket({}) fail. {}ms latter try again.", socketAddress, rpcConfig.getReconnectDelay());
                    try {
                        /**重连间隔 默认：6s*/
                        Thread.sleep(rpcConfig.getReconnectDelay());
                    } catch (InterruptedException ex2) {
                        ex2.printStackTrace();
                    }
                }
            }
            // 忽略已连接的连接
            return Optional.empty();
        }
        log.warn("Finally, netty connection fail , socket is {}", socketAddress);
        //报警，该服务失效
        return Optional.empty();
    }


    @Override
    public void destroy() throws Exception {
        workerGroup.shutdownGracefully();
        log.info("RPC client was down.");
    }
}
