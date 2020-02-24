package com.xuliang.tc.core.transaction.resource;


import com.xuliang.tc.aspect.weave.ConnectionCallback;

import java.sql.Connection;

public interface TransactionResourceProxy {

    /**
     * 获取资源连接
     *
     * @param connectionCallback Connection提供者
     * @return Connection Connection
     * @throws Throwable Throwable
     */
    Connection proxyConnection(ConnectionCallback connectionCallback) throws Throwable;


}
