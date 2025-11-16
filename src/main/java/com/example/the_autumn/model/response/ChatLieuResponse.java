package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.ChatLieu;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLieuResponse {

    private Integer id;
    private String maChatLieu;
    private String tenChatLieu;
    private Boolean trangThai;

    public ChatLieuResponse(ChatLieu cl) {
        this.id = cl.getId();
        this.maChatLieu = cl.getMaChatLieu();
        this.tenChatLieu = cl.getTenChatLieu();
        this.trangThai = cl.getTrangThai();
    }
}
