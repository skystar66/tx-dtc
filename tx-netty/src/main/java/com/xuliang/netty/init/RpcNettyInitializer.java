package com.xuliang.netty.init;

import com.xuliang.lcn.common.runner.TxLcnInitializer;
import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.netty.content.RpcCmdContext;
import com.xuliang.netty.manager.SocketManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(0)
public class RpcNettyInitializer implements TxLcnInitializer {

    @Autowired
    private RpcConfig rpcConfig;

    @Override
    public void init() throws Exception {
        RpcCmdContext.getInstance().setRpcConfig(rpcConfig);
        SocketManager.getInstance().setRpcConfig(rpcConfig);
    }
}
