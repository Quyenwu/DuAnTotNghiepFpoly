package com.example.the_autumn.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordResponse {
    private Boolean success;
    private String message;

    public ForgotPasswordResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public static ForgotPasswordResponse success(String message) {
        return new ForgotPasswordResponse(true, message);
    }

    public static ForgotPasswordResponse error(String message) {
        return new ForgotPasswordResponse(false, message);
    }
}