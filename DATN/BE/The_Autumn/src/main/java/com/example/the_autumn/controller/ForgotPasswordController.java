package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ForgotPasswordRequest;
import com.example.the_autumn.model.request.ResetPasswordRequest;
import com.example.the_autumn.model.response.ForgotPasswordResponse;
import com.example.the_autumn.service.ForgotPasswordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class ForgotPasswordController {

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            String resetToken = forgotPasswordService.generateResetToken(request.getEmail());

            if (resetToken != null) {
                return ResponseEntity.ok(ForgotPasswordResponse.success(
                        "Nếu email tồn tại trong hệ thống, hướng dẫn reset mật khẩu sẽ được gửi đến email của bạn"
                ));
            } else {
                return ResponseEntity.ok(ForgotPasswordResponse.success(
                        "Nếu email tồn tại trong hệ thống, hướng dẫn reset mật khẩu sẽ được gửi đến email của bạn"
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ForgotPasswordResponse.error(
                    "Lỗi xử lý yêu cầu. Vui lòng thử lại sau."
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ForgotPasswordResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request) {
        try {
            if (request.getNewPassword().length() < 6) {
                return ResponseEntity.badRequest().body(ForgotPasswordResponse.error(
                        "Mật khẩu phải có ít nhất 6 ký tự"
                ));
            }

            boolean success = forgotPasswordService.resetPassword(
                    request.getToken(),
                    request.getNewPassword()
            );

            if (success) {
                return ResponseEntity.ok(ForgotPasswordResponse.success(
                        "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới."
                ));
            } else {
                return ResponseEntity.badRequest().body(ForgotPasswordResponse.error(
                        "Token không hợp lệ, đã hết hạn hoặc có lỗi xảy ra. Vui lòng thử lại."
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ForgotPasswordResponse.error(
                    "Lỗi đặt lại mật khẩu. Vui lòng thử lại sau."
            ));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<ForgotPasswordResponse> validateResetToken(@RequestParam String token) {
        try {
            boolean isValid = forgotPasswordService.isValidToken(token);

            if (isValid) {
                return ResponseEntity.ok(ForgotPasswordResponse.success("Token hợp lệ"));
            } else {
                return ResponseEntity.badRequest().body(ForgotPasswordResponse.error("Token không hợp lệ hoặc đã hết hạn"));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ForgotPasswordResponse.error("Lỗi xác thực token"));
        }
    }
}