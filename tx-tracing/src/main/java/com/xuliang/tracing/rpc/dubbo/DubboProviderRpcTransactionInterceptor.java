package com.xuliang.tracing.rpc.dubbo;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.extension.Activate;
import com.alibaba.dubbo.rpc.*;
import com.xuliang.tracing.RpcTracingContext;

/**
 * @author zhanghonglong
 * @date 2020/10/28 11:01
 */
@Activate(group = Constants.PROVIDER)
public class DubboProviderRpcTransactionInterceptor implements Filter {
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcTracingContext.getInstance().invoke(invocation.getAttachments()::get);
        return invoker.invoke(invocation);
    }
}
