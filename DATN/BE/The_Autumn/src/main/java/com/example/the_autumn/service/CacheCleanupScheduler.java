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

    // 🚨 GIẢM TỪ 30 phút xuống 5 phút cho sản phẩm
    @Scheduled(fixedRate = 1 * 60 * 1000) // 5 phút thay vì 30 phút
    public void clearKnowledgeSummaryCache() {
        if (cacheManager.getCache("knowledge_base_summary") != null) {
            cacheManager.getCache("knowledge_base_summary").clear();
            System.out.println("🔄 Đã xóa cache knowledge_base_summary");
        }
    }

    // Xóa cache ai_answer_cache mỗi 10 phút
    @Scheduled(fixedRate =1 * 60 * 1000) // 10 phút
    public void clearAiAnswerCache() {
        if (cacheManager.getCache("ai_answer_cache") != null) {
            cacheManager.getCache("ai_answer_cache").clear();
        }
    }

    // 🚨 THÊM: Xóa cache chi tiết mỗi 5 phút
    @Scheduled(fixedRate = 1 * 60 * 1000) // 5 phút
    public void clearKnowledgeBaseCache() {
        if (cacheManager.getCache("knowledge_base") != null) {
            cacheManager.getCache("knowledge_base").clear();
            System.out.println("🔄 Đã xóa cache knowledge_base");
        }
    }

}