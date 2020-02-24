package com.xuliang.lcn.txmsg;


import com.xuliang.lcn.txmsg.dto.AppInfo;
import com.xuliang.lcn.txmsg.dto.MessageDto;
import com.xuliang.lcn.txmsg.dto.RpcCmd;
import com.xuliang.lcn.txmsg.enums.RpcResponseState;
import com.xuliang.lcn.txmsg.loadbalance.RpcLoadBalance;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Description: 客户端api
 * Company: CodingApi
 * Date: 2020/2/10
 *
 * @author xuliang
 */
public abstract class RpcClient {


    //rpc 负载均衡选择器
    @Autowired
    RpcLoadBalance loadBalance;

    /**
     * 发送指令不需要返回数据，需要知道返回状态
     *
     * @param rpcCmd 指令内容
     * @return 指令状态
     * @throws RpcException 远程调用请求异常
     */

    public abstract RpcResponseState send(RpcCmd rpcCmd) throws Exception;


    /**
     * 发送指令不需要返回数据，需要知道返回的状态
     *
     * @param remoteKey 远程标识关键字
     * @param msg       指令内容
     * @return 指令状态
     * @throws RpcException 远程调用请求异常
     */
    public abstract RpcResponseState send(String remoteKey, MessageDto msg) throws Exception;

    /**
     * 发送请求并获取响应
     *
     * @param rpcCmd 指令内容
     * @return 响应指令数据
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(RpcCmd rpcCmd) throws Exception;


    /**
     * 发送请求并获取响应
     *
     * @param messageDto 指令内容
     * @return 响应指令数据
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(String remoteKey, MessageDto messageDto) throws Exception;



    /**
     * 发送请求并获取响应
     *
     * @param remoteKey 远程标识关键字
     * @param msg       请求内容
     * @param timeout   超时时间
     * @return 响应消息
     * @throws RpcException 远程调用请求异常
     */
    public abstract MessageDto request(String remoteKey, MessageDto msg, long timeout) throws Exception;


    /**
     * 获取一个远程标识关键字
     *
     * @return 远程标识关键字
     * @throws Exception 远程调用请求异常
     */
    public String loadRemoteKey() throws Exception {
        return loadBalance.getRemoteKey();
    }

    /**
     * 获取所有的远程连接对象
     *
     * @return 远程连接对象数组.
     */
    public abstract List<String> loadAllRemoteKey();

    /**
     * 获取模块远程标识
     *
     * @param moduleName 模块名称
     * @return 远程标识
     */
    public abstract List<String> remoteKeys(String moduleName);


    /**
     * 绑定模块名称
     *
     * @param remoteKey 远程标识
     * @param appName   应用名称
     * @param labelName  TC标识名称
     * @throws Exception   RpcException
     */
    public abstract void bindAppName(String remoteKey, String appName,String labelName) throws Exception;


    /**
     * 获取模块名称
     *
     * @param remoteKey 远程标识
     * @return 应用名称
     */
    public abstract String getAppName(String remoteKey);


    /**
     * 获取所有的模块信息
     *
     * @return 应用名称
     */
    public abstract List<AppInfo> apps();








}
