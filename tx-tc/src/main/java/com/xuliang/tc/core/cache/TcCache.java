package com.xuliang.tc.core.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xuliang.tc.core.commit.ConnectionProxy;
import com.xuliang.tc.utils.TransactionStatusConstants;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TcCache {

    private static class InstanceHolder {
        public static final TcCache instance = new TcCache();
    }

    public static TcCache getInstance() {
        return InstanceHolder.instance;
    }


    private static ExecutorService executorCaffinePool = Executors.newFixedThreadPool(8,
            new DefaultThreadFactory("pool-caffine-thread"));


    /**
     * cache 缓存
     */
    @Getter
    private static Cache<String, Object> tcCache;


    static {
        tcCache = Caffeine.newBuilder()
                .maximumSize(64)
                .executor(executorCaffinePool)
                //指定缓存在写入多久后失效。
                .expireAfterWrite(TransactionStatusConstants.WRITE_AFTER_EXPIRE_TIME, TimeUnit.SECONDS)
                .build();
    }


    /**
     * 获取sql代理资源
     */
    public ConnectionProxy getTransactionSqlConnection(String groupId) throws Exception {
        Object cacheObj = tcCache.getIfPresent(
                TransactionStatusConstants.SQL_CACHE_KEY_PREFIX + groupId
        );
        if (cacheObj != null) {
            return (ConnectionProxy) cacheObj;
        }
        return null;
    }

    /**
     * 设置sql代理资源
     */

    public void setTransactionSqlConnection(String groupId, ConnectionProxy connectionProxy) {
        tcCache.put(TransactionStatusConstants.SQL_CACHE_KEY_PREFIX + groupId, connectionProxy);
    }

    /**
     * 清理sql代理资源
     */
    public void cleanTransactionConnection(String groupId) {
        tcCache.invalidate(TransactionStatusConstants.SQL_CACHE_KEY_PREFIX + groupId);
    }


}
