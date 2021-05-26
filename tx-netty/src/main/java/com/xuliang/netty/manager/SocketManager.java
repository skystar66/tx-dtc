package com.xuliang.netty.manager;


import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.lcn.txmsg.dto.AppInfo;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.RpcResponseState;
import com.xuliang.netty.dto.NettyRpcCmd;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.net.SocketAddress;
import java.util.*;
import java.util.concurrent.*;

/**
 * @desc：netty通道管理类
 */
@Slf4j
public class SocketManager {

    private Map<String, AppInfo> appNames;

    private ChannelGroup channelGroup;


    private static SocketManager manager = null;

    private long attrDelayTime = 1000 * 60;


    private ScheduledExecutorService executorService;


    public SocketManager() {
        appNames = new ConcurrentHashMap<>();

        channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


        executorService = Executors.newSingleThreadScheduledExecutor();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                executorService.awaitTermination(10, TimeUnit.MINUTES);
            } catch (InterruptedException ignored) {
            }
        }));
    }

    public static SocketManager getInstance() {
        if (manager == null) {
            synchronized (SocketManager.class) {
                if (manager == null) {
                    manager = new SocketManager();
                }
            }
        }
        return manager;
    }

    /**
     * 发送请求
     */
    public RpcResponseState send(String remoteKey, RpcCmd rpcCmd) {
        Channel channel = getChannel(remoteKey);
//        Future<V> sync() throws InterruptedException;//等待结果返回
//        Future<V> syncUninterruptibly();//等待结果返回，不能被中断
        ChannelFuture channelFuture = channel.writeAndFlush(rpcCmd).syncUninterruptibly();
        return channelFuture.isSuccess() ? RpcResponseState.success : RpcResponseState.fail;
    }


    /**
     * 发送请求 带返回结果
     */
    public MessageDto request(String key, RpcCmd cmd, long timeout) throws Exception {
        NettyRpcCmd nettyRpcCmd = (NettyRpcCmd) cmd;
        Channel channel = getChannel(key);
        channel.writeAndFlush(nettyRpcCmd);
        //阻塞结果
        if (timeout < 0) {
            //一直阻塞
            nettyRpcCmd.await();
        } else {
            nettyRpcCmd.await(timeout);
        }
        MessageDto messageDto = nettyRpcCmd.loadResult();
        nettyRpcCmd.loadRpcContent().clear();
        return messageDto;
    }


    /**
     * 根据key 获取channel通道
     */
    public Channel getChannel(String key) {
        for (Channel channel : channelGroup) {
            if (channel.remoteAddress().toString().equals(key)) {
                return channel;
            }
        }
        return null;
    }


    /**
     * 获取所有通道 remoteKey
     */
    public List<String> loadAllRemoteKey() {
        List<String> allKeys = new ArrayList<>();
        for (Channel channel : channelGroup) {
            allKeys.add(channel.remoteAddress().toString());
        }
        return allKeys;


    }

    /**
     * 获取模块的远程标识keys
     *
     * @param moduleName 模块名称
     * @return remoteKeys
     */
    public List<String> removeKeys(String moduleName) {
        List<String> allKeys = new ArrayList<>();
        for (Channel channel : channelGroup) {
            if (moduleName.equals(getModuleName(channel))) {
                allKeys.add(channel.remoteAddress().toString());
            }
        }
        return allKeys;
    }

    /**
     * 绑定连接数据
     *
     * @param remoteKey 远程标识
     * @param appName   模块名称
     * @param labelName TC标识名称
     */
    public void bindModuleName(String remoteKey, String appName, String labelName) throws Exception {
        AppInfo appInfo = new AppInfo();
        appInfo.setAppName(appName);
//        appInfo.setLabelName(labelName);
        appInfo.setCreateTime(new Date());
//        if (containsLabelName(labelName)) {
//            throw new Exception("labelName:" + labelName + " has exist.");
//        }
        appNames.put(remoteKey, appInfo);
    }

    public boolean containsLabelName(String moduleName) {
        Set<String> keys = appNames.keySet();
        for (String key : keys) {
            AppInfo appInfo = appNames.get(key);
            if (moduleName.equals(appInfo.getAppName())) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取模块名称
     *
     * @param channel 管道信息
     * @return 模块名称
     */
    public String getModuleName(Channel channel) {

        String key = channel.remoteAddress().toString();
        return getModuleName(key);

    }

    /**
     * 获取模块名称
     *
     * @param remoteKey 远程唯一标识
     * @return 模块名称
     */
    public String getModuleName(String remoteKey) {

        AppInfo appInfo = appNames.get(remoteKey);
        return appInfo == null ? null : appInfo.getAppName();

    }

    public void addChannel(Channel channel) {
        channelGroup.add(channel);
    }


    /**
     * 移除channel
     */
    public void removeChannel(Channel channel) {
        channelGroup.remove(channel);
        String key = channel.remoteAddress().toString();

        // 未设置过期时间，立即过期
        if (attrDelayTime < 0) {
            appNames.remove(key);
            return;
        }

        // 设置了过期时间，到时间后清除
        try {
            executorService.schedule(() -> {
                appNames.remove(key);
            }, attrDelayTime, TimeUnit.MILLISECONDS);
        } catch (RejectedExecutionException ignored) {
            // caused down server.
        }
    }


    public boolean noConnect(SocketAddress socketAddress) {
        for (Channel channel : channelGroup) {
            if (channel.remoteAddress().toString().equals(socketAddress.toString())) {
                return false;
            }
        }
        return true;
    }


    public void setRpcConfig(RpcConfig rpcConfig) {
        attrDelayTime = rpcConfig.getAttrDelayTime();
    }

    public List<AppInfo> appInfos() {
        return new ArrayList<>(appNames.values());
    }


    public int currentSize() {
        return channelGroup.size();
    }


    public ChannelGroup getChannelGroup() {
        return channelGroup;
    }

    public void setChannelGroup(ChannelGroup channelGroup) {
        this.channelGroup = channelGroup;
    }
}
