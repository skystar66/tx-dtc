package com.xuliang.tc.core.context;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Description: 基于JDK线程安全的 {@code ConcurrentHashMap} 实现的 {@code AttachmentCache}
 * Date: 19-1-23 下午12:04
 *
 * @author ujued
 * @see AttachmentCache
 */
@Component
public class MapBasedAttachmentCache implements AttachmentCache {

    private Map<String, Map<String, Object>> cache = new ConcurrentHashMap<>(64);

    private Map<String, Object> singlePropCache = new ConcurrentHashMap<>(64);

    @Override
    public void attach(String mainKey, String key, Object attachment) {
        Objects.requireNonNull(mainKey);
        Objects.requireNonNull(key);
        Objects.requireNonNull(attachment);

        if (cache.containsKey(mainKey)) {
            Map<String, Object> map = cache.get(mainKey);
            map.put(key, attachment);
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put(key, attachment);
        cache.put(mainKey, map);
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

        if (cache.containsKey(mainKey)) {
            if (cache.get(mainKey).containsKey(key)) {
                return (T) cache.get(mainKey).get(key);
            }
        }
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T attachment(String key) {
        return (T) this.singlePropCache.get(key);
    }

    @Override
    public void remove(String mainKey, String key) {
        if (cache.containsKey(mainKey)) {
            cache.get(mainKey).remove(key);
        }
    }

    @Override
    public void removeAll(String mainKey) {
        this.cache.remove(mainKey);
    }

    @Override
    public boolean containsKey(String mainKey, String key) {
        return cache.containsKey(mainKey) && cache.get(mainKey).containsKey(key);
    }

    @Override
    public boolean containsKey(String key) {
        return singlePropCache.containsKey(key);
    }

    @Override
    public void remove(String key) {
        this.singlePropCache.remove(key);
    }
}
