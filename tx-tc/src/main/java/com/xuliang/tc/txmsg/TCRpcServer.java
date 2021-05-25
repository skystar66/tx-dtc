package com.xuliang.tc.txmsg;

import com.xuliang.lcn.common.runner.TxLcnInitializer;
import com.xuliang.lcn.txmsg.RpcClientInitializer;
import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.lcn.txmsg.dto.TxManagerHost;
import com.xuliang.tc.config.TxClientConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


/**
 * Description:
 * Company: api
 * Date: 2018/12/10
 *
 * @author xuliang
 * @desc:客户端
 */
@Component
@Order(1)
public class TCRpcServer implements TxLcnInitializer {

    @Autowired
    private RpcClientInitializer rpcClientInitializer;


    @Autowired
    private TxClientConfig txClientConfig;

    @Autowired
    RpcConfig rpcConfig;


    @Override
    public void init() throws Exception {
        // rpc timeout (ms)
        if (rpcConfig.getWaitTime() <= 5) {
            rpcConfig.setWaitTime(1000);
        }
        // rpc client init.
        rpcClientInitializer.init(TxManagerHost.parserList(
                txClientConfig.getManagerAddress()), false);
    }

}
