package com.example.the_autumn.API;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

public class TestVnPayHash {

    public static void main(String[] args) {
        // ⚠️ SECRET KEY TỪ VNPAY SANDBOX - KIỂM TRA LẠI
        String secretKey = "S05ET46I4SWCPCMQ7ZFBNCAIOZOK1CO8";

        // ⚠️ COPY EXACT HashData từ log backend của bạn
        // Ví dụ từ log:
        String hashData = "vnp_Amount=17900000&vnp_Command=pay&vnp_CreateDate=20251112191022&vnp_CurrCode=VND&vnp_ExpireDate=20251112192522&vnp_IpAddr=127.0.0.1&vnp_IpnUrl=https://ed5f7f40e3a5.ngrok-free.app/api/v1/payment/vnpay-ipn&vnp_Locale=vn&vnp_OrderInfo=Thanh toan don hang HD00086&vnp_OrderType=other&vnp_ReturnUrl=https://ed5f7f40e3a5.ngrok-free.app/api/v1/payment/vnpay-return&vnp_TmnCode=HLACIE3M&vnp_TxnRef=HD00086_1762956622&vnp_Version=2.1.0";

        System.out.println("========================================");
        System.out.println("VNPAY HASH TEST");
        System.out.println("========================================");
        System.out.println();

        System.out.println("🔑 Secret Key:");
        System.out.println(secretKey);
        System.out.println();

        System.out.println("📝 HashData (Raw - no encoding):");
        System.out.println(hashData);
        System.out.println();

        String calculatedHash = hmacSHA512(secretKey, hashData);

        System.out.println("🔐 Calculated SecureHash:");
        System.out.println(calculatedHash);
        System.out.println();

        System.out.println("========================================");
        System.out.println("✅ COPY hash này và so sánh với:");
        System.out.println("   1. Hash trong log backend");
        System.out.println("   2. Hash trong Payment URL");
        System.out.println("========================================");
    }

    public static String hmacSHA512(String key, String data) {
        try {
            Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
            SecretKeySpec secretKey = new SecretKeySpec(hmacKeyBytes, "HmacSHA512");
            hmac512.init(secretKey);

            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();

        } catch (Exception ex) {
            throw new RuntimeException("Error generating HMAC SHA512", ex);
        }
    }
}