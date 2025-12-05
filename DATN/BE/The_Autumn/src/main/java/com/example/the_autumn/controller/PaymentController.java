package com.example.the_autumn.controller;

import com.example.the_autumn.service.VnPayService;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.repository.HoaDonRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/api/v1/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174","http://192.203.4.118:5173"})
public class PaymentController {
    private static final Logger log = LoggerFactory.getLogger(PaymentController.class);

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private HoaDonRepository hoaDonRepo;

    /**
     * IPN URL - VNPAY server-to-server callback
     * Support both GET and POST methods
     */
    @RequestMapping(value = "/vnpay-ipn", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<Map<String, String>> handleIpn(HttpServletRequest request) {
        log.info("========== VNPAY IPN CALLBACK ==========");
        log.info("📍 Method: {}", request.getMethod());
        log.info("📍 URI: {}", request.getRequestURI());
        log.info("📍 Query String: {}", request.getQueryString());

        Map<String, String> params = extractAllParams(request);

        log.info("📥 Total params received: {}", params.size());
        if (!params.isEmpty()) {
            params.forEach((key, value) -> log.info("  📌 {}: {}", key, value));
        }

        if (params.isEmpty()) {
            log.error("❌ NO PARAMS RECEIVED!");
            return ResponseEntity.ok(Map.of(
                    "RspCode", "99",
                    "Message", "No parameters received"
            ));
        }

        try {
            Map<String, String> response = vnPayService.handleIpnResponse(params);
            log.info("✅ IPN Response: {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Error handling IPN: ", e);
            return ResponseEntity.ok(Map.of(
                    "RspCode", "99",
                    "Message", "Error: " + e.getMessage()
            ));
        }
    }

    /**
     * Return URL - User redirect callback
     * ⭐ CẬP NHẬT PAYMENT STATUS NGAY TẠI ĐÂY (backup cho IPN)
     */
    @RequestMapping(value = "/vnpay-return", method = {RequestMethod.GET, RequestMethod.POST})
    @Transactional(rollbackFor = Exception.class)
    public void handleReturn(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        log.info("========== VNPAY RETURN CALLBACK ==========");
        log.info("📍 Method: {}", request.getMethod());
        log.info("📍 Query String: {}", request.getQueryString());

        Map<String, String> params = extractAllParams(request);

        log.info("📥 Total params received: {}", params.size());

        if (params.isEmpty()) {
            log.error("❌ NO PARAMS RECEIVED!");
            response.sendRedirect("http://localhost:5173/payment/failed?orderCode=UNKNOWN&errorCode=99");
            return;
        }

        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef");
        String vnp_TransactionNo = params.get("vnp_TransactionNo");
        String vnp_BankCode = params.get("vnp_BankCode");
        String vnp_PayDate = params.get("vnp_PayDate");
        String vnp_TransactionStatus = params.get("vnp_TransactionStatus");

        String maHoaDon = "UNKNOWN";
        if (vnp_TxnRef != null && !vnp_TxnRef.isEmpty()) {
            maHoaDon = vnp_TxnRef.contains("_") ? vnp_TxnRef.split("_")[0] : vnp_TxnRef;
        }

        log.info("📦 Order code: {}", maHoaDon);
        log.info("🔢 Response code: {}", vnp_ResponseCode);
        log.info("🔢 Transaction status: {}", vnp_TransactionStatus);

        // ⭐ CẬP NHẬT PAYMENT STATUS NGAY TẠI ĐÂY
        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);

            if (hoaDonOpt.isPresent()) {
                HoaDon hoaDon = hoaDonOpt.get();

                if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                    // ✅ THANH TOÁN THÀNH CÔNG
                    log.info("✅ Payment SUCCESS - Updating order status");

                    // Chỉ update nếu đơn hàng chưa được xử lý
                    if (hoaDon.getTrangThai() == 0 || hoaDon.getTrangThai() == 5) {
                        hoaDon.setTrangThai(1); // Chờ giao hàng
                        hoaDon.setNgayThanhToan(new Date());
                        hoaDonRepo.save(hoaDon);
                        hoaDonRepo.flush();

                        log.info("✅ Order status updated to: {}", hoaDon.getTrangThai());

                        // Cập nhật lịch sử thanh toán
                        vnPayService.updatePaymentHistoryToSuccess(
                                hoaDon,
                                vnp_TransactionNo,
                                vnp_BankCode,
                                vnp_PayDate
                        );

                        log.info("✅ Payment history updated successfully");
                    } else {
                        log.info("ℹ️ Order already processed (status: {})", hoaDon.getTrangThai());
                    }

                } else {
                    // ❌ THANH TOÁN THẤT BẠI
                    log.warn("❌ Payment FAILED - Code: {}", vnp_ResponseCode);

                    if (hoaDon.getTrangThai() == 0) {
                        hoaDon.setTrangThai(5); // Thanh toán thất bại
                        hoaDonRepo.save(hoaDon);
                        hoaDonRepo.flush();

                        log.info("❌ Order status updated to FAILED");

                        // Cập nhật lịch sử thanh toán thất bại
                        vnPayService.updatePaymentHistoryToFailed(
                                hoaDon,
                                vnp_ResponseCode,
                                vnp_TransactionNo
                        );

                        log.info("❌ Payment history updated to FAILED");
                    }
                }
            } else {
                log.error("❌ Order not found: {}", maHoaDon);
            }

        } catch (Exception e) {
            log.error("❌ Error updating payment from return URL: ", e);
            // Vẫn redirect để user biết kết quả
        }

        // Redirect user đến trang kết quả
        String redirectUrl;
        if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
            log.info("✅ Redirecting to success page");
            redirectUrl = String.format("http://localhost:5173/payment/success?orderCode=%s", maHoaDon);
        } else {
            log.warn("❌ Redirecting to failed page");
            String errorCode = vnp_ResponseCode != null ? vnp_ResponseCode : "99";
            redirectUrl = String.format("http://localhost:5173/payment/failed?orderCode=%s&errorCode=%s",
                    maHoaDon, errorCode);
        }

        log.info("🔗 Redirecting to: {}", redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    /**
     * Extract parameters from both GET and POST requests
     */
    private Map<String, String> extractAllParams(HttpServletRequest request) {
        Map<String, String> params = new HashMap<>();

        try {
            // 1. Get from query string (GET)
            Enumeration<String> paramNames = request.getParameterNames();
            while (paramNames.hasMoreElements()) {
                String name = paramNames.nextElement();
                String value = request.getParameter(name);
                if (value != null && !value.isEmpty()) {
                    params.put(name, value);
                }
            }

            // 2. If no params and POST, try reading body
            if (params.isEmpty() && "POST".equalsIgnoreCase(request.getMethod())) {
                try {
                    String body = request.getReader().lines()
                            .collect(Collectors.joining(System.lineSeparator()));

                    log.info("📄 POST Body: {}", body);

                    if (body != null && !body.isEmpty()) {
                        String[] pairs = body.split("&");
                        for (String pair : pairs) {
                            String[] kv = pair.split("=", 2);
                            if (kv.length == 2) {
                                String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                                String value = java.net.URLDecoder.decode(kv[1], "UTF-8");
                                params.put(key, value);
                            }
                        }
                    }
                } catch (Exception e) {
                    log.error("Error reading POST body: ", e);
                }
            }
        } catch (Exception e) {
            log.error("Error extracting params: ", e);
        }

        return params;
    }

    /**
     * Test endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<?> test(HttpServletRequest request) {
        log.info("🧪 Test endpoint called");

        Map<String, String> params = extractAllParams(request);

        return ResponseEntity.ok(Map.of(
                "status", "ok",
                "message", "VNPay controller is working",
                "method", request.getMethod(),
                "queryString", request.getQueryString() != null ? request.getQueryString() : "null",
                "paramsReceived", params,
                "paramsCount", params.size(),
                "timestamp", System.currentTimeMillis()
        ));
    }
}