package com.xuliang.lcn.txmsg.loadbalance;


/**
 * @author xuliang
 */
public interface RpcLoadBalance {


    /**
     * 获取一个远程标识关键字
     *
     * @return 远程key
     * @throws RpcException 远程调用请求异常
     */
    String getRemoteKey() throws Exception;


}
