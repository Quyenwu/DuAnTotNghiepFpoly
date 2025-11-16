package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.QuanHuyen;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LoginResponse {
    private Integer id;
    private String email;
    private String sdt;
    private String token;
    private String userType;
    private String hoTen;
    private List<QuanHuyen> quanHuyens;
    private String message;
    private Boolean success;

    // Constructor cũ (giữ nguyên để tương thích)
    public LoginResponse(String token, String userType, String hoTen, String message, Boolean success) {
        this.token = token;
        this.userType = userType;
        this.hoTen = hoTen;
        this.message = message;
        this.success = success;
    }

    // ✅ Constructor mới với đầy đủ thông tin
    public LoginResponse(Integer id, String email, String sdt, String token,
                         String userType, String hoTen, String message, Boolean success) {
        this.id = id;
        this.email = email;
        this.sdt = sdt;
        this.token = token;
        this.userType = userType;
        this.hoTen = hoTen;
        this.message = message;
        this.success = success;
    }

    // Method cũ (giữ nguyên)
    public static LoginResponse success(String token, String userType, String hoTen, String message) {
        return new LoginResponse(token, userType, hoTen, message, true);
    }

    public static LoginResponse success(Integer id, String email, String sdt,
                                        String token, String userType, String hoTen, String message) {
        return new LoginResponse(id, email, sdt, token, userType, hoTen, message, true);
    }
    public static LoginResponse error(String message) {
        return new LoginResponse(null, null, null, message, false);
    }
}