package com.example.the_autumn.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AIService {

    private final DeepSeekService deepSeekService;
    private final KnowledgeBaseService knowledgeBaseService;

    public AIService(DeepSeekService deepSeekService,
                     KnowledgeBaseService knowledgeBaseService) {
        this.deepSeekService = deepSeekService;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    public String ask(String message, Integer roomId) {
        if (message == null || message.isEmpty()) {
            return "Xin lỗi, tôi không hiểu câu hỏi của bạn.";
        }

        // Detect topic dựa trên câu hỏi
        String topic = knowledgeBaseService.detectTopic(message);

        // Lấy knowledge base theo topic
        Map<String, Object> kb = knowledgeBaseService.getKnowledgeBase(topic);

        // Gọi DeepSeekService để trả lời dựa trên KB
        return deepSeekService.getAnswer(message, kb);
    }
}
