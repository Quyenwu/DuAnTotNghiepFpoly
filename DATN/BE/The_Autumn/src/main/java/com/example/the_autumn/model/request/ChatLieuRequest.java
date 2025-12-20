package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatLieuRequest {

    private Integer id;

    private String maChatLieu;

    @NotBlank(message = "Tên chất liệu không được để trống")
    private String tenChatLieu;

    private Boolean trangThai;
}
