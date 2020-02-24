package com.xuliang.tc.client;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.RpcAnswer;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.LCNCmdType;
import com.xuliang.lcn.txmsg.params.NotifyUnitParams;
import com.xuliang.tc.service.RpcExecuteService;
import com.xuliang.tc.support.TxLcnBeanHelper;
import com.xuliang.tc.txmsg.MessageCreator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;


/**
 * Description: TxClient对RPC命令回复
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
@Service
@Slf4j
public class ClientRpcAnswer implements RpcAnswer, DisposableBean {


    @Autowired
    private TxLcnBeanHelper transactionBeanHelper;

    @Autowired
    private RpcClient rpcClient;

    @Autowired
    private ExecutorService executorService;


    @PostConstruct
    public void init() {
        this.executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 5,
                new ThreadFactoryBuilder().setDaemon(false).setNameFormat("tc-rpc-service-%d").build());
    }


    @Override
    public void callback(RpcCmd rpcCmd) {

        executorService.submit(() -> {
            log.info("Client Receive Message: {}", rpcCmd);
            TransactionCmd transactionCmd = parser(rpcCmd);
            /**事务类型*/
            String transactionType = transactionCmd.getTransactionType();
            /**执行cmd 指令*/
            String action = transactionCmd.getMsg().getAction();
            RpcExecuteService executeService =
                    transactionBeanHelper.loadRpcExecuteService(transactionType, transactionCmd.getType());
            MessageDto messageDto = null;
            try {
                Serializable message = executeService.execute(transactionCmd);
                messageDto = MessageCreator.okResponse(message, action);
            } catch (Exception e) {
                log.error("message > execute error.", e);
                messageDto = MessageCreator.failResponse(e, action);
            } finally {
                if (Objects.nonNull(rpcCmd.getKey())) {
                    try {
                        rpcCmd.setMsg(messageDto);
                        rpcClient.send(rpcCmd);
                    } catch (Exception e) {
                        log.error("response request[{}] error. error message: {}", rpcCmd.getKey(), e.getMessage());
                    }
                }
            }
        });
    }

    public TransactionCmd parser(RpcCmd rpcCmd) {
        TransactionCmd transactionCmd = new TransactionCmd();
        transactionCmd.setGroupId(rpcCmd.getMsg().getGroupId());
        transactionCmd.setRequestKey(rpcCmd.getKey());
        transactionCmd.setType(LCNCmdType.parserCmd(rpcCmd.getMsg().getAction()));
        if (rpcCmd.getMsg().getAction().equals(MessageConstants.ACTION_NOTIFY_UNIT)) {
            NotifyUnitParams notifyUnitParams = rpcCmd.getMsg().loadBean(NotifyUnitParams.class);
            transactionCmd.setTransactionType(notifyUnitParams.getUnitType());
        }
        transactionCmd.setMsg(rpcCmd.getMsg());
        return transactionCmd;
    }


    @Override
    public void destroy() throws Exception {
        this.executorService.shutdown();
        this.executorService.awaitTermination(6, TimeUnit.SECONDS);
    }
}
