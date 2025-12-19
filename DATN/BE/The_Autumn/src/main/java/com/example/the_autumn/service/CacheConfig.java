package com.example.the_autumn.service;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.cache.interceptor.SimpleKeyGenerator;
import org.springframework.cache.interceptor.KeyGenerator;
import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager(
                "knowledge_base",
                "knowledge_base_summary",
                "ai_answer_cache"
        );

        // Cấu hình cache với TTL ngắn hơn
        cacheManager.setCacheNames(Arrays.asList("knowledge_base", "knowledge_base_summary", "ai_answer_cache"));

        return cacheManager;
    }

    // Tạo key cho cache có thêm tham số forceRefresh
    @Bean
    public KeyGenerator customKeyGenerator() {
        return (target, method, params) -> {
            if (params.length > 0 && params[0] instanceof String) {
                return params[0]; // Chỉ dùng topic làm key
            }
            return SimpleKeyGenerator.generateKey(params);
        };
    }
}