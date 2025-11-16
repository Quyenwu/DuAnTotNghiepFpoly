package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ChatLieuRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.ChatLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat-lieu")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class ChatLieuController {

    @Autowired
    private ChatLieuService clService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(clService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody ChatLieuRequest chatLieuRq){
        clService.add(chatLieuRq);
        return new ResponseObject<>(null, "Thêm chất liệu thành công");
    }
}
