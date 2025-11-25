package com.example.the_autumn.service;

import com.example.the_autumn.repository.TinNhanRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class ChatCleanupService {

    private final TinNhanRepository tinNhanRepo;

    public ChatCleanupService(TinNhanRepository tinNhanRepo) {
        this.tinNhanRepo = tinNhanRepo;
    }

    // run daily at 00:30 — archive or delete older than 90 days (example)
    @Scheduled(cron = "0 30 0 * * ?")
    public void cleanupOldMessages() {
        // implement deletion or move to archive table
        // example: findAll, filter date < limit and delete — but better to use SQL DELETE with date condition
    }
}
