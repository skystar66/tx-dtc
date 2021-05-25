package com.xuliang.tc.manager;

import com.xuliang.tc.aspect.callback.ConnectionCallback;
import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.transaction.sqlresource.TransactionResourceProxy;
import com.xuliang.tc.support.TxLcnBeanHelper;
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
        DTXLocalContext localContext = DTXLocalContext.cur();

        //设置为代理模式
        if (Objects.nonNull(localContext) && localContext.isProxy()) {
            //构造代理连接对象
            Connection connection = transactionResourceProxy.proxyConnection(connectionCallback);
            log.info("proxy a sql connection: {}.", connection);
            return connection;
        }
        //如果不是代理返回原始链接
        return connectionCallback.call();
    }


}
