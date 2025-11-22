package com.example.the_autumn.service;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.HoaDonChiTiet;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

@Service
@Slf4j
public class EmailTaoDonHangService {
    @Autowired
    private JavaMailSender mailSender;

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) return "0₫";
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    private String formatDate(java.util.Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return sdf.format(date);
    }

    @Async
    @Transactional
    public void sendOrderConfirmationEmail(HoaDon hoaDon, String recipientEmail) {
        try {
            log.info("📧 Preparing to send order confirmation email to: {}", recipientEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("nguyentvph26645@fpt.edu.vn");
            helper.setTo(recipientEmail);
            helper.setSubject("Xác nhận đơn hàng #" + hoaDon.getMaHoaDon() + " - The Autumn Shop");

            String emailContent = buildOrderConfirmationEmail(hoaDon);
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("✅ Email sent successfully to: {}", recipientEmail);

        } catch (MessagingException e) {
            log.error("❌ Failed to send email to {}: {}", recipientEmail, e.getMessage());
        } catch (Exception e) {
            log.error("❌ Unexpected error sending email: {}", e.getMessage(), e);
        }
    }

    private String buildOrderConfirmationEmail(HoaDon hoaDon) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang='vi'>");
        html.append("<head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4; }");
        html.append(".container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { background: linear-gradient(135deg, #f97316 0%, #ea580c 100%); color: white; padding: 30px; text-align: center; }");
        html.append(".header h1 { margin: 0; font-size: 28px; }");
        html.append(".content { padding: 30px; }");
        html.append(".order-info { background: #fff7ed; border-left: 4px solid #f97316; padding: 15px; margin: 20px 0; border-radius: 5px; }");
        html.append(".order-info h2 { margin-top: 0; color: #ea580c; font-size: 18px; }");
        html.append(".info-row { display: flex; justify-content: space-between; padding: 8px 0; border-bottom: 1px solid #e5e7eb; }");
        html.append(".info-label { font-weight: bold; color: #6b7280; }");
        html.append(".info-value { color: #111827; }");
        html.append(".products-table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
        html.append(".products-table th { background: #f3f4f6; padding: 12px; text-align: left; font-weight: bold; color: #374151; }");
        html.append(".products-table td { padding: 12px; border-bottom: 1px solid #e5e7eb; }");
        html.append(".total-section { background: #fef3c7; padding: 15px; border-radius: 5px; margin: 20px 0; }");
        html.append(".total-row { display: flex; justify-content: space-between; padding: 5px 0; }");
        html.append(".total-label { font-weight: bold; }");
        html.append(".total-amount { font-size: 24px; color: #ea580c; font-weight: bold; }");
        html.append(".discount-badge { background: #10b981; color: white; padding: 2px 8px; border-radius: 4px; font-size: 12px; }");
        html.append(".footer { background: #f9fafb; padding: 20px; text-align: center; color: #6b7280; font-size: 14px; }");
        html.append(".button { display: inline-block; background: #f97316; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; margin: 10px 0; }");
        html.append("</style>");
        html.append("</head>");
        html.append("<body>");

        html.append("<div class='container'>");

        // Header
        html.append("<div class='header'>");
        html.append("<h1>🍂 The Autumn Shop</h1>");
        html.append("<p style='margin: 5px 0 0 0; font-size: 16px;'>Cảm ơn bạn đã đặt hàng!</p>");
        html.append("</div>");

        // Content
        html.append("<div class='content'>");
        html.append("<p>Xin chào <strong>").append(hoaDon.getKhachHang().getHoTen()).append("</strong>,</p>");
        html.append("<p>Đơn hàng của bạn đã được tiếp nhận thành công. Chúng tôi sẽ liên hệ với bạn trong thời gian sớm nhất.</p>");

        // Order Info
        html.append("<div class='order-info'>");
        html.append("<h2>📋 Thông tin đơn hàng</h2>");
        html.append("<div class='info-row'><span class='info-label'>Mã đơn hàng:</span><span class='info-value'>").append(hoaDon.getMaHoaDon()).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Ngày đặt:</span><span class='info-value'>").append(formatDate(hoaDon.getNgayTao())).append("</span></div>");
        html.append("<div class='info-row'><span class='info-label'>Trạng thái:</span><span class='info-value'>Chờ xác nhận</span></div>");
        html.append("</div>");

        // Delivery Address
        html.append("<div class='order-info'>");
        html.append("<h2>📍 Địa chỉ giao hàng</h2>");
        html.append("<p style='margin: 5px 0;'>").append(hoaDon.getDiaChiKhachHang()).append("</p>");
        html.append("<p style='margin: 5px 0;'><strong>SĐT:</strong> ").append(hoaDon.getKhachHang().getSdt()).append("</p>");
        html.append("</div>");

        // Products Table
        html.append("<h2 style='color: #ea580c; margin-top: 30px;'>🛍️ Sản phẩm đã đặt</h2>");
        html.append("<table class='products-table'>");
        html.append("<thead><tr>");
        html.append("<th>Sản phẩm</th>");
        html.append("<th style='text-align: center;'>SL</th>");
        html.append("<th style='text-align: right;'>Đơn giá</th>");
        html.append("<th style='text-align: right;'>Thành tiền</th>");
        html.append("</tr></thead>");
        html.append("<tbody>");

        for (HoaDonChiTiet item : hoaDon.getHoaDonChiTiets()) {
            html.append("<tr>");
            html.append("<td>").append(item.getChiTietSanPham().getSanPham().getTenSanPham());
            html.append("<br><small style='color: #6b7280;'>Size: ").append(item.getChiTietSanPham().getKichThuoc().getTenKichThuoc()).append("</small>");
            html.append("</td>");
            html.append("<td style='text-align: center;'>").append(item.getSoLuong()).append("</td>");
            html.append("<td style='text-align: right;'>").append(formatCurrency(item.getGiaBan())).append("</td>");
            html.append("<td style='text-align: right;'><strong>").append(formatCurrency(item.getThanhTien())).append("</strong></td>");
            html.append("</tr>");
        }

        html.append("</tbody>");
        html.append("</table>");

        // Total Section
        html.append("<div class='total-section'>");

        // Subtotal
        html.append("<div class='total-row'>");
        html.append("<span>Tạm tính:</span>");
        html.append("<span>").append(formatCurrency(hoaDon.getTongTien())).append("</span>");
        html.append("</div>");

        // Shipping
        html.append("<div class='total-row'>");
        html.append("<span>Phí vận chuyển:</span>");
        html.append("<span style='color: #10b981;'>Miễn phí</span>");
        html.append("</div>");

        // Discount (if any)
        if (hoaDon.getPhieuGiamGia() != null) {
            BigDecimal discount = hoaDon.getTongTien().subtract(hoaDon.getTongTienSauGiam());
            html.append("<div class='total-row'>");
            html.append("<span>Giảm giá <span class='discount-badge'>").append(hoaDon.getPhieuGiamGia().getTenChuongTrinh()).append("</span>:</span>");
            html.append("<span style='color: #10b981;'>-").append(formatCurrency(discount)).append("</span>");
            html.append("</div>");
        }

        html.append("<hr style='margin: 10px 0; border: none; border-top: 2px solid #d1d5db;'>");

        // Final Total
        html.append("<div class='total-row'>");
        html.append("<span class='total-label'>Tổng cộng:</span>");
        html.append("<span class='total-amount'>").append(formatCurrency(hoaDon.getTongTienSauGiam())).append("</span>");
        html.append("</div>");

        html.append("</div>");

        // Payment Method
        String paymentMethod = hoaDon.getHinhThucThanhToans().isEmpty() ? "Tiền mặt" :
                hoaDon.getHinhThucThanhToans().get(0).getPhuongThucThanhToan().getTenPhuongThucThanhToan();
        html.append("<div class='order-info'>");
        html.append("<h2>💳 Phương thức thanh toán</h2>");
        html.append("<p style='margin: 5px 0;'>").append(paymentMethod).append("</p>");
        html.append("</div>");

        // Call to Action
        html.append("<div style='text-align: center; margin: 30px 0;'>");
        html.append("<p>Bạn có thể tra cứu đơn hàng bằng số điện thoại tại website của chúng tôi.</p>");
        html.append("</div>");

        html.append("</div>");

        // Footer
        html.append("<div class='footer'>");
        html.append("<p><strong>The Autumn Shop</strong></p>");
        html.append("<p>Cảm ơn bạn đã tin tưởng và ủng hộ!</p>");
        html.append("<p style='font-size: 12px; color: #9ca3af;'>Email này được gửi tự động, vui lòng không trả lời.</p>");
        html.append("</div>");

        html.append("</div>");
        html.append("</body>");
        html.append("</html>");

        return html.toString();
    }

    @Async
    public void sendOrderCancellationEmail(HoaDon hoaDon, String recipientEmail, String reason) {
        try {
            log.info("📧 Sending order cancellation email to: {}", recipientEmail);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("nguyentvph26645@fpt.edu.vn");
            helper.setTo(recipientEmail);
            helper.setSubject("Đơn hàng #" + hoaDon.getMaHoaDon() + " đã được hủy - The Autumn Shop");

            String emailContent = buildCancellationEmail(hoaDon, reason);
            helper.setText(emailContent, true);

            mailSender.send(message);
            log.info("✅ Cancellation email sent successfully to: {}", recipientEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send cancellation email: {}", e.getMessage(), e);
        }
    }

    private String buildCancellationEmail(HoaDon hoaDon, String reason) {
        StringBuilder html = new StringBuilder();

        html.append("<!DOCTYPE html>");
        html.append("<html lang='vi'>");
        html.append("<head><meta charset='UTF-8'>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; line-height: 1.6; color: #333; }");
        html.append(".container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".header { background: #ef4444; color: white; padding: 30px; text-align: center; }");
        html.append(".content { padding: 30px; }");
        html.append(".info-box { background: #fef2f2; border-left: 4px solid #ef4444; padding: 15px; margin: 20px 0; border-radius: 5px; }");
        html.append(".footer { background: #f9fafb; padding: 20px; text-align: center; color: #6b7280; }");
        html.append("</style></head><body>");

        html.append("<div class='container'>");
        html.append("<div class='header'><h1>🚫 Đơn hàng đã hủy</h1></div>");
        html.append("<div class='content'>");
        html.append("<p>Xin chào <strong>").append(hoaDon.getKhachHang().getHoTen()).append("</strong>,</p>");
        html.append("<p>Đơn hàng <strong>").append(hoaDon.getMaHoaDon()).append("</strong> đã được hủy thành công.</p>");

        html.append("<div class='info-box'>");
        html.append("<p><strong>Lý do hủy:</strong></p>");
        html.append("<p>").append(reason != null ? reason : "Không có lý do").append("</p>");
        html.append("</div>");

        if (hoaDon.getPhieuGiamGia() != null) {
            html.append("<p>✅ Mã giảm giá <strong>").append(hoaDon.getPhieuGiamGia().getTenChuongTrinh()).append("</strong> đã được hoàn lại cho bạn.</p>");
        }

        html.append("<p>Cảm ơn bạn đã quan tâm đến The Autumn Shop. Chúng tôi hy vọng được phục vụ bạn trong tương lai!</p>");
        html.append("</div>");

        html.append("<div class='footer'>");
        html.append("<p><strong>The Autumn Shop</strong></p>");
        html.append("<p>Hỗ trợ: nguyentvph26645@fpt.edu.vn</p>");
        html.append("</div></div></body></html>");

        return html.toString();
    }
}

