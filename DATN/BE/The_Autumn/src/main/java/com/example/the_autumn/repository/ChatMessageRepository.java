package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    List<ChatMessage> findByPhongIdOrderByCreatedAtAsc(String phongId);
    ChatMessage findTopByPhongIdOrderByCreatedAtDesc(String phongId);

}
