package com.xuliang.lcn.service.transation;

import com.xuliang.lcn.core.storage.FastStorage;
import com.xuliang.lcn.core.storage.TransactionUnit;
import com.xuliang.lcn.service.RpcExecuteService;
import com.xuliang.lcn.support.MessageSender;
import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.RpcResponseState;
import com.xuliang.lcn.txmsg.params.JoinGroupParams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Description: 加入事务组
 * Company: CodingApi
 * Date: 2018/12/29
 *
 * @author xuliang
 */

@Slf4j
@Service("rpc_join-group")
public class JoinGroupExecuteService implements RpcExecuteService {
    @Autowired
    private FastStorage fastStorage;
    @Autowired
    RpcClient rpcClient;

    @Autowired
    MessageSender messageSender;

    @Override
    public void execute(RpcCmd transactionCmd) {

        try {
            //添加事务组参数
            JoinGroupParams joinGroupParams = transactionCmd.getMsg().loadBean(JoinGroupParams.class);
            log.info("unit:{} try join group:{}",
                    joinGroupParams.getUnitId(), transactionCmd.getMsg().getGroupId());
            //手动回滚时设置状态为回滚状态 0
            if (joinGroupParams.getTransactionState() == 0) {
                fastStorage.saveTransactionState(joinGroupParams.getGroupId(), 0);
            }
            TransactionUnit transactionUnit = new TransactionUnit();
            transactionUnit.setUnitId(joinGroupParams.getUnitId());
            transactionUnit.setUnitType(joinGroupParams.getUnitType());
            transactionUnit.setModId(rpcClient.getAppName(transactionCmd.getRemoteKey()));
            fastStorage.saveTransactionUnitToGroup(joinGroupParams.getGroupId(), transactionUnit);
            log.info("unit:{} join group:{} success joined.",
                    joinGroupParams.getUnitId(), joinGroupParams.getGroupId());
            messageSender.senderSuccess(transactionCmd, RpcResponseState.success);
        } catch (Exception ex) {
            log.error("action: rpc_join-group groupId:{} execute service error. error: {}",transactionCmd.getMsg().getGroupId(), ex);
            messageSender.senderFail(transactionCmd, ex);
        }
    }
}
