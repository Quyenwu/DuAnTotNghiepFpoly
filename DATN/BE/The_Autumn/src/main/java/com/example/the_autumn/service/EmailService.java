package com.example.the_autumn.service;

import com.example.the_autumn.entity.PhieuGiamGia;
import jakarta.mail.MessagingException;
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

    @Async
    public void sendResetPasswordEmail(String toEmail, String resetToken) {
        String resetLink = "http://localhost:5173/reset-password?token=" + resetToken;
        String subject = "🔐 Đặt lại mật khẩu - The Autumn";
        String body = buildResetPasswordEmail(resetLink);

        sendEmail(toEmail, subject, body);
    }

    private String buildResetPasswordEmail(String resetLink) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <style>
                    body { 
                        font-family: 'Arial', sans-serif; 
                        line-height: 1.6; 
                        color: #333; 
                        margin: 0; 
                        padding: 0; 
                    }
                    .container { 
                        max-width: 600px; 
                        margin: 0 auto; 
                        background: #ffffff; 
                    }
                    .header { 
                        background: linear-gradient(135deg, #E67E22, #D35400); 
                        color: white; 
                        padding: 30px 20px; 
                        text-align: center; 
                    }
                    .header h1 { 
                        margin: 0; 
                        font-size: 28px; 
                    }
                    .content { 
                        padding: 30px; 
                        background: #f9f9f9; 
                    }
                    .button { 
                        background: linear-gradient(135deg, #E67E22, #D35400); 
                        color: white; 
                        padding: 15px 30px; 
                        text-decoration: none; 
                        border-radius: 5px; 
                        display: inline-block; 
                        font-weight: bold;
                        font-size: 16px;
                        margin: 20px 0;
                    }
                    .code-box { 
                        background: #fff; 
                        padding: 15px; 
                        border-radius: 5px; 
                        border-left: 4px solid #E67E22;
                        margin: 20px 0;
                        word-break: break-all;
                        font-family: monospace;
                    }
                    .footer { 
                        text-align: center; 
                        padding: 20px; 
                        font-size: 12px; 
                        color: #666; 
                        background: #fff;
                    }
                    .note {
                        background: #fff3cd;
                        padding: 15px;
                        border-radius: 5px;
                        border-left: 4px solid #ffc107;
                        margin: 20px 0;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">
                        <h1>THE AUTUMN</h1>
                        <p>Hệ thống quản lý cửa hàng thời trang</p>
                    </div>
                    <div class="content">
                        <h2>🔐 Đặt Lại Mật Khẩu</h2>
                        <p>Xin chào,</p>
                        <p>Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản <strong>The Autumn</strong>.</p>
                        
                        <p style="text-align: center;">
                            <a href="%s" class="button">ĐẶT LẠI MẬT KHẨU</a>
                        </p>
                        
                        <p>Hoặc sử dụng link sau:</p>
                        <div class="code-box">
                            %s
                        </div>
                        
                        <div class="note">
                            <p><strong>📌 Lưu ý quan trọng:</strong></p>
                            <p>• Link đặt lại mật khẩu sẽ hết hạn sau <strong>24 giờ</strong></p>
                            <p>• Nếu bạn không yêu cầu đặt lại mật khẩu, vui lòng bỏ qua email này</p>
                            <p>• Liên hệ hỗ trợ nếu bạn gặp bất kỳ vấn đề nào</p>
                        </div>
                    </div>
                    <div class="footer">
                        <p><strong>The Autumn Team</strong></p>
                        <p>📧 Email: TheAutumnShop@gmail.com</p>
                        <p>📞 Hotline: 0900 123 456</p>
                        <p>© 2025 The Autumn. All rights reserved.</p>
                    </div>
                </div>
            </body>
            </html>
            """.formatted(resetLink, resetLink);
    }

    @Async
    public void sendDiscountEmail(String to, PhieuGiamGia phieu) {
        sendEmail(to, "🎉 Bạn nhận được phiếu giảm giá từ The Autumn!", buildEmailBody(phieu));
    }

    @Async
    public void sendDiscountUpdateEmail(String to, PhieuGiamGia phieu) {
        sendEmail(to, "🔔 Thay đổi giá trị phiếu giảm giá của bạn!", buildEmailBodyUpdate(phieu));
    }

    @Async
    public void sendDiscountCancelEmail(String to, PhieuGiamGia phieu) {
        sendEmail(to, "⚠️ Phiếu giảm giá của bạn đã bị hủy", buildEmailBodyCancel(phieu));
    }

    @Async
    public void sendMailKhachHang(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
        }
    }

    @Async
    public void sendEmail(String to, String subject, String body) {
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
    @Async
    public void sendMailNhanVien(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            mailSender.send(message);
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi gửi email: " + e.getMessage());
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

    private String buildEmailBodyUpdate(PhieuGiamGia phieu) {
        String maGiamGia = getMaGiamGia(phieu);
        String giamGiaText = formatGiamGia(phieu);
        String mucGiamToiDa = (phieu.getMucGiaGiamToiDa() != null && phieu.getMucGiaGiamToiDa().compareTo(BigDecimal.ZERO) > 0)
                ? String.format("<br>💰 Mức giảm tối đa: <strong>%,d VNĐ</strong>", phieu.getMucGiaGiamToiDa().intValue())
                : "";
        BigDecimal donHangToiThieu = phieu.getGiaTriDonHangToiThieu() != null
                ? phieu.getGiaTriDonHangToiThieu() : BigDecimal.ZERO;

        return String.format("""
        <!DOCTYPE html> <html> <head> <meta charset="UTF-8"> <style> body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; } .container { max-width: 600px; margin: 0 auto; padding: 20px; border: 1px solid #ddd; border-radius: 10px; } .header { background: linear-gradient(135deg, #E67E22, #D35400); color: white; padding: 20px; text-align: center; border-radius: 10px 10px 0 0; } .content { padding: 20px; background: #f9f9f9; } .discount-code { font-size: 24px; font-weight: bold; color: #E67E22; text-align: center; margin: 20px 0; } .footer { text-align: center; padding: 20px; font-size: 12px; color: #666; } </style> </head> <body> <div class="container"> <div class="header"> <h1>🎉 THÔNG BÁO KHUYẾN MÃI</h1> </div> <div class="content"> <p>Xin chào Quý khách,</p> <p>The Autumn cập nhập lại phiếu giảm giá cho bạn:</p> <div class="discount-code"> MÃ GIẢM GIÁ: <span style="color: #E67E22;">%s</span> </div> <div style="background: white; padding: 15px; border-radius: 5px; border-left: 4px solid #E67E22;"> <p><strong>🏷 Chương trình:</strong> %s</p> <p><strong>💰 Giá trị giảm:</strong> <span style="color: #27AE60;">%s</span></p> <p><strong>📅 Thời gian áp dụng:</strong> %s đến %s</p> <p><strong>📦 Đơn hàng tối thiểu:</strong> %,d VNĐ</p> %s </div> <p style="margin-top: 20px;">Hãy sử dụng ngay để nhận ưu đãi hấp dẫn từ chúng tôi!</p> </div> <div class="footer"> <p>Trân trọng,<br><strong>The Autumn Team</strong></p> </div> </div> </body> </html>
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

    @Async
    public void sendCustomerResetPasswordEmail(String toEmail, String resetToken) {
        String websiteLink = "http://localhost:5173/customer/login";
        String subject = "🔐 Đặt lại mật khẩu tài khoản khách hàng - The Autumn";
        String body = buildCustomerResetPasswordEmail(websiteLink, resetToken);

        sendEmail(toEmail, subject, body);
    }

    private String buildCustomerResetPasswordEmail(String websiteLink, String resetToken) {
        // Sử dụng String.format thay vì text block formatting
        return String.format("""
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <style>
                body { 
                    font-family: 'Arial', sans-serif; 
                    line-height: 1.6; 
                    color: #333; 
                    margin: 0; 
                    padding: 0; 
                    background-color: #f9f9f9;
                }
                .container { 
                    max-width: 600px; 
                    margin: 20px auto; 
                    background: #ffffff; 
                    border-radius: 10px;
                    overflow: hidden;
                    box-shadow: 0 4px 15px rgba(0,0,0,0.1);
                }
                .header { 
                    background: linear-gradient(135deg, #E67E22, #D35400); 
                    color: white; 
                    padding: 30px 20px; 
                    text-align: center; 
                }
                .header h1 { 
                    margin: 0; 
                    font-size: 28px; 
                    font-weight: bold;
                }
                .header p {
                    margin: 10px 0 0 0;
                    opacity: 0.9;
                }
                .content { 
                    padding: 40px 30px; 
                }
                .welcome-text {
                    font-size: 16px;
                    margin-bottom: 25px;
                    color: #555;
                }
                .button { 
                    background: linear-gradient(135deg, #E67E22, #D35400); 
                    color: white; 
                    padding: 16px 35px; 
                    text-decoration: none; 
                    border-radius: 8px; 
                    display: inline-block; 
                    font-weight: bold;
                    font-size: 16px;
                    transition: all 0.3s ease;
                    box-shadow: 0 4px 15px rgba(230, 126, 34, 0.3);
                    margin: 10px 0;
                }
                .button:hover {
                    transform: translateY(-2px);
                    box-shadow: 0 6px 20px rgba(230, 126, 34, 0.4);
                }
                .code-box { 
                    background: #f8f9fa; 
                    padding: 20px; 
                    border-radius: 8px; 
                    border-left: 4px solid #E67E22;
                    margin: 25px 0;
                    word-break: break-all;
                    font-family: 'Courier New', monospace;
                    font-size: 14px;
                    text-align: center;
                }
                .footer { 
                    text-align: center; 
                    padding: 25px; 
                    font-size: 12px; 
                    color: #666; 
                    background: #f8f9fa;
                    border-top: 1px solid #eee;
                }
                .note {
                    background: #fff3cd;
                    padding: 20px;
                    border-radius: 8px;
                    border-left: 4px solid #ffc107;
                    margin: 25px 0;
                    font-size: 14px;
                }
                .note strong {
                    color: #856404;
                }
                .steps {
                    background: #e8f4fd;
                    padding: 20px;
                    border-radius: 8px;
                    margin: 20px 0;
                }
                .step {
                    margin: 10px 0;
                    display: flex;
                    align-items: center;
                }
                .step-number {
                    background: #E67E22;
                    color: white;
                    width: 25px;
                    height: 25px;
                    border-radius: 50%%;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    margin-right: 15px;
                    font-weight: bold;
                    font-size: 14px;
                }
                .instruction-box {
                    background: #e7f3ff;
                    padding: 15px;
                    border-radius: 8px;
                    border-left: 4px solid #1890ff;
                    margin: 15px 0;
                }
            </style>
        </head>
        <body>
            <div class="container">
                <div class="header">
                    <h1>THE AUTUMN</h1>
                    <p>Hệ thống thời trang cao cấp</p>
                </div>
                <div class="content">
                    <h2 style="color: #E67E22; margin-top: 0;">🔐 Yêu Cầu Đặt Lại Mật Khẩu</h2>
                    
                    <p class="welcome-text">Xin chào Quý khách,</p>
                    <p>Chúng tôi đã nhận được yêu cầu đặt lại mật khẩu cho tài khoản khách hàng của bạn tại <strong>The Autumn</strong>.</p>
                    
                    <div class="instruction-box">
                        <p><strong>📝 Hướng dẫn đặt lại mật khẩu:</strong></p>
                        <p>1. Truy cập trang đăng nhập khách hàng</p>
                        <p>2. Nhấp vào "Quên mật khẩu?"</p>
                        <p>3. Nhập mã xác nhận bên dưới cùng với mật khẩu mới</p>
                    </div>
                    
                    <div style="text-align: center; margin: 25px 0;">
                        <a href="%s" class="button">TRUY CẬP TRANG ĐĂNG NHẬP</a>
                    </div>
                    
                    <p><strong>Mã xác nhận của bạn:</strong></p>
                    <div class="code-box">
                        <strong style="font-size: 18px; color: #E67E22;">%s</strong>
                    </div>
                    
                    <div class="note">
                        <p><strong>📌 Thông tin quan trọng:</strong></p>
                        <p>• Mã xác nhận có hiệu lực trong <strong>24 giờ</strong></p>
                        <p>• Vui lòng không chia sẻ mã này với bất kỳ ai</p>
                        <p>• Nếu bạn không thực hiện yêu cầu này, hãy bỏ qua email</p>
                        <p>• Liên hệ hỗ trợ nếu bạn cần trợ giúp thêm</p>
                    </div>
                    
                    <p>Trân trọng,<br>
                    <strong>Đội ngũ The Autumn</strong></p>
                </div>
                <div class="footer">
                    <p><strong>The Autumn - Hệ thống thời trang cao cấp</strong></p>
                    <p>📧 Email: TheAutumnShop@gmail.com | 📞 Hotline: 0900 123 456</p>
                    <p>© 2025 The Autumn. All rights reserved.</p>
                </div>
            </div>
        </body>
        </html>
        """, websiteLink, resetToken);
    }
}