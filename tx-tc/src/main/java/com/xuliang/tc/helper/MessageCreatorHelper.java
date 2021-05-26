package com.xuliang.tc.helper;

import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.params.*;

import java.io.Serializable;

/**
 * @author xuliang
 */
public class MessageCreatorHelper {


    /**
     * 成功
     *
     * @param message message
     * @param action  action
     * @return MessageDto
     */
    public static MessageDto okResponse(Serializable message, String action) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(action);
        messageDto.setState(MessageConstants.STATE_OK);
        messageDto.setData(message);
        return messageDto;
    }

    /**
     * 失败
     *
     * @param message message
     * @param action  action
     * @return MessageDto
     */
    public static MessageDto failResponse(Serializable message, String action) {
        MessageDto messageDto = new MessageDto();
        messageDto.setState(MessageConstants.STATE_EXCEPTION);
        messageDto.setAction(action);
        messageDto.setData(message);
        return messageDto;
    }

    /**
     * 初始化客户端请求
     *
     * @param appName appName
     * @return MessageDto
     */
    public static MessageDto initClient(String appName) {
        InitClientParams initClientParams = new InitClientParams();
        initClientParams.setAppName(appName);
        MessageDto messageDto = new MessageDto();
        messageDto.setData(initClientParams);
        messageDto.setAction(MessageConstants.ACTION_INIT_CLIENT);
        return messageDto;
    }

    /**
     * 创建事务组
     *
     * @param groupId groupId
     * @return MessageDto
     */
    public static MessageDto createGroup(String groupId) {
        MessageDto msg = new MessageDto();
        msg.setGroupId(groupId);
        msg.setAction(MessageConstants.ACTION_CREATE_GROUP);
        return msg;
    }
    /**
     * 关闭事务组
     *
     * @param notifyGroupParams notifyGroupParams
     * @return MessageDto
     */
    public static MessageDto notifyGroup(NotifyGroupParams notifyGroupParams) {
        MessageDto msg = new MessageDto();
        msg.setGroupId(notifyGroupParams.getGroupId());
        msg.setAction(MessageConstants.ACTION_NOTIFY_GROUP);
        msg.setData(notifyGroupParams);
        return msg;
    }

    /**
     * 关闭事务组
     *
     * @param cleanGroupParams cleanGroupParams
     * @return MessageDto
     */
    public static MessageDto cleanGroup(CleanGroupParams cleanGroupParams) {
        MessageDto msg = new MessageDto();
        msg.setGroupId(cleanGroupParams.getGroupId());
        msg.setAction(MessageConstants.ACTION_CLEAN_GROUP);
        msg.setData(cleanGroupParams);
        return msg;
    }

    /**
     * 加入事务组
     *
     * @param joinGroupParams joinGroupParams
     * @return MessageDto
     */
    public static MessageDto joinGroup(JoinGroupParams joinGroupParams) {
        MessageDto msg = new MessageDto();
        msg.setGroupId(joinGroupParams.getGroupId());
        msg.setAction(MessageConstants.ACTION_JOIN_GROUP);
        msg.setData(joinGroupParams);
        return msg;
    }


    /**
     * 询问事务状态指令
     *
     * @param groupId groupId
     * @param unitId  unitId
     * @return MessageDto
     */
    public static MessageDto askTransactionState(String groupId, String unitId) {
        MessageDto messageDto = new MessageDto();
        messageDto.setGroupId(groupId);
        messageDto.setAction(MessageConstants.ACTION_ASK_TRANSACTION_STATE);
        messageDto.setData(new AskTransactionStateParams(groupId, unitId));
        return messageDto;
    }

    /**
     * 写异常信息指令
     *
     * @param txExceptionParams txExceptionParams
     * @return MessageDto
     */
    public static MessageDto writeTxException(TxExceptionParams txExceptionParams) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(MessageConstants.ACTION_WRITE_EXCEPTION);
        messageDto.setGroupId(txExceptionParams.getGroupId());
        messageDto.setData(txExceptionParams);
        return messageDto;
    }





}
