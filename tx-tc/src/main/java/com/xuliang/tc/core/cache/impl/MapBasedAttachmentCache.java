package com.xuliang.tc.core.cache.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.xuliang.tc.core.cache.AttachmentCache;
import com.xuliang.tc.utils.TransactionStatusConstants;
import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Description: 基于JDK线程安全的 {@code ConcurrentHashMap} 实现的 {@code AttachmentCache}
 * Date: 19-1-23 下午12:04
 *
 * @author ujued
 * @see AttachmentCache
 */
@Component
public class MapBasedAttachmentCache implements AttachmentCache {

//    private Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>(64);


    private static ExecutorService executorCaffinePool = Executors.newFixedThreadPool(8,
            new DefaultThreadFactory("pool-caffine-thread"));


    /**
     * cache 缓存
     */
    @Getter
    private static Cache<String, Map<String, Object>> cache;


    /**
     * cache 缓存
     */
    @Getter
    private static Cache<String, Object> singlePropCache;


    static {
        cache = Caffeine.newBuilder()
                .maximumSize(64)
                .executor(executorCaffinePool)
                //指定缓存在写入多久后失效。
                .expireAfterWrite(TransactionStatusConstants.WRITE_AFTER_EXPIRE_TIME, TimeUnit.SECONDS)
                .build();
        singlePropCache = Caffeine.newBuilder()
                .maximumSize(64)
                .executor(executorCaffinePool)
                //指定缓存在写入多久后失效。
                .expireAfterWrite(TransactionStatusConstants.WRITE_AFTER_EXPIRE_TIME, TimeUnit.SECONDS)
                .build();


    }


    @Override
    public void attach(String mainKey, String key, Object attachment) {

        Objects.requireNonNull(mainKey);
        Objects.requireNonNull(key);
        Objects.requireNonNull(attachment);

        Map<String, Object> attachMap = cache.get(mainKey, k -> {
            Map<String, Object> map = new HashMap<>();
            map.put(key, attachment);
            return map;
        });
        attachMap.put(key, attachment);
    }

    @Override
    public void attach(String key, Object attachment) {
        this.singlePropCache.put(key, attachment);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T attachment(String mainKey, String key) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(mainKey);

        Map<String, Object> map = cache.getIfPresent(mainKey);
        if (map != null) {
            Object obj = map.get(key);
            if (null != obj) {
                return (T) obj;
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T attachment(String key) {
        return (T) this.singlePropCache.getIfPresent(key);
    }

    @Override
    public void remove(String mainKey, String key) {

        Map<String, Object> map = cache.getIfPresent(mainKey);
        if (map != null) {
            map.remove(key);
        }

    }

    @Override
    public void removeAll(String mainKey) {
        cache.invalidate(mainKey);
    }

    @Override
    public boolean containsKey(String mainKey, String key) {

        Map<String, Object> map = cache.getIfPresent(mainKey);
        if (map != null) {
            if (map.containsKey(key)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsKey(String key) {
        return singlePropCache.getIfPresent(key) != null;
    }

    @Override
    public void remove(String key) {
        this.singlePropCache.invalidate(key);
    }
}
