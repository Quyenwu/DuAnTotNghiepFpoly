package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.repository.KhachHangRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class CustomerForgotPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerForgotPasswordService.class);

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final ConcurrentMap<String, CustomerResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

    public String generateResetToken(String email) {
        KhachHang khachHang = khachHangRepository.findByEmail(email)
                .orElse(null);

        if (khachHang == null || !khachHang.getTrangThai()) {
            logger.warn("❌ Customer email not found or inactive: {}", email);
            return null;
        }

        String resetToken = UUID.randomUUID().toString();

        resetTokens.put(resetToken, new CustomerResetTokenInfo(
                email,
                System.currentTimeMillis() + (24 * 60 * 60 * 1000) // 24 hours
        ));

        try {
            emailService.sendCustomerResetPasswordEmail(email, resetToken);
            logger.info("✅ Reset password email sent to customer: {}", email);
            return resetToken;
        } catch (Exception e) {
            logger.error("❌ Failed to send reset password email to customer {}: {}", email, e.getMessage());
            resetTokens.remove(resetToken);
            return null;
        }
    }

    public boolean resetPassword(String token, String newPassword) {
        try {
            if (!isValidToken(token)) {
                logger.warn("❌ Invalid token for customer password reset: {}", token);
                return false;
            }

            CustomerResetTokenInfo tokenInfo = resetTokens.get(token);
            if (tokenInfo == null) {
                return false;
            }

            String email = tokenInfo.getEmail();
            KhachHang khachHang = khachHangRepository.findByEmail(email)
                    .orElse(null);

            if (khachHang == null || !khachHang.getTrangThai()) {
                logger.warn("❌ Customer not found or inactive: {}", email);
                return false;
            }

            logger.info("🔐 Resetting password for customer: {}", email);
            String hashedPassword = passwordEncoder.encode(newPassword);
            logger.info("🔐 Password hashed successfully");

            khachHang.setMatKhau(hashedPassword);
            khachHangRepository.save(khachHang);

            resetTokens.remove(token);

            logger.info("✅ Password reset successfully for customer: {}", email);
            return true;

        } catch (Exception e) {
            logger.error("❌ Error resetting password for token {}: {}", token, e.getMessage());
            return false;
        }
    }

    public boolean isValidToken(String token) {
        if (token == null || token.length() != 36) {
            return false;
        }

        CustomerResetTokenInfo tokenInfo = resetTokens.get(token);
        if (tokenInfo == null) {
            return false;
        }

        if (System.currentTimeMillis() > tokenInfo.getExpiryTime()) {
            resetTokens.remove(token);
            logger.info("🗑️ Customer token expired and removed: {}", token);
            return false;
        }

        return true;
    }

    public String getEmailFromToken(String token) {
        CustomerResetTokenInfo tokenInfo = resetTokens.get(token);
        return tokenInfo != null ? tokenInfo.getEmail() : null;
    }

    public boolean isPasswordValid(String password) {
        if (password == null || password.length() < 6) {
            return false;
        }
        return true;
    }

    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        int initialSize = resetTokens.size();

        resetTokens.entrySet().removeIf(entry ->
                currentTime > entry.getValue().getExpiryTime()
        );

        int removedCount = initialSize - resetTokens.size();
        if (removedCount > 0) {
            logger.info("🧹 Cleaned up {} expired customer tokens", removedCount);
        }
    }

    private static class CustomerResetTokenInfo {
        private String email;
        private long expiryTime;

        public CustomerResetTokenInfo(String email, long expiryTime) {
            this.email = email;
            this.expiryTime = expiryTime;
        }

        public String getEmail() { return email; }
        public long getExpiryTime() { return expiryTime; }
    }
}