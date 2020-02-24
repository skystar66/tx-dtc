package com.xuliang.lcn.server;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xuliang.lcn.config.TxManagerConfig;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.TxLcnManagerRpcBeanHelper;
import com.xuliang.lcn.txmsg.RpcAnswer;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.LCNCmdType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * @author xuliang
 */
@Service
@Slf4j
public class ServerRpcAnswer implements RpcAnswer, DisposableBean {


    @Autowired
    private RpcClient rpcClient;

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


    @Override
    public void callback(RpcCmd rpcCmd) {
        executorService.submit(() -> {
            try {
                log.info("Server Receive Message : {}", rpcCmd);
                TransactionCmd transactionCmd = parser(rpcCmd);
                /**获取当前消息的操作类型比如创建事物组、加入事务组等*/
                String action = transactionCmd.getMsg().getAction();
                /**根据操作类型获取具体的service*/
                RpcExecuteService rpcExecuteService = rpcBeanHelper.loadManagerService(transactionCmd.getType());
                MessageDto messageDto = null;
                try {
                    Serializable message = rpcExecuteService.execute(transactionCmd);
                    messageDto = MessageCreator.okResponse(message, action);
                } catch (Exception e) {
                    log.error("rpc execute service error. action: " + action, e);
                    messageDto = MessageCreator.failResponse(e, action);
                } finally {
                    //对需要响应信息的请求做出响应
                    if (!StringUtils.isEmpty(rpcCmd.getKey())) {
                        try {
                            messageDto.setGroupId(rpcCmd.getMsg().getGroupId());
                            rpcCmd.setMsg(messageDto);
                            rpcClient.send(rpcCmd);
                        } catch (Exception ignored) {
                        }
                    }
                }

            } catch (Exception ex) {
                if (!StringUtils.isEmpty(rpcCmd.getKey())) {
                    log.info("send response.");
                    String action = rpcCmd.getMsg().getAction();
                    // 事务协调器业务未处理的异常响应服务器失败
                    rpcCmd.setMsg(MessageCreator.serverException(action));
                    try {
                        rpcClient.send(rpcCmd);
                        log.info("send response ok.");
                    } catch (Exception ignored) {
                        log.error("requester:{} dead.", rpcCmd.getRemoteKey());
                    }
                }
            }
        });
    }

    private TransactionCmd parser(RpcCmd rpcCmd) {
        TransactionCmd transactionCmd = new TransactionCmd();
        transactionCmd.setGroupId(rpcCmd.getMsg().getGroupId());
        transactionCmd.setMsg(rpcCmd.getMsg());
        transactionCmd.setRemoteKey(rpcCmd.getRemoteKey());
        transactionCmd.setRequestKey(rpcCmd.getKey());
        transactionCmd.setType(LCNCmdType.parserCmd(rpcCmd.getMsg().getAction()));
        return transactionCmd;

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
