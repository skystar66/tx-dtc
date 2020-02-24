package com.xuliang.lcn.txmsg.listener;


/**
 * @author xuliang 2019/1/31
 */
public interface RpcConnectionListener {



    /**
     * 建立连接监听
     * @param remoteKey 远程key
     */
    void connect(String remoteKey);

    /**
     * 断开连接监听
     * @param remoteKey 远程key
     * @param appName   模块名称
     */
    void disconnect(String remoteKey,String appName);







}
