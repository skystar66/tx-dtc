package com.xuliang.tc.core.sqlconnect.sqlsource;

import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import com.xuliang.tc.aspect.callback.ConnectionCallback;
import com.xuliang.tc.core.cache.TcCache;
import com.xuliang.tc.core.commit.ConnectionProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.util.Objects;


/**
 * @author xuluiang
 */
@Service
@Slf4j
public class LcnTransactionResourceProxy implements TransactionResourceProxy {

    @Override
    public Connection proxyConnection(ConnectionCallback connectionCallback) throws Throwable {
        String groupId = TransactionLocalContextThreadLocal.current().getGroupId();
        Connection connection = TcCache.getInstance().getTransactionSqlConnection(groupId);
        if (Objects.nonNull(connection)) {
            return connection;
        }
        ConnectionProxy connectionProxy = new ConnectionProxy(connectionCallback.call());
        TcCache.getInstance().setTransactionSqlConnection(groupId, connectionProxy);
        connectionProxy.setAutoCommit(false);
        return connectionProxy;
    }
}
