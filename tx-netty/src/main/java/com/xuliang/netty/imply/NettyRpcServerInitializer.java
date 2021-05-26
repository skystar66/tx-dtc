package com.xuliang.netty.imply;

import com.xuliang.lcn.txmsg.RpcServerInitializer;
import com.xuliang.lcn.txmsg.dto.ManagerProperties;
import com.xuliang.netty.em.NettyType;
import com.xuliang.netty.handler.init.NettyRpcServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author ujued
 */
@Service
@Slf4j
public class NettyRpcServerInitializer implements RpcServerInitializer, DisposableBean {


    @Autowired
    private NettyRpcServerChannelInitializer nettyRpcServerChannelInitializer;


    private EventLoopGroup workerGroup;
    private NioEventLoopGroup bossGroup;


    @Override
    public void init(ManagerProperties managerProperties) {

        //设置为服务端模型
        NettyContext.nettyType = NettyType.server;
        NettyContext.params = managerProperties;
        nettyRpcServerChannelInitializer.setManagerProperties(managerProperties);
        int port = managerProperties.getRpcPort();
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    //1、ChannelOption.SO_BACKLOG
                    //ChannelOption.SO_BACKLOG对应的是tcp/ip协议listen函数中的backlog参数。函数listen(int socketfd, int backlog)用来初始化服务端可连接队列。
                    //服务端处理客户端连接请求是顺序处理的，所以同一时间只能处理一个客户端连接，多个客户端来的时候，服务端将不能处理的客户端连接请求放在队列中等待处理，backlog参数指定了队列的大小。
                    .option(ChannelOption.SO_BACKLOG, 100)
                    .option(ChannelOption.TCP_NODELAY, false)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(nettyRpcServerChannelInitializer);

            // Start the server.
            if (StringUtils.hasText(managerProperties.getRpcHost())) {
                b.bind(managerProperties.getRpcHost(), managerProperties.getRpcPort());
            } else {
                b.bind(port);
            }
            log.info("Netty Socket Success Started Server:{}:{} ",
                    StringUtils.hasText(managerProperties.getRpcHost()) ? managerProperties.getRpcHost() : "0.0.0.0", port);

        } catch (Exception e) {
            // Shut down all event loops to terminate all threads.
            e.printStackTrace();
        }

    }

    /**
     * 销毁线程组
     */
    @Override
    public void destroy() throws Exception {
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }

        log.info("server stopped success!!!");
    }
}
