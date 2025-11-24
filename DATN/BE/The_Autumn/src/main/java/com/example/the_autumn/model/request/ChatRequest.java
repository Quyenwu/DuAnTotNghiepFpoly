package com.example.the_autumn.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatRequest {
    private String roomId;
    private String message;
}
