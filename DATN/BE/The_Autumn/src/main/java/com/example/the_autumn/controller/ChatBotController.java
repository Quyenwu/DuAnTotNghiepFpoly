package com.example.the_autumn.controller;

import com.example.the_autumn.entity.ChatMessage;
import com.example.the_autumn.model.request.ChatRequest;
import com.example.the_autumn.repository.ChatMessageRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.service.DeepSeekService;
import com.example.the_autumn.service.KnowledgeBaseService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private final DeepSeekService deepSeekService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final ChatMessageRepository chatMessageRepo;
    private final SimpMessagingTemplate messagingTemplate;
    private final KhachHangRepository khachHangRepo;

    public ChatBotController(DeepSeekService deepSeekService,
                             KnowledgeBaseService knowledgeBaseService,
                             ChatMessageRepository chatMessageRepo,
                             SimpMessagingTemplate messagingTemplate,
                             KhachHangRepository khachHangRepo) {
        this.deepSeekService = deepSeekService;
        this.knowledgeBaseService = knowledgeBaseService;
        this.chatMessageRepo = chatMessageRepo;
        this.messagingTemplate = messagingTemplate;
        this.khachHangRepo = khachHangRepo;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> ask(@RequestBody ChatRequest request) {
        try {
            String topic = knowledgeBaseService.detectTopic(request.getMessage());
            Map<String, Object> knowledge = knowledgeBaseService.getKnowledgeBase(topic);
            String answer = deepSeekService.getAnswer(request.getMessage(), knowledge);

            // Lấy tin nhắn AI cuối cùng trong room
            ChatMessage lastMsg = chatMessageRepo.findTopByPhongIdOrderByCreatedAtDesc(request.getRoomId());

            if (lastMsg != null && lastMsg.getRole().equals("ai")) {
                // Merge nếu tin nhắn trước là AI
                lastMsg.setNoiDung(lastMsg.getNoiDung() + "\n" + answer);
                lastMsg.setCreatedAt(LocalDateTime.now()); // cập nhật thời gian
                chatMessageRepo.save(lastMsg);
                messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), lastMsg);
            } else {
                // Tạo tin nhắn AI mới
                ChatMessage aiMsg = new ChatMessage();
                aiMsg.setPhongId(request.getRoomId());
                aiMsg.setNguoiGui("AI");
                aiMsg.setRole("ai");
                aiMsg.setNoiDung(answer);
                aiMsg.setCreatedAt(LocalDateTime.now());
                chatMessageRepo.save(aiMsg);
                messagingTemplate.convertAndSend("/topic/chat/" + request.getRoomId(), aiMsg);
            }

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "reply", answer,
                    "topic", topic
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "reply", "Hệ thống đang lỗi.",
                    "error", e.getMessage()
            ));
        }
    }

    @GetMapping("/rooms")
    public Map<String, Object> getRooms() {
        List<Map<String, String>> rooms = khachHangRepo.findAll().stream()
                .map(kh -> Map.of("roomId", kh.getMaKhachHang(), "name", kh.getHoTen()))
                .toList();
        return Map.of("rooms", rooms);
    }

    @GetMapping("/history/{roomId}")
    public List<ChatMessage> getChatHistory(@PathVariable String roomId) {
        return chatMessageRepo.findByPhongIdOrderByCreatedAtAsc(roomId);
    }

}
