package com.example.the_autumn.controller;

import com.example.the_autumn.entity.ChatMessage;
import com.example.the_autumn.repository.ChatMessageRepository;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.time.LocalDateTime;
import java.util.List;
@Controller
public class ChatWebSocketController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepo;

    public ChatWebSocketController(SimpMessagingTemplate messagingTemplate,
                                   ChatMessageRepository chatMessageRepo) {
        this.messagingTemplate = messagingTemplate;
        this.chatMessageRepo = chatMessageRepo;
    }

    // Khách/nhân viên gửi tin nhắn
    @MessageMapping("/chat.send/{roomId}")
    public void sendMessage(@Payload ChatMessage msg) {
        msg.setCreatedAt(LocalDateTime.now());
        chatMessageRepo.save(msg);
        messagingTemplate.convertAndSend("/topic/chat/" + msg.getPhongId(), msg);
    }

    // Nhân viên join
    @MessageMapping("/chat.staff.join/{roomId}")
    public void staffJoin(@Payload ChatMessage msg) {
        msg.setCreatedAt(LocalDateTime.now());
        msg.setRole("admin");
        msg.setNoiDung("Nhân viên đã tham gia phòng");
        chatMessageRepo.save(msg);

        // Push lịch sử
        List<ChatMessage> history = chatMessageRepo.findByPhongIdOrderByCreatedAtAsc(msg.getPhongId());
        history.forEach(m -> messagingTemplate.convertAndSend("/topic/chat/" + msg.getPhongId(), m));
    }

    // Nhân viên leave
    @MessageMapping("/chat.staff.leave/{roomId}")
    public void staffLeave(@Payload ChatMessage msg) {
        msg.setCreatedAt(LocalDateTime.now());
        msg.setRole("admin");
        msg.setNoiDung("Nhân viên đã rời phòng");
        chatMessageRepo.save(msg);
        messagingTemplate.convertAndSend("/topic/chat/" + msg.getPhongId(), msg);
    }

    // AI gửi tin nhắn
    public void sendAIMessage(String roomId, String content) {
        ChatMessage aiMsg = new ChatMessage();
        aiMsg.setPhongId(roomId);
        aiMsg.setNguoiGui("AI");
        aiMsg.setRole("ai");
        aiMsg.setNoiDung(content);
        aiMsg.setCreatedAt(LocalDateTime.now());
        chatMessageRepo.save(aiMsg);
        messagingTemplate.convertAndSend("/topic/chat/" + roomId, aiMsg);
    }
}

