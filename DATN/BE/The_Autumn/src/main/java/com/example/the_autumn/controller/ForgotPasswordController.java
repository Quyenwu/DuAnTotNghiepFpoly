package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ForgotPasswordRequest;
import com.example.the_autumn.model.request.ResetPasswordRequest;
import com.example.the_autumn.model.response.ForgotPasswordResponse;
import com.example.the_autumn.service.ForgotPasswordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://192.203.4.118:5173"})
public class ForgotPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordController.class);

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<ForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request) {
        try {
            logger.info("🔍 FORGOT_PASSWORD - Received request for email: {}", request.getEmail());

            String resetToken = forgotPasswordService.generateResetToken(request.getEmail());
            logger.info("🔍 FORGOT_PASSWORD - Generated token: {}", resetToken);

            if (resetToken != null) {
                logger.info("✅ FORGOT_PASSWORD - Email sent successfully to: {}", request.getEmail());
                return ResponseEntity.ok(ForgotPasswordResponse.success(
                        "Nếu email tồn tại trong hệ thống, hướng dẫn reset mật khẩu sẽ được gửi đến email của bạn"
                ));
            } else {
                logger.info("⚠️ FORGOT_PASSWORD - Email not found or inactive: {}", request.getEmail());
                return ResponseEntity.ok(ForgotPasswordResponse.success(
                        "Nếu email tồn tại trong hệ thống, hướng dẫn reset mật khẩu sẽ được gửi đến email của bạn"
                ));
            }
        } catch (Exception e) {
            logger.error("❌ FORGOT_PASSWORD - Error for email {}: {}", request.getEmail(), e.getMessage(), e);
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