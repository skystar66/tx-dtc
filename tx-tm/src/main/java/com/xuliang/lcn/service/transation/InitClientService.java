package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.server.TransactionCmd;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.RpcConfig;
import com.xuliang.lcn.txmsg.params.InitClientParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;


/**
 * Description: 初始化客户端信息
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


    @Override
    public Serializable execute(TransactionCmd transactionCmd) throws Exception {
        InitClientParams initClientParams = transactionCmd.getMsg().loadBean(InitClientParams.class);
        log.info("Registered TC: {}", initClientParams.getAppName());
        try {
            rpcClient.bindAppName(transactionCmd.getRemoteKey(), initClientParams.getAppName(), null);
        } catch (Exception e) {
            throw new Exception(e);
        }
        // Machine len and id
//        initClientParams.setSeqLen(txManagerConfig.getSeqLen());
//        initClientParams.setMachineId(managerService.machineIdSync());
        // DTX Time and TM timeout.
        initClientParams.setDtxTime(txManagerConfig.getDtxTime());
        initClientParams.setTmRpcTimeout(rpcConfig.getWaitTime());
        // TM Name
//        initClientParams.setAppName(modIdProvider.modId());
        return initClientParams;
    }
}
