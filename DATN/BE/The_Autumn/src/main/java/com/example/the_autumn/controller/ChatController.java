package com.example.the_autumn.controller;

import com.example.the_autumn.entity.ChatMessage;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage")
    public void receiveMessage(@Payload ChatMessage message) {
        // nếu roomId null => tạo default
        String room = message.getRoomId() != null ? message.getRoomId() : "default";
        // gửi tin nhắn đến room riêng
        messagingTemplate.convertAndSend("/topic/" + room, message);
    }
}

