package com.xuliang.lcn.txmsg;


import com.xuliang.lcn.txmsg.dto.TxManagerHost;

import java.net.SocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Future;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/10
 *
 * @author xuliang
 */
public interface RpcClientInitializer {


    /**
     * message client init
     *
     * @param hosts manager host list
     * @param sync  is sync
     */
    void init(List<TxManagerHost> hosts, boolean sync);

    /**
     * 建立连接
     *
     * @param socketAddress 远程连接对象
     * @return future
     */
    Optional<Future> connect(SocketAddress socketAddress);

}
