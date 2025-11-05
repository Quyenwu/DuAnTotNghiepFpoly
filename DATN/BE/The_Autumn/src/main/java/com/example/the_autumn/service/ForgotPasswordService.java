package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class ForgotPasswordService {

    private static final Logger logger = LoggerFactory.getLogger(ForgotPasswordService.class);

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private EmailService emailService;

    private final ConcurrentMap<String, ResetTokenInfo> resetTokens = new ConcurrentHashMap<>();

    public String generateResetToken(String email) {
        if (!isEmailExists(email)) {
            logger.warn("❌ Email not found: {}", email);
            return null;
        }

        String resetToken = UUID.randomUUID().toString();

        resetTokens.put(resetToken, new ResetTokenInfo(email, System.currentTimeMillis() + (24 * 60 * 60 * 1000)));

        try {
            emailService.sendResetPasswordEmail(email, resetToken);
            logger.info("✅ Reset password email sent to: {}", email);
        } catch (Exception e) {
            logger.error("❌ Failed to send reset password email to {}: {}", email, e.getMessage());
        }

        logger.info("🔐 Generated reset token for {}: {}", email, resetToken);
        return resetToken;
    }

    public boolean isValidToken(String token) {
        if (token == null || token.length() != 36) {
            return false;
        }

        ResetTokenInfo tokenInfo = resetTokens.get(token);
        if (tokenInfo == null) {
            return false;
        }

        if (System.currentTimeMillis() > tokenInfo.getExpiryTime()) {
            resetTokens.remove(token);
            logger.info("🗑️ Token expired and removed: {}", token);
            return false;
        }

        return true;
    }

    public String getEmailFromToken(String token) {
        ResetTokenInfo tokenInfo = resetTokens.get(token);
        return tokenInfo != null ? tokenInfo.getEmail() : null;
    }

    public boolean resetPassword(String token, String newPassword) {
        try {
            if (!isValidToken(token)) {
                logger.warn("❌ Invalid token for password reset: {}", token);
                return false;
            }

            String email = getEmailFromToken(token);
            if (email == null) {
                return false;
            }

            if (newPassword == null || newPassword.length() < 6) {
                logger.warn("❌ Invalid password length for email: {}", email);
                return false;
            }

            boolean updated = updatePasswordInDatabase(email, newPassword);

            if (updated) {
                resetTokens.remove(token);
                logger.info("✅ Password reset successfully for: {}", email);
                return true;
            } else {
                logger.error("❌ Failed to update password in database for: {}", email);
                return false;
            }

        } catch (Exception e) {
            logger.error("❌ Error resetting password for token {}: {}", token, e.getMessage());
            return false;
        }
    }

    private boolean updatePasswordInDatabase(String email, String newPassword) {
        try {
            List<NhanVien> allNhanVien = nhanVienRepository.findAll();
            Optional<NhanVien> nvOpt = allNhanVien.stream()
                    .filter(nv -> email.equals(nv.getEmail()))
                    .findFirst();

            if (nvOpt.isPresent()) {
                NhanVien nv = nvOpt.get();
                nv.setMatKhau(newPassword);
                nhanVienRepository.save(nv);
                logger.info("✅ Updated password for staff: {}", email);
                return true;
            }

            Optional<KhachHang> khOpt = khachHangRepository.findByEmail(email);
            if (khOpt.isPresent()) {
                KhachHang kh = khOpt.get();
                kh.setMatKhau(newPassword);
                khachHangRepository.save(kh);
                logger.info("✅ Updated password for customer: {}", email);
                return true;
            }

            logger.warn("❌ No user found with email: {}", email);
            return false;

        } catch (Exception e) {
            logger.error("❌ Database error updating password for {}: {}", email, e.getMessage());
            return false;
        }
    }

    private boolean isEmailExists(String email) {
        List<NhanVien> allNhanVien = nhanVienRepository.findAll();
        boolean existsInNhanVien = allNhanVien.stream()
                .anyMatch(nv -> email.equals(nv.getEmail()) &&
                        nv.getTrangThai() != null &&
                        nv.getTrangThai());

        boolean existsInKhachHang = khachHangRepository.findByEmail(email)
                .map(kh -> kh.getTrangThai() != null && kh.getTrangThai())
                .orElse(false);

        boolean exists = existsInNhanVien || existsInKhachHang;
        logger.info("🔍 Email {} exists in system: {}", email, exists);
        return exists;
    }

    public void cleanupExpiredTokens() {
        long currentTime = System.currentTimeMillis();
        int initialSize = resetTokens.size();

        resetTokens.entrySet().removeIf(entry ->
                currentTime > entry.getValue().getExpiryTime()
        );

        int removedCount = initialSize - resetTokens.size();
        if (removedCount > 0) {
            logger.info("🧹 Cleaned up {} expired tokens", removedCount);
        }
    }

    private static class ResetTokenInfo {
        private String email;
        private long expiryTime;

        public ResetTokenInfo(String email, long expiryTime) {
            this.email = email;
            this.expiryTime = expiryTime;
        }

        public String getEmail() { return email; }
        public long getExpiryTime() { return expiryTime; }
    }
}