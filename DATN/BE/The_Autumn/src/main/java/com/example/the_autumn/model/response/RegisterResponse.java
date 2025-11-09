package com.example.the_autumn.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponse {
    private Boolean success;
    private String message;
    private String token;
    private String userType;
    private String hoTen;

    public RegisterResponse(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public RegisterResponse(Boolean success, String message, String token, String userType, String hoTen) {
        this.success = success;
        this.message = message;
        this.token = token;
        this.userType = userType;
        this.hoTen = hoTen;
    }

    public static RegisterResponse success(String message, String token, String userType, String hoTen) {
        return new RegisterResponse(true, message, token, userType, hoTen);
    }

    public static RegisterResponse error(String message) {
        return new RegisterResponse(false, message);
    }
}