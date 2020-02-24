package com.xuliang.tc.core.transaction.resource;

import com.xuliang.tc.aspect.weave.ConnectionCallback;
import com.xuliang.tc.core.connect.proxy.LcnConnectionProxy;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.context.TCGlobalContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Objects;


/**
 * @author xuluiang
 */
@Service
@Slf4j
public class LcnTransactionResourceProxy implements TransactionResourceProxy {

    @Autowired
    private TCGlobalContext globalContext;


    @Override
    public Connection proxyConnection(ConnectionCallback connectionCallback) throws Throwable {
        String groupId = DTXLocalContext.cur().getGroupId();
        Connection connection = globalContext.getLcnConnection(groupId);
        if (Objects.nonNull(connection)) {
            return connection;
        }
        log.info("获取缓存 sql connect 连接资源失败！ 重新获取");
        LcnConnectionProxy lcnConnectionProxy = new LcnConnectionProxy(connectionCallback.call());
        globalContext.setLcnConnection(groupId, lcnConnectionProxy);
        lcnConnectionProxy.setAutoCommit(false);
        return lcnConnectionProxy;
    }
}