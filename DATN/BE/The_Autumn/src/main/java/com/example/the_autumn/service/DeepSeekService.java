package com.example.the_autumn.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class DeepSeekService {

    @Value("${deepseek.api.url}")
    private String deepseekApiUrl;

    @Value("${deepseek.api.key}")
    private String deepseekApiKey;

    @Value("${deepseek.api.model}")
    private String deepseekModel;

    private final ObjectMapper objectMapper;

    public DeepSeekService() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public String getAnswer(String question, Map<String, Object> knowledgeBase) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + deepseekApiKey);

            String knowledgeJson = objectMapper.writeValueAsString(knowledgeBase);

            String systemPrompt = """
                    Bạn là trợ lý AI của cửa hàng thời trang The Autumn.
                    Dữ liệu hiện có:
                    %s
                    Nguyên tắc:
                    - Nếu có dữ liệu → trả lời chi tiết, thân thiện.
                    - Nếu không → nói "Xin lỗi, tôi không tìm thấy theo yêu cầu, tôi sẽ kết nối nhân viên."
                    """.formatted(knowledgeJson);

            Map<String, Object> body = Map.of(
                    "model", deepseekModel,
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", question)
                    )
            );

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(deepseekApiUrl, request, Map.class);

            if (response.getBody() != null && response.getBody().get("choices") != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                return (String) message.get("content");
            }
            return "Xin lỗi, tôi không có câu trả lời phù hợp.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Xin lỗi, hệ thống bận. " + e.getMessage();
        }
    }
}
