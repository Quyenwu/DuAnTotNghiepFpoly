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

    // ✅ ObjectMapper đã đăng ký JavaTimeModule
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

            // Chuyển knowledge base sang JSON
            String knowledgeJson = objectMapper.writeValueAsString(knowledgeBase);

            String systemPrompt = """
                    Bạn là trợ lý AI của cửa hàng thời trang The Autumn.
                    Hãy sử dụng dữ liệu thật từ hệ thống để trả lời khách hàng.
                    Dữ liệu sau đây là thông tin sản phẩm, khuyến mãi, khách hàng:
                    %s

                    Nguyên tắc:
                    - Nếu tìm thấy thông tin phù hợp → trả lời chi tiết, tự nhiên, thân thiện.
                    - Nếu không có dữ liệu phù hợp → nói: "Xin lỗi, tôi không tìm thấy dữ liệu phù hợp trong hệ thống."
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
            return "Xin lỗi, hiện tại hệ thống đang bận. Vui lòng thử lại sau ít phút nhé! " + e.getMessage();
        }
    }
}
