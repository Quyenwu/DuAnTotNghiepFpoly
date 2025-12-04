package com.example.the_autumn.service;

import com.example.the_autumn.model.response.VietQRResponse;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VietQRService {

    public VietQRResponse taoVietQR(Long amount, String noiDung, String orderId, String tenKhachHang) {
        try {
            // Encode nội dung
            String noiDungEncoded = URLEncoder.encode(noiDung, StandardCharsets.UTF_8.toString());
            String tenKhachHangEncoded = URLEncoder.encode(tenKhachHang, StandardCharsets.UTF_8.toString());

            String bankCode = "970416";            // Ngân hàng
            String accountNumber = "20088591";     // Số tài khoản
            String accountName = tenKhachHangEncoded;
            String qrUrl =
                    "https://api.vietqr.io/image/" + bankCode + "-" + accountNumber + "-QNu6Y3t.jpg" +
                            "?accountName=" + accountName +
                            "&amount=" + amount +
                            "&addInfo=" + noiDungEncoded;

            return new VietQRResponse(
                    "Vui lòng quét mã để thanh toán đơn hàng #" + orderId,
                    qrUrl,
                    qrUrl,
                    orderId
            );

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo VietQR: " + e.getMessage());
        }
    }
}
