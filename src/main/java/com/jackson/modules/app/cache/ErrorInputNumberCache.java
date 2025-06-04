package com.jackson.modules.app.cache;

import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * 缓存输入错误次数
 */
@CacheConfig(cacheNames = {ErrorInputNumberCache.CACHE_NAME})
@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
@Component
@AllArgsConstructor
public class ErrorInputNumberCache {
    static final String CACHE_NAME = "errorNumber";
    private final CacheManager cacheManager;
    private final Object deleteLock = new Object();

    /**
     * @param userId   String
     * @param entity 验证码
     * @return 验证码
     */
    @CachePut(key = "#userId")
    public CacheModel put(String userId, CacheModel entity) {
        return entity;
    }

    @CacheEvict(key = "#userId")
    public void delete(String userId) {
    }

    public CacheModel get(String userId) {
        Cache cache = cacheManager.getCache(CACHE_NAME);
        if (cache == null) { //整个Cache是Null，直接返回
            return null;
        }
        Object nativeCache = cache.getNativeCache();
        ConcurrentMap<String, CacheModel> map = (ConcurrentMap) nativeCache;
        CacheModel model = map.get(userId);
        if (model == null) { //当前Key的Cache是Null，返回
            return null;
        }

        // 已过期， 直接返回
        if (model.getExpireTime() < System.currentTimeMillis()) {
            // 删除此 key
            cache.evict(userId);
            return null;
        }

        // 未过期，删除所有过期的 key
        synchronized (deleteLock) {
            List<String> keyList = map.values().stream().filter(Objects::nonNull)
                    .filter(obj -> obj.getExpireTime() < System.currentTimeMillis())
                    .map(CacheModel::getUserId).collect(Collectors.toList());
            keyList.forEach(cache::evict);
        }
        return model;
    }
}