package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChatLieu;
import com.example.the_autumn.model.request.ChatLieuRequest;
import com.example.the_autumn.model.response.ChatLieuResponse;
import com.example.the_autumn.repository.ChatLieuRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatLieuService {

    @Autowired
    private ChatLieuRepository chatLieuRepo;

    public List<ChatLieuResponse> findAll() {
        return chatLieuRepo.findAll()
                .stream()
                .map(ChatLieuResponse::new)
                .toList();
    }

    public void add(ChatLieuRequest request) {
        ChatLieu chatLieu = MapperUtils.map(request, ChatLieu.class);
        chatLieu.setTrangThai(true);
        chatLieuRepo.save(chatLieu);
    }

    public List<ChatLieuResponse> findByName(String name) {
        return chatLieuRepo.findByTenChatLieuContainingIgnoreCase(name)
                .stream()
                .map(ChatLieuResponse::new)
                .collect(Collectors.toList());
    }

    public List<ChatLieuResponse> findByName2(String name) {
        return chatLieuRepo.findByNameContaining(name)
                .stream()
                .map(ChatLieuResponse::new)
                .collect(Collectors.toList());
    }
}
