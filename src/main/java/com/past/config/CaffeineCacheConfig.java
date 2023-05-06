package com.past.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Caffeine缓存管理器 配置类
 */
@Configuration
public class CaffeineCacheConfig {

    /**
     * CacheManager实现
     * @return cacheManager
     */
    @Bean("cacheManager")
    public CacheManager cacheManager() {

        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();

        // 缓存集合
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(new CaffeineCache("type-with-goods-cache", Caffeine.newBuilder()
                //指定key下的最大存储数据量
                .maximumSize(1000)
                //过期策略：最后一次访问后 多久过期 1800秒
                .expireAfterAccess(1800, TimeUnit.SECONDS)
                .build()));
        simpleCacheManager.setCaches(caches);

        return simpleCacheManager;
    }

}
