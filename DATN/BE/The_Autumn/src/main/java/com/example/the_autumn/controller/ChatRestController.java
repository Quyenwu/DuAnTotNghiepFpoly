package com.example.the_autumn.controller;

import com.example.the_autumn.entity.ChatMessage;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatRestController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody ChatMessage message) {
        messagingTemplate.convertAndSend("/topic/" + message.getRoomId(), message);
        return "Đã gửi tin nhắn!";
    }
}
