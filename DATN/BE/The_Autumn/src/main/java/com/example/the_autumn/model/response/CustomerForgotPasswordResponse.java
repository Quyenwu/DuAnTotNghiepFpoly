package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CustomerForgotPasswordResponse {
    private boolean success;
    private String message;
    private String token;

    public static CustomerForgotPasswordResponse success(String message) {
        return new CustomerForgotPasswordResponse(true, message, null);
    }

    public static CustomerForgotPasswordResponse successWithToken(String message, String token) {
        return new CustomerForgotPasswordResponse(true, message, token);
    }

    public static CustomerForgotPasswordResponse error(String message) {
        return new CustomerForgotPasswordResponse(false, message, null);
    }
}