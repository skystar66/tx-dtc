package com.xuliang.tc.core;

import com.xuliang.lcn.txmsg.RpcClient;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.params.CleanGroupParams;
import com.xuliang.lcn.txmsg.params.JoinGroupParams;
import com.xuliang.lcn.txmsg.params.NotifyGroupParams;
import com.xuliang.lcn.txmsg.params.TxExceptionParams;
import com.xuliang.lcn.txmsg.utils.MessageUtils;
import com.xuliang.tc.config.TxClientConfig;
import com.xuliang.tc.helper.MessageCreatorHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TransactionMsgSenger {


    @Autowired
    private RpcClient rpcClient;

    @Autowired
    TxClientConfig clientConfig;


    /**
     * 创建事务组
     *
     * @param groupId groupId
     */
    public MessageDto createGroup(String groupId) throws Exception {
        String remoteKey = rpcClient.loadRemoteKey();
        //创建事物组
        return rpcClient.request(remoteKey, MessageCreatorHelper.createGroup(groupId), clientConfig.getAwaitTime());
    }


    /**
     * 通知事务组
     *
     * @param groupId          groupId
     * @param transactionState 分布式事务状态
     * @return dtx state
     */
    public MessageDto notifyGroup(String groupId, int transactionState) throws Exception {
        String remoteKey = rpcClient.loadRemoteKey();
        NotifyGroupParams notifyGroupParams = new NotifyGroupParams();
        notifyGroupParams.setGroupId(groupId);
        notifyGroupParams.setState(transactionState);
        //通知事务组 关闭
        MessageDto messageDto = rpcClient.request(remoteKey, MessageCreatorHelper.notifyGroup(notifyGroupParams),
                clientConfig.getTmRpcTimeout() * clientConfig.getChainLevel());
        // 成功清理发起方事务
        if (!MessageUtils.statusOk(messageDto)) {
            throw new Exception(messageDto.loadBean(Throwable.class));
        }
        return messageDto;
    }

    /**
     * 清理事务组
     *
     * @param groupId          groupId
     * @param transactionState 分布式事务状态
     * @return dtx state
     */
    public void cleanGroup(String groupId, int transactionState) throws Exception {
        String remoteKey = rpcClient.loadRemoteKey();
        CleanGroupParams cleanGroupParams = new CleanGroupParams();
        cleanGroupParams.setGroupId(groupId);
        cleanGroupParams.setState(transactionState);
        //通知事务组 关闭
        rpcClient.send(remoteKey, MessageCreatorHelper.cleanGroup(cleanGroupParams));
    }


    /**
     * 加入事务组
     *
     * @param groupId          groupId
     * @param unitId           事务单元标识
     * @param unitType         事务类型
     * @param transactionState 用户事务状态
     */
    public MessageDto joinGroup(String groupId, String unitId, String unitType, int transactionState) throws Exception {
        String remoteKey = rpcClient.loadRemoteKey();
        JoinGroupParams joinGroupParams = new JoinGroupParams();
        joinGroupParams.setGroupId(groupId);
        joinGroupParams.setUnitId(unitId);
        joinGroupParams.setUnitType(unitType);
        joinGroupParams.setTransactionState(transactionState);
        return rpcClient.request(remoteKey, MessageCreatorHelper.joinGroup(joinGroupParams), -1);
    }

    /**
     * 询问事务状态
     *
     * @param groupId groupId
     * @param unitId  unitId
     * @return 事务无状态 （0,1,-1）
     * @throws Exception Non TM
     */
    public int askTransactionState(String groupId, String unitId) throws Exception {

        String remoteKey = rpcClient.loadRemoteKey();
        MessageDto messageDto = rpcClient.request(remoteKey, MessageCreatorHelper.askTransactionState(groupId, unitId));
        if (MessageUtils.statusOk(messageDto)) {
            return messageDto.loadBean(Integer.class);
        }
        return -1;
    }

    /**
     * Manager 记录事务状态
     *
     * @param groupId   groupId
     * @param unitId    unitId
     * @param registrar registrar
     * @param state     transactionState
     */
    public void reportTransactionState(String groupId, String unitId, Short registrar, int state) throws Exception {

        TxExceptionParams txExceptionParams = new TxExceptionParams();
        txExceptionParams.setGroupId(groupId);
        txExceptionParams.setRegistrar(registrar);
        txExceptionParams.setTransactionState(state);
        txExceptionParams.setUnitId(unitId);
        String remoteKey = rpcClient.loadRemoteKey();
        rpcClient.request(remoteKey, MessageCreatorHelper.writeTxException(txExceptionParams));
    }


}
