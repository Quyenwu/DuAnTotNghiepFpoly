package com.example.the_autumn.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {
    private Integer roomId; // numeric
    private String message;
    private Integer senderType; // optional: 0-KH,1-NV (FE can pass)
}
