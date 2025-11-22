package com.example.the_autumn.config;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class VnPayConfig {
    private static final Logger log = LoggerFactory.getLogger(VnPayConfig.class);

    public static final String vnp_TmnCode = "HLACIE3M";
    public static final String vnp_HashSecret = "S05ET46I4SWCPCMQ7ZFBNCAIOZOK1CO8";
    public static final String vnp_Url = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";

    @Value("${vnpay.callback.base-url}")
    private String baseUrl;

    public String getVnpIpnUrl() {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("vnpay.callback.base-url is not configured");
        }
        String cleanBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;

        String ipnUrl = cleanBaseUrl + "/api/v1/payment/vnpay-ipn";
        log.info("📍 IPN URL: {}", ipnUrl);
        return ipnUrl;
    }

    public String getVnpReturnUrl() {
        if (baseUrl == null || baseUrl.isEmpty()) {
            throw new RuntimeException("vnpay.callback.base-url is not configured");
        }
        String cleanBaseUrl = baseUrl.endsWith("/")
                ? baseUrl.substring(0, baseUrl.length() - 1)
                : baseUrl;

        String returnUrl = cleanBaseUrl + "/api/v1/payment/vnpay-return";
        log.info("📍 Return URL: {}", returnUrl);
        return returnUrl;
    }

    public static String hmacSHA512(String key, String data) {
        try {
            if (key == null || data == null) {
                throw new NullPointerException("Key and data must not be null");
            }

            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeyBytes = key.getBytes(StandardCharsets.UTF_8);
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
            throw new RuntimeException("Error generating HMAC SHA512", ex);
        }
    }

    public static String getIpAddress(HttpServletRequest request) {
        String ipAddr = request.getHeader("X-Forwarded-For");

        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddr == null || ipAddr.isEmpty() || "unknown".equalsIgnoreCase(ipAddr)) {
            ipAddr = request.getRemoteAddr();
        }

        if (ipAddr != null && ipAddr.contains(",")) {
            ipAddr = ipAddr.split(",")[0].trim();
        }

        if ("0:0:0:0:0:0:0:1".equals(ipAddr)) {
            return "127.0.0.1";
        }

        if (ipAddr == null || ipAddr.isEmpty()) {
            return "127.0.0.1";
        }

        return ipAddr;
    }

    public static Map<String, String> getParamsFromRequest(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();
        Enumeration<String> paramNames = request.getParameterNames();

        while (paramNames.hasMoreElements()) {
            String paramName = paramNames.nextElement();
            String paramValue = request.getParameter(paramName);
            if (paramValue != null && !paramValue.isEmpty()) {
                params.put(paramName, paramValue);
            }
        }

        return params;
    }
}