package com.example.the_autumn.model.response;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LoginResponse {
    private Integer chucVuId;
    private String chucVuName;
    private String email;
    private String matKhau;
    private String message;
    private Boolean success;
    private String accessToken;
    private String typeToken;
    public LoginResponse(String token, String userType, String hoTen, String message, Boolean success) {

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