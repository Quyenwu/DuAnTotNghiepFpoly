package com.example.the_autumn.controller;

import com.example.the_autumn.dto.ChatPayload;
import com.example.the_autumn.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class ChatWebSocketController {

    private final ChatService chatService;

    public ChatWebSocketController(ChatService chatService) {
        this.chatService = chatService;
    }

    // clients send to /app/chat.send
    @MessageMapping("/chat.send")
    public void onMessage(@Payload ChatPayload payload) {
        chatService.handleIncoming(payload);
    }
}
