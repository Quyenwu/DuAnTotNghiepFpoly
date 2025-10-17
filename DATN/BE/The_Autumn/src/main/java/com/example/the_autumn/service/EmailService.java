package com.example.the_autumn.service;

import com.example.the_autumn.entity.PhieuGiamGia;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    public void sendDiscountEmail(String to, PhieuGiamGia phieu) {
        sendEmail(to, "🎉 Bạn nhận được phiếu giảm giá từ The Autumn!", buildEmailBody(phieu));
    }

    public void sendDiscountUpdateEmail(String to, PhieuGiamGia phieu) {
        sendEmail(to, "🔔 Thay đổi giá trị phiếu giảm giá của bạn!", buildEmailBody(phieu));
    }

    public void sendDiscountCancelEmail(String to, PhieuGiamGia phieu) {
        sendEmail(to, "⚠️ Phiếu giảm giá của bạn đã bị hủy", buildEmailBodyCancel(phieu));
    }

    private void sendEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom("TheAutumnShop@gmail.com", "The Autumn");
            mailSender.send(message);
            logger.info("✅ Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("⚠️ Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    private String getMaGiamGia(PhieuGiamGia phieu) {
        return (phieu.getMaGiamGia() != null && !phieu.getMaGiamGia().isBlank())
                ? phieu.getMaGiamGia()
                : "AUTUMN_" + phieu.getId();
    }

    private String formatGiamGia(PhieuGiamGia phieu) {
        if (Boolean.TRUE.equals(phieu.getLoaiGiamGia())) {
            return String.format("%,d VNĐ", phieu.getGiaTriGiamGia().intValue());
        } else {
            return phieu.getGiaTriGiamGia().intValue() + "%";
        }
    }

    private String buildEmailBodyCancel(PhieuGiamGia phieu) {
        return String.format("""
                <p>Xin chào Quý khách,</p>
                <p>Rất tiếc, phiếu giảm giá <strong>%s</strong> của bạn đã bị hủy.</p>
                <p>Nếu có thắc mắc, vui lòng liên hệ bộ phận chăm sóc khách hàng.</p>
                <p>Trân trọng,<br>The Autumn Team</p>
                """, getMaGiamGia(phieu));
    }

    private String buildEmailBody(PhieuGiamGia phieu) {
        String maGiamGia = getMaGiamGia(phieu);
        String giamGiaText = formatGiamGia(phieu);
        String mucGiamToiDa = (phieu.getMucGiaGiamToiDa() != null && phieu.getMucGiaGiamToiDa().compareTo(BigDecimal.ZERO) > 0)
                ? String.format("<br>💰 Mức giảm tối đa: <strong>%,d VNĐ</strong>", phieu.getMucGiaGiamToiDa().intValue())
                : "";
        BigDecimal donHangToiThieu = phieu.getGiaTriDonHangToiThieu() != null
                ? phieu.getGiaTriDonHangToiThieu() : BigDecimal.ZERO;

        return String.format("""
        <!DOCTYPE html> <html> <head> <meta charset="UTF-8"> <style> body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; } .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; } .header { background: linear-gradient(135deg, #E67E22, #D35400); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; } .content { padding: 20px; background: #f9f9f9; } .discount-code { font-size: 24px; font-weight: bold; color: #E67E22; text-align: center; margin: 20px 0; } .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; } </style> </head> <body> <div class="container"> <div class="header"> <h1>🎉 THÔNG BÁO KHUYẾN MÃI</h1> </div> <div class="content"> <p>Xin chào Quý khách,</p> <p>The Autumn gửi tặng bạn phiếu giảm giá đặc biệt:</p> <div class="discount-code"> MÃ GIẢM GIÁ: <span style="color: #E67E22;">%s</span> </div> <div style="background: white; padding: 15px; border-radius: 5px; border-left: 4px solid #E67E22;"> <p><strong>🏷 Chương trình:</strong> %s</p> <p><strong>💰 Giá trị giảm:</strong> <span style="color: #27AE60;">%s</span></p> <p><strong>📅 Thời gian áp dụng:</strong> %s đến %s</p> <p><strong>📦 Đơn hàng tối thiểu:</strong> %,d VNĐ</p> %s </div> <p style="margin-top: 20px;">Hãy sử dụng ngay để nhận ưu đãi hấp dẫn từ chúng tôi!</p> </div> <div class="footer"> <p>Trân trọng,<br><strong>The Autumn Team</strong></p> </div> </div> </body> </html>
        """,
                maGiamGia,
                phieu.getTenChuongTrinh(),
                giamGiaText,
                phieu.getNgayBatDau(),
                phieu.getNgayKetThuc(),
                donHangToiThieu.intValue(),
                mucGiamToiDa
        );
    }
}