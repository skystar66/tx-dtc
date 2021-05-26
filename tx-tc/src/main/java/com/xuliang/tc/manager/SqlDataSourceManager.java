package com.xuliang.tc.manager;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import com.xuliang.tc.aspect.callback.ConnectionCallback;
import com.xuliang.tc.core.sqlconnect.sqlsource.TransactionResourceProxy;
import com.xuliang.tc.helper.TxLcnBeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Objects;

/**
 * Description:sql连接资源管理器
 * Company: CodingApi
 * Date: 2018/11/29
 *
 * @author xuliang
 */
@Slf4j
@Component
public class SqlDataSourceManager {


    @Autowired
    TxLcnBeanHelper txLcnBeanHelper;

    @Autowired
    TransactionResourceProxy transactionResourceProxy;

    public Object getConnection(ConnectionCallback connectionCallback) throws Throwable {

        //获取事物本地上下文
        TransactionLocalContext localContext = TransactionLocalContextThreadLocal.current();

        //设置为代理模式
        if (Objects.nonNull(localContext) && localContext.hasSqlProxy()) {
            //构造代理连接对象
            Connection connection = transactionResourceProxy.proxyConnection(connectionCallback);
            return connection;
        }
        //如果不是代理返回原始链接
        return connectionCallback.call();
    }


}
