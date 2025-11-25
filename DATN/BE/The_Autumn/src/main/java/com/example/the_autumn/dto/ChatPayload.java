package com.example.the_autumn.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatPayload {
    private Integer roomId;
    private Integer guiTu; // 0-KH,1-NV,2-AI
    private String noiDung;
    private Integer changeType;
    private Integer nhanVienId;
}
