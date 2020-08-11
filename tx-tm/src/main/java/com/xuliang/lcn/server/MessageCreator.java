package com.xuliang.lcn.server;


import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.params.NotifyConnectParams;
import com.xuliang.lcn.txmsg.params.NotifyUnitParams;

import java.io.Serializable;

/**
 * 消息创建器
 *
 * @author xuliang
 */
public class MessageCreator {




    /**
     * 正常响应
     *
     * @param action  action
     * @param message message
     * @return MessageDto
     */
    public static MessageDto okResponse(Serializable message, String action) {
        MessageDto messageDto = new MessageDto();
        messageDto.setState(MessageConstants.STATE_OK);
        messageDto.setAction(action);
        messageDto.setData(message);
        return messageDto;
    }

    /**
     * 失败响应
     *
     * @param action  action
     * @param message message
     * @return MessageDto
     */
    public static MessageDto failResponse(Serializable message, String action) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(action);
        messageDto.setState(MessageConstants.STATE_EXCEPTION);
        messageDto.setData(message);
        return messageDto;
    }


    /**
     * 服务器错误
     *
     * @param action action
     * @return MessageDto
     */
    public static MessageDto serverException(String action) {
        MessageDto messageDto = new MessageDto();
        messageDto.setAction(action);
        messageDto.setState(MessageConstants.STATE_EXCEPTION);
        return messageDto;
    }


    /**
     * 提交事务组
     *
     * @param notifyUnitParams notifyUnitParams
     * @return MessageDto
     */
    public static MessageDto notifyUnit(NotifyUnitParams notifyUnitParams) {
        MessageDto msg = new MessageDto();
        msg.setGroupId(notifyUnitParams.getGroupId());
        msg.setAction(MessageConstants.ACTION_NOTIFY_UNIT);
        msg.setData(notifyUnitParams);
        return msg;
    }

    /**
     * 通知TxClient连接
     *
     * @param notifyConnectParams notifyConnectParams
     * @return MessageDto
     */
    public static MessageDto newTxManager(NotifyConnectParams notifyConnectParams) {
        MessageDto msg = new MessageDto();
        msg.setAction(MessageConstants.ACTION_NEW_TXMANAGER);
        msg.setData(notifyConnectParams);
        return msg;
    }



}
