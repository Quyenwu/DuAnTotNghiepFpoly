package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ChatRequest;
import com.example.the_autumn.service.DeepSeekService;
import com.example.the_autumn.service.KnowledgeBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private final DeepSeekService deepSeekService;
    private final KnowledgeBaseService knowledgeBaseService;

    public ChatBotController(DeepSeekService deepSeekService, KnowledgeBaseService knowledgeBaseService) {
        this.deepSeekService = deepSeekService;
        this.knowledgeBaseService = knowledgeBaseService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody ChatRequest request) {
        try {

            String topic = knowledgeBaseService.detectTopic(request.getMessage());


            Map<String, Object> filteredKnowledge = knowledgeBaseService.getKnowledgeBase(topic);


            String answer = deepSeekService.getAnswer(request.getMessage(), filteredKnowledge);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "reply", answer != null ? answer : "Xin lỗi, tôi không tìm thấy câu trả lời phù hợp.",
                    "topic", topic
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of(
                    "success", false,
                    "reply", "⚠️ Đã xảy ra lỗi trong quá trình xử lý chatbot.",
                    "error", e.getMessage()
            ));
        }
    }
}
