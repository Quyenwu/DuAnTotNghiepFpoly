package com.example.the_autumn.service;

import com.example.the_autumn.repository.ChatMessageRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatCleanupService {

    private final ChatMessageRepository chatMessageRepo;

    public ChatCleanupService(ChatMessageRepository chatMessageRepo) {
        this.chatMessageRepo = chatMessageRepo;
    }

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanupOldChats() {
        LocalDateTime limit = LocalDateTime.now().minusDays(30);
        chatMessageRepo.findAll().stream()
                .filter(c -> c.getCreatedAt().isBefore(limit))
                .forEach(chatMessageRepo::delete);
    }
}
