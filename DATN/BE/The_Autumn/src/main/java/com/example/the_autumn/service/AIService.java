package com.example.the_autumn.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AIService {

    private final DeepSeekService deepSeekService;
    private final KnowledgeBaseService knowledgeBaseService;

    // Lưu thời gian hỏi và số lần hỏi mỗi room
    private final Map<Integer, Long> lastAskTime = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> askCount = new ConcurrentHashMap<>();
    private final long MIN_INTERVAL_MS = 3000; // 3 giây giữa 2 câu hỏi
    private final int MAX_ASK_PER_MIN = 20; // tối đa 20 câu / phút

    public AIService(DeepSeekService deepSeekService,
                     KnowledgeBaseService knowledgeBaseService) {
        this.deepSeekService = deepSeekService;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @Cacheable(value = "ai_answer_cache", key = "#topic + ':' + #message")
    public String ask(String message, Integer roomId) {
        if (message == null || message.isEmpty()) {
            return "Xin lỗi, tôi không hiểu câu hỏi của bạn.";
        }

        long now = System.currentTimeMillis();

        // Kiểm tra spam theo khoảng cách
        if (lastAskTime.containsKey(roomId) && now - lastAskTime.get(roomId) < MIN_INTERVAL_MS) {
            return "Bạn đang hỏi quá nhanh, vui lòng đợi 3 giây giữa 2 câu.";
        }

        // Kiểm tra số câu hỏi / phút
        askCount.putIfAbsent(roomId, 0);
        if (askCount.get(roomId) >= MAX_ASK_PER_MIN) {
            return "Bạn đã hỏi quá nhiều trong 1 phút, vui lòng đợi một chút.";
        }

        lastAskTime.put(roomId, now);
        askCount.put(roomId, askCount.get(roomId) + 1);

        // Detect topic
        String topic = knowledgeBaseService.detectTopic(message);

        // Lấy KB summary
        String kbSummary = knowledgeBaseService.getKBSummary(topic);

        // Gọi AI trả lời
        return deepSeekService.getAnswer(message, kbSummary);
    }
}
