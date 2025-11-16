package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.CustomerForgotPasswordRequest;
import com.example.the_autumn.model.request.CustomerResetPasswordRequest;
import com.example.the_autumn.model.response.CustomerForgotPasswordResponse;
import com.example.the_autumn.service.CustomerForgotPasswordService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/auth")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class CustomerForgotPasswordController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerForgotPasswordController.class);

    @Autowired
    private CustomerForgotPasswordService customerForgotPasswordService;

    @PostMapping("/forgot-password")
    public ResponseEntity<CustomerForgotPasswordResponse> forgotPassword(
            @Valid @RequestBody CustomerForgotPasswordRequest request) {
        try {
            logger.info("🔍 CUSTOMER_FORGOT_PASSWORD - Received request for email: {}", request.getEmail());

            String resetToken = customerForgotPasswordService.generateResetToken(request.getEmail());
            logger.info("🔍 CUSTOMER_FORGOT_PASSWORD - Generated token: {}", resetToken);

            // Luôn trả về success message để bảo mật
            if (resetToken != null) {
                logger.info("✅ CUSTOMER_FORGOT_PASSWORD - Email sent successfully to: {}", request.getEmail());
                return ResponseEntity.ok(CustomerForgotPasswordResponse.success(
                        "Nếu email tồn tại trong hệ thống, hướng dẫn reset mật khẩu sẽ được gửi đến email của bạn"
                ));
            } else {
                logger.info("⚠️ CUSTOMER_FORGOT_PASSWORD - Email not found or inactive: {}", request.getEmail());
                return ResponseEntity.ok(CustomerForgotPasswordResponse.success(
                        "Nếu email tồn tại trong hệ thống, hướng dẫn reset mật khẩu sẽ được gửi đến email của bạn"
                ));
            }
        } catch (Exception e) {
            logger.error("❌ CUSTOMER_FORGOT_PASSWORD - Error for email {}: {}", request.getEmail(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(CustomerForgotPasswordResponse.error(
                    "Lỗi xử lý yêu cầu. Vui lòng thử lại sau."
            ));
        }
    }

    @PostMapping("/reset-password")
    public ResponseEntity<CustomerForgotPasswordResponse> resetPassword(
            @Valid @RequestBody CustomerResetPasswordRequest request) {
        try {
            logger.info("🔍 CUSTOMER_RESET_PASSWORD - Received reset request for token: {}", request.getToken());

            // Validate password strength
            if (request.getNewPassword() == null || request.getNewPassword().length() < 6) {
                logger.warn("❌ CUSTOMER_RESET_PASSWORD - Invalid password length");
                return ResponseEntity.badRequest().body(CustomerForgotPasswordResponse.error(
                        "Mật khẩu phải có ít nhất 6 ký tự"
                ));
            }

            boolean success = customerForgotPasswordService.resetPassword(
                    request.getToken(),
                    request.getNewPassword()
            );

            if (success) {
                logger.info("✅ CUSTOMER_RESET_PASSWORD - Password reset successfully");
                return ResponseEntity.ok(CustomerForgotPasswordResponse.success(
                        "Đặt lại mật khẩu thành công. Bạn có thể đăng nhập với mật khẩu mới."
                ));
            } else {
                logger.warn("❌ CUSTOMER_RESET_PASSWORD - Invalid or expired token: {}", request.getToken());
                return ResponseEntity.badRequest().body(CustomerForgotPasswordResponse.error(
                        "Token không hợp lệ, đã hết hạn hoặc có lỗi xảy ra. Vui lòng thử lại."
                ));
            }
        } catch (Exception e) {
            logger.error("❌ CUSTOMER_RESET_PASSWORD - Error for token {}: {}", request.getToken(), e.getMessage(), e);
            return ResponseEntity.badRequest().body(CustomerForgotPasswordResponse.error(
                    "Lỗi đặt lại mật khẩu. Vui lòng thử lại sau."
            ));
        }
    }

    @GetMapping("/validate-reset-token")
    public ResponseEntity<CustomerForgotPasswordResponse> validateResetToken(@RequestParam String token) {
        try {
            logger.info("🔍 CUSTOMER_VALIDATE_TOKEN - Validating token: {}", token);

            boolean isValid = customerForgotPasswordService.isValidToken(token);

            if (isValid) {
                logger.info("✅ CUSTOMER_VALIDATE_TOKEN - Token is valid: {}", token);
                return ResponseEntity.ok(CustomerForgotPasswordResponse.success("Token hợp lệ"));
            } else {
                logger.warn("❌ CUSTOMER_VALIDATE_TOKEN - Token is invalid or expired: {}", token);
                return ResponseEntity.badRequest().body(CustomerForgotPasswordResponse.error("Token không hợp lệ hoặc đã hết hạn"));
            }
        } catch (Exception e) {
            logger.error("❌ CUSTOMER_VALIDATE_TOKEN - Error validating token {}: {}", token, e.getMessage(), e);
            return ResponseEntity.badRequest().body(CustomerForgotPasswordResponse.error("Lỗi xác thực token"));
        }
    }
}