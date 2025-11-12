package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class BaseResponse {

    private Boolean isSuccess;
    private String message;
    private Object data;
}
