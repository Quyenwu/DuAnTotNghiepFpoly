package com.example.the_autumn.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class VNPayService {

    @Value("${vnpay.tmnCode}")
    private String vnp_TmnCode;

    @Value("${vnpay.hashSecret}")
    private String vnp_HashSecret;

    @Value("${vnpay.url}")
    private String vnp_Url;

    @Value("${vnpay.returnUrl}")
    private String vnp_ReturnUrl;

    @Value("${vnpay.version}")
    private String vnp_Version;

    public String createOrder(int amount, String orderInfo, String orderId) {
        try {
            // Sử dụng tham số giống hệt code mẫu của VNPay
            String vnp_Command = "pay";
            String vnp_TxnRef = getRandomNumber(8); // VNPay yêu cầu random 8 số
            String vnp_IpAddr = "127.0.0.1"; // Hoặc lấy IP thực tế
            String orderType = "other"; // Loại đơn hàng

            // Tạo map chứa params - KHÔNG dùng TreeMap
            Map<String, String> vnp_Params = new HashMap<>();
            vnp_Params.put("vnp_Version", this.vnp_Version);
            vnp_Params.put("vnp_Command", vnp_Command);
            vnp_Params.put("vnp_TmnCode", this.vnp_TmnCode);
            vnp_Params.put("vnp_Amount", String.valueOf(amount * 100)); // Nhân 100
            vnp_Params.put("vnp_CurrCode", "VND");
            vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
            vnp_Params.put("vnp_OrderInfo", orderInfo);
            vnp_Params.put("vnp_OrderType", orderType);
            vnp_Params.put("vnp_Locale", "vn");
            vnp_Params.put("vnp_ReturnUrl", this.vnp_ReturnUrl);
            vnp_Params.put("vnp_IpAddr", vnp_IpAddr);

            // Thời gian tạo đơn - theo múi giờ VN
            Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
            String vnp_CreateDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_CreateDate", vnp_CreateDate);

            // Thời gian hết hạn (15 phút)
            cld.add(Calendar.MINUTE, 15);
            String vnp_ExpireDate = formatter.format(cld.getTime());
            vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

            // Sắp xếp các tham số theo key - GIỐNG HỆT VNPay
            List fieldNames = new ArrayList(vnp_Params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            StringBuilder query = new StringBuilder();

            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = vnp_Params.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    // Build hash data - ENCODE giống VNPay
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    // Build query
                    query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    query.append('=');
                    query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                    if (itr.hasNext()) {
                        query.append('&');
                        hashData.append('&');
                    }
                }
            }

            // Tạo chữ ký - Sử dụng hàm hash giống VNPay
            String vnp_SecureHash = hmacSHA512(this.vnp_HashSecret, hashData.toString());
            query.append("&vnp_SecureHash=").append(vnp_SecureHash);

            String paymentUrl = this.vnp_Url + "?" + query.toString();

            // Log để debug
            System.out.println("=== VNPay Payment URL Generation ===");
            System.out.println("Hash Data: " + hashData.toString());
            System.out.println("Secure Hash: " + vnp_SecureHash);
            System.out.println("Final URL: " + paymentUrl);

            return paymentUrl;

        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo URL thanh toán VNPAY: " + e.getMessage(), e);
        }
    }

    private String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException();
            }
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes();
            final SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);
            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            throw new RuntimeException("Lỗi tạo chữ ký HMAC: " + ex.getMessage(), ex);
        }
    }

    private String getRandomNumber(int len) {
        Random rnd = new Random();
        String chars = "0123456789";
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(rnd.nextInt(chars.length())));
        }
        return sb.toString();
    }

    public boolean validateIPN(Map<String, String> params) {
        try {
            String vnp_SecureHash = params.get("vnp_SecureHash");
            if (vnp_SecureHash == null || vnp_SecureHash.isEmpty()) {
                return false;
            }

            // Remove secure hash from params for validation
            Map<String, String> sortedParams = new HashMap<>(params);
            sortedParams.remove("vnp_SecureHash");

            // Sort parameters
            List fieldNames = new ArrayList(sortedParams.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            Iterator itr = fieldNames.iterator();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = sortedParams.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }

            String calculatedHash = hmacSHA512(this.vnp_HashSecret, hashData.toString());
            return vnp_SecureHash.equals(calculatedHash);

        } catch (Exception e) {
            System.err.println("Lỗi validate IPN: " + e.getMessage());
            return false;
        }
    }
}