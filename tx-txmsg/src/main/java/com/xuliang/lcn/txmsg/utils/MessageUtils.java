package com.xuliang.lcn.txmsg.utils;


import com.xuliang.lcn.txmsg.MessageConstants;
import com.xuliang.lcn.txmsg.dto.MessageDto;

/**
 * Description:
 * Date: 2018/12/18
 *
 * @author xuliang
 */
public class MessageUtils {


    /**
     * 响应消息状态
     *
     * @param messageDto 请求对象
     * @return 响应状态
     */
    public static boolean statusOk(MessageDto messageDto) {
        return messageDto.getState() == MessageConstants.STATE_OK;
    }


}
