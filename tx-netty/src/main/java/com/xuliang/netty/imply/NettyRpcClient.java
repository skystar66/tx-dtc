package com.xuliang.netty.imply;

import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.AppInfo;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.RpcResponseState;
import com.xuliang.netty.dto.NettyRpcCmd;
import com.xuliang.netty.manager.SocketManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Description: netty rpc  client  通讯实现类
 * Company: wanbaApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@Component
@Slf4j
public class NettyRpcClient extends RpcClient {


    @Override
    public RpcResponseState send(RpcCmd rpcCmd) throws Exception {
        return SocketManager.getInstance().send(rpcCmd.getRemoteKey(), rpcCmd);
    }

    @Override
    public RpcResponseState send(String remoteKey, MessageDto msg) throws Exception {
        RpcCmd rpcCmd = new NettyRpcCmd();
        rpcCmd.setRemoteKey(remoteKey);
        rpcCmd.setMsg(msg);
        return send(rpcCmd);
    }

    @Override
    public MessageDto request(RpcCmd rpcCmd) throws Exception {
        return request0(rpcCmd, -1);
    }

    @Override
    public MessageDto request(String remoteKey, MessageDto messageDto) throws Exception {
        return request(remoteKey, messageDto, -1);
    }

    @Override
    public MessageDto request(String remoteKey, MessageDto msg, long timeout) throws Exception {
        long startTime = System.currentTimeMillis();
        NettyRpcCmd nettyRpcCmd = new NettyRpcCmd();
        String key = nettyRpcCmd.randomKey();
        nettyRpcCmd.setKey(key);
        nettyRpcCmd.setRemoteKey(remoteKey);
        nettyRpcCmd.setMsg(msg);
        MessageDto result = request0(nettyRpcCmd, timeout);
        log.info("cmd request used time: {} ms", System.currentTimeMillis() - startTime);
        return result;
    }

    private MessageDto request0(RpcCmd rpcCmd, long timeout) throws Exception {
        return SocketManager.getInstance().request(rpcCmd.getRemoteKey(), rpcCmd, timeout);
    }


    @Override
    public List<String> loadAllRemoteKey() {
        return SocketManager.getInstance().loadAllRemoteKey();
    }

    @Override
    public List<String> remoteKeys(String moduleName) {
        return SocketManager.getInstance().removeKeys(moduleName);
    }

    @Override
    public void bindAppName(String remoteKey, String appName, String labelName) throws Exception {
        SocketManager.getInstance().bindModuleName(remoteKey, appName,labelName);
    }

    @Override
    public String getAppName(String remoteKey) {
        return SocketManager.getInstance().getModuleName(remoteKey);
    }

    @Override
    public List<AppInfo> apps() {
        return SocketManager.getInstance().appInfos();
    }
}
