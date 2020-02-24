package com.xuliang.lcn.tm.txmsg;

import com.xuliang.lcn.common.runner.TxLcnInitializer;
import com.xuliang.lcn.common.runner.TxLcnRunnerOrders;
import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.lcn.txmsg.RpcServerInitializer;
import com.xuliang.lcn.txmsg.dto.ManagerProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TMRpcServer implements TxLcnInitializer {


    @Autowired
    TxManagerConfig txManagerConfig;

    @Autowired
    private RpcServerInitializer rpcServerInitializer;

    @Autowired
    private RpcConfig rpcConfig;


    @Override
    public void init() throws Exception {
        // 1. 配置
        if (rpcConfig.getWaitTime() <= 5) {
            rpcConfig.setWaitTime(1000);
        }
        if (rpcConfig.getAttrDelayTime() < 0) {
            //网络延迟时间 8s
            rpcConfig.setAttrDelayTime(txManagerConfig.getDtxTime());
        }

        // 2. 初始化RPC Server
        ManagerProperties managerProperties = new ManagerProperties();
        managerProperties.setCheckTime(txManagerConfig.getHeartTime());
        managerProperties.setRpcPort(txManagerConfig.getPort());
        managerProperties.setRpcHost(txManagerConfig.getHost());
        rpcServerInitializer.init(managerProperties);
    }

    @Override
    public int order() {
        return TxLcnRunnerOrders.MAX;
    }
}
