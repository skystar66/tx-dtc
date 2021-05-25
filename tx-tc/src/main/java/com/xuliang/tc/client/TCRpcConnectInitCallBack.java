package com.xuliang.tc.client;

import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.listener.ClientInitCallBack;
import com.xuliang.lcn.txmsg.params.InitClientParams;
import com.xuliang.lcn.txmsg.utils.MessageUtils;
import com.xuliang.tc.cluster.AutoTMClusterEngine;
import com.xuliang.tc.config.TxClientConfig;
import com.xuliang.tc.txmsg.MessageCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author xuliang
 * @desc：客户端连接成功回调服务端类
 * @see TMSearcher
 */
@Component
@Slf4j
public class TCRpcConnectInitCallBack implements ClientInitCallBack {

    @Autowired
    RpcClient rpcClient;

    @Autowired
    TxClientConfig txClientConfig;

    @Autowired
    ConfigurableEnvironment environment;

    @Autowired
    AutoTMClusterEngine autoTMClusterEngine;

    /**
     * 当前模块名称
     */
    private String applicationName;

    @PostConstruct
    public void init() {
        String appName = environment.getProperty("spring.application.name");
        this.applicationName = StringUtils.hasText(appName) ? appName : "application";
    }

    @Override
    public void connected(String remoteKey) {
        new Thread(() -> {
            try {
                //todo 这一步完全多余，可以直接在创建连接时，进行注册！
                log.info("Send  Register message to TM[{}]", remoteKey);
                MessageDto msg = rpcClient.request(remoteKey, MessageCreator.initClient(applicationName), 5000);

                if (MessageUtils.statusOk(msg)) {
                    InitClientParams resParams = msg.loadBean(InitClientParams.class);
                    // 1. 设置DTX Time 、 TM RPC timeout 和 MachineId
                    txClientConfig.applyDtxTime(resParams.getDtxTime());
                    txClientConfig.applyTmRpcTimeout(resParams.getTmRpcTimeout());
                    // 2. 日志
                    log.info("Finally, determined dtx time is {}ms, tm rpc timeout is {} ms",
                            resParams.getDtxTime(), resParams.getTmRpcTimeout());
                    log.info("Send  Seacher TM Cluster message to TM[{}]", remoteKey);
                    // 3. 搜索TM 集群信息
                    autoTMClusterEngine.searchTMCluster(remoteKey);
                    return;
                }

            } catch (Exception ex) {
                log.error("TM[{}] exception. connect fail!，error {}", remoteKey, ex);
            }
        }).start();
    }

    @Override
    public void connectFail(String remoteKey) {

    }
}
