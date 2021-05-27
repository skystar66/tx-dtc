package com.xuliang.lcn.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.TxLcnManagerRpcBeanHelper;
import com.xuliang.lcn.txmsg.RpcAnswer;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.LCNCmdType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author xuliang
 */
@Service
@Slf4j
public class ServerRpcAnswer implements RpcAnswer, DisposableBean {

    private ExecutorService executorService;

    @Autowired
    private TxLcnManagerRpcBeanHelper rpcBeanHelper;

    @Autowired
    TxManagerConfig managerConfig;

    @PostConstruct
    public void init() {
        managerConfig.setConcurrentLevel(
                Math.max(Runtime.getRuntime().availableProcessors() * 5, managerConfig.getConcurrentLevel()));
        this.executorService = Executors.newFixedThreadPool(managerConfig.getConcurrentLevel(),
                new ThreadFactoryBuilder().setDaemon(false).setNameFormat("tm-rpc-service-%d").build());
    }


    /**
     * @Description: 服务端处理
     * @Param: [rpcCmd]
     * @return: void
     * @Author: xl
     * @Date: 2021/5/27
     **/
    @Override
    public void callback(RpcCmd rpcCmd) {
        executorService.submit(() -> {
            try {
                log.info("Server Receive Message : {}", rpcCmd);
                LCNCmdType lcnCmdType = LCNCmdType.parserCmd(rpcCmd.getMsg().getAction());
                /**根据操作类型获取具体的service*/
                RpcExecuteService rpcExecuteService = rpcBeanHelper.loadManagerService(lcnCmdType);
                rpcExecuteService.execute(rpcCmd);
            } catch (Exception e) {
                log.error("rpc execute service error. groupId:{} action:{} error:{} ", rpcCmd.getMsg().getGroupId(),
                        rpcCmd.getMsg().getAction(), e);
            }
        });
    }


    /**
     * 销毁
     */
    @Override
    public void destroy() throws Exception {
        this.executorService.shutdown();
        this.executorService.awaitTermination(6, TimeUnit.SECONDS);
    }
}
