package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.MessageSender;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.params.InitClientParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Description: 注册客户端信息
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author xuliang
 */
@Service(value = "rpc_init-client")
@Slf4j
public class InitClientService implements RpcExecuteService {


    @Autowired
    private RpcClient rpcClient;

    @Autowired
    private TxManagerConfig txManagerConfig;


    @Autowired
    private RpcConfig rpcConfig;


    @Autowired
    MessageSender messageSender;

    @Override
    public void execute(RpcCmd transactionCmd){
        InitClientParams initClientParams = transactionCmd.getMsg().loadBean(InitClientParams.class);
        log.info("Registered TC: {}", initClientParams.getAppName());
        try {
            rpcClient.bindAppName(transactionCmd.getRemoteKey(), initClientParams.getAppName(), null);
            // DTX Time and TM timeout.
            initClientParams.setDtxTime(txManagerConfig.getDtxTime());
            initClientParams.setTmRpcTimeout(rpcConfig.getWaitTime());
            messageSender.senderSuccess(transactionCmd, initClientParams);
        } catch (Exception e) {
            log.error("action: rpc_init-client groupId:{} execute service error. error: {}",transactionCmd.getMsg().getGroupId(), e);
            messageSender.senderFail(transactionCmd, e);
        }
    }
}
