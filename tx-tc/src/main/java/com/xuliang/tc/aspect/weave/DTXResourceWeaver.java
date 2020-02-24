package com.xuliang.tc.aspect.weave;


import com.xuliang.tc.core.context.DTXLocalContext;
import com.xuliang.tc.core.transaction.resource.TransactionResourceProxy;
import com.xuliang.tc.support.TxLcnBeanHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.util.Objects;

/**
 * Description:
 * Company: CodingApi
 * Date: 2018/12/2
 *
 * @author xuliang
 */
@Component
@Slf4j
public class DTXResourceWeaver {

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
