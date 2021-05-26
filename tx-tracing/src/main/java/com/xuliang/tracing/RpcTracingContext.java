package com.xuliang.tracing;

import com.xuliang.lcn.common.context.TransactionLocalContext;
import com.xuliang.lcn.common.context.TransactionLocalContextThreadLocal;
import org.springframework.util.StringUtils;

/**
 * @author xuliang
 * @desc: 分布式事物链路上下文
 */
public class RpcTracingContext {

    public static final String HEADER_KEY_GROUP_ID = "X-Group-ID";

    private RpcTracingContext() {

    }

    private static class InstanceHolder {
        public static final RpcTracingContext instance = new RpcTracingContext();
    }

    public static RpcTracingContext getInstance() {
        return InstanceHolder.instance;
    }


    public void build(RpcTransactionSetter setter) {
        TransactionLocalContext transactionInfo = TransactionLocalContextThreadLocal.current();
        setter.set(HEADER_KEY_GROUP_ID, transactionInfo.getGroupId());
    }


    public void invoke(RpcTransactionGetter rpcTransactionGetter) {
        String groupId = rpcTransactionGetter.get(HEADER_KEY_GROUP_ID);
        TransactionLocalContext transactionInfo =  TransactionLocalContextThreadLocal.current();
        if (!StringUtils.isEmpty(groupId) && transactionInfo == null) {
            new TransactionLocalContext(groupId);
        }
    }


    /**
     * RpcTracingContext 信息设置器
     */
    public interface RpcTransactionSetter {
        /**
         * @param key   key
         * @param value value
         */
        void set(String key, String value);
    }

    /**
     * RpcTracingContext 信息获取器
     */
    public interface RpcTransactionGetter {
        /**
         * @param key key
         * @return tracing value
         */
        String get(String key);
    }

}
