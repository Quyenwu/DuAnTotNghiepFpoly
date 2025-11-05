package com.example.the_autumn.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponse {
    private String token;
    private String userType;
    private String hoTen;
    private String message;
    private Boolean success;

    public LoginResponse(String token, String userType, String hoTen, String message, Boolean success) {
        this.token = token;
        this.userType = userType;
        this.hoTen = hoTen;
        this.message = message;
        this.success = success;
    }

    public static LoginResponse success(String token, String userType, String hoTen, String message) {
        return new LoginResponse(token, userType, hoTen, message, true);
    }

    public static LoginResponse error(String message) {
        return new LoginResponse(null, null, null, message, false);
    }
}