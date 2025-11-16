package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChatLieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ChatLieuRepository extends JpaRepository<ChatLieu,Integer> {

    @Query("SELECT cl FROM ChatLieu cl WHERE LOWER(cl.tenChatLieu) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<ChatLieu> findByNameContaining(String name);

    List<ChatLieu> findByTenChatLieuContainingIgnoreCase(String name);
}
