package com.example.the_autumn.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class CacheCleanupScheduler {

    @Autowired
    private CacheManager cacheManager;

    // Xóa cache knowledge_base_summary mỗi 30 phút
    @Scheduled(fixedRate = 30 * 60 * 1000) // 30 phút
    public void clearKnowledgeSummaryCache() {
        if (cacheManager.getCache("knowledge_base_summary") != null) {
            cacheManager.getCache("knowledge_base_summary").clear();
        }
    }

    // Xóa cache ai_answer_cache mỗi 10 phút
    @Scheduled(fixedRate = 10 * 60 * 1000) // 10 phút
    public void clearAiAnswerCache() {
        if (cacheManager.getCache("ai_answer_cache") != null) {
            cacheManager.getCache("ai_answer_cache").clear();
        }
    }
}
