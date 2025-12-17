package com.example.the_autumn.controller;

import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.service.LichSuThanhToanService;
import com.example.the_autumn.service.VnPayService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/vnpay")
@Slf4j
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class VnPayController {

    @Autowired
    private VnPayService vnPayService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private LichSuThanhToanService lichSuThanhToanService;

    /**
     * Tạo URL thanh toán VNPay
     */
    @PostMapping("/create-payment")
    public ResponseEntity<?> createPayment(@RequestParam String maHoaDon, HttpServletRequest request) {
        try {
            log.info("=== Creating VNPAY payment for order: {} ===", maHoaDon);

            Optional<HoaDon> hoaDonOpt = hoaDonRepository.findByMaHoaDon(maHoaDon);
            if (!hoaDonOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Đơn hàng không tồn tại"));
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Kiểm tra trạng thái đơn hàng
            if (hoaDon.getTrangThai() != null && hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 5) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Đơn hàng đã được xử lý"));
            }

            // Kiểm tra số tiền
            if (hoaDon.getTongTienSauGiam() == null || hoaDon.getTongTienSauGiam().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Số tiền thanh toán không hợp lệ"));
            }

            // ⭐ TẠO LỊCH SỬ THANH TOÁN VÀ LƯU ID VÀO MAP
            Integer paymentHistoryId = lichSuThanhToanService.createPendingVnPayPayment(hoaDon);
            if (paymentHistoryId != null) {
                vnPayService.savePendingPaymentId(maHoaDon, paymentHistoryId);
                log.info("✅ Created pending payment history with ID: {}", paymentHistoryId);
            }

            // Tạo URL thanh toán
            String paymentUrl = vnPayService.createPaymentUrl(hoaDon, request);

            // Cập nhật trạng thái đơn hàng thành đang chờ thanh toán
            hoaDon.setTrangThai(2); // 2 = Đang chờ thanh toán
            hoaDonRepository.save(hoaDon);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("paymentUrl", paymentUrl);
            response.put("orderCode", maHoaDon);
            response.put("amount", hoaDon.getTongTienSauGiam());
            response.put("message", "Tạo URL thanh toán thành công");

            log.info("✅ Payment URL created successfully for order: {}", maHoaDon);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error creating payment URL: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi tạo URL thanh toán: " + e.getMessage()));
        }
    }

    /**
     * Endpoint callback từ VNPay (Return URL)
     * Được gọi khi người dùng hoàn tất thanh toán trên VNPay
     */
    @GetMapping("/payment-callback")
    public void paymentCallback(HttpServletRequest request, HttpServletResponse response) {
        try {
            log.info("=== VNPAY Callback Received ===");

            // Lấy tất cả parameters từ VNPay
            Map<String, String> params = new HashMap<>();
            request.getParameterNames().asIterator()
                    .forEachRemaining(paramName ->
                            params.put(paramName, request.getParameter(paramName)));

            log.info("📥 Callback params: {}", params);

            String vnp_ResponseCode = params.get("vnp_ResponseCode");
            String vnp_TxnRef = params.get("vnp_TxnRef");
            String vnp_Amount = params.get("vnp_Amount");
            String vnp_TransactionNo = params.get("vnp_TransactionNo");

            // Chuyển đổi số tiền từ VNPay (đơn vị: đồng * 100) sang đơn vị thực
            long amount = 0;
            if (vnp_Amount != null) {
                amount = Long.parseLong(vnp_Amount) / 100;
            }

            String maHoaDon = vnp_TxnRef;
            if (vnp_TxnRef != null && vnp_TxnRef.contains("_")) {
                maHoaDon = vnp_TxnRef.split("_")[0];
            }

            log.info("🔄 Processing callback for order: {}", maHoaDon);
            log.info("💰 Amount: {} VND", amount);
            log.info("📊 Response Code: {}", vnp_ResponseCode);

            // Redirect về frontend với thông tin thanh toán
            String frontendUrl = "http://localhost:3000/payment-result" + // Thay đổi URL frontend của bạn
                    "?success=" + ("00".equals(vnp_ResponseCode)) +
                    "&orderCode=" + maHoaDon +
                    "&amount=" + amount +
                    "&transactionNo=" + (vnp_TransactionNo != null ? vnp_TransactionNo : "") +
                    "&message=" + ("00".equals(vnp_ResponseCode) ? "Thanh toán thành công" : "Thanh toán thất bại");

            response.sendRedirect(frontendUrl);
            log.info("🔀 Redirecting to: {}", frontendUrl);

        } catch (Exception e) {
            log.error("❌ Error in payment callback: ", e);
            try {
                response.sendRedirect("http://localhost:3000/payment-result?success=false&message=Lỗi xử lý thanh toán");
            } catch (Exception ex) {
                log.error("❌ Error redirecting: ", ex);
            }
        }
    }

    /**
     * Endpoint IPN (Instant Payment Notification)
     * Được VNPay gọi để xác nhận giao dịch (bất đồng bộ)
     */
    @PostMapping("/ipn")
    @ResponseBody
    public ResponseEntity<?> ipnCallback(HttpServletRequest request) {
        log.info("=== VNPAY IPN Endpoint Called ===");

        try {
            // Lấy tất cả parameters từ VNPay
            Map<String, String> params = new HashMap<>();
            request.getParameterNames().asIterator()
                    .forEachRemaining(paramName ->
                            params.put(paramName, request.getParameter(paramName)));

            log.info("📨 IPN params received: {}", params);

            // Xử lý IPN thông qua service
            Map<String, String> ipnResponse = vnPayService.handleIpnResponse(params);

            log.info("📤 IPN response: {}", ipnResponse);
            return ResponseEntity.ok(ipnResponse);

        } catch (Exception e) {
            log.error("❌ Error processing IPN: ", e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("RspCode", "99");
            errorResponse.put("Message", "Unknown error");
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Kiểm tra trạng thái thanh toán
     */
    @GetMapping("/check-payment-status")
    public ResponseEntity<?> checkPaymentStatus(@RequestParam String maHoaDon) {
        try {
            log.info("🔍 Checking payment status for order: {}", maHoaDon);

            Optional<HoaDon> hoaDonOpt = hoaDonRepository.findByMaHoaDon(maHoaDon);
            if (!hoaDonOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Đơn hàng không tồn tại"));
            }

            HoaDon hoaDon = hoaDonOpt.get();

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("orderCode", maHoaDon);
            response.put("status", hoaDon.getTrangThai());
            response.put("statusText", getStatusText(hoaDon.getTrangThai()));
            response.put("amount", hoaDon.getTongTienSauGiam());
            response.put("paymentDate", hoaDon.getNgayThanhToan());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error checking payment status: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi kiểm tra trạng thái thanh toán"));
        }
    }

    /**
     * Hủy thanh toán VNPay
     */
    @PostMapping("/cancel-payment")
    public ResponseEntity<?> cancelPayment(@RequestParam String maHoaDon) {
        try {
            log.info("🗑️ Cancelling payment for order: {}", maHoaDon);

            Optional<HoaDon> hoaDonOpt = hoaDonRepository.findByMaHoaDon(maHoaDon);
            if (!hoaDonOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Đơn hàng không tồn tại"));
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Chỉ hủy nếu đang ở trạng thái chờ thanh toán
            if (hoaDon.getTrangThai() != 2) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Không thể hủy thanh toán ở trạng thái hiện tại"));
            }

            // Cập nhật trạng thái về chờ thanh toán (0) hoặc thất bại (5)
            hoaDon.setTrangThai(5); // 5 = Thanh toán thất bại
            hoaDonRepository.save(hoaDon);

            // Cập nhật lịch sử thanh toán (nếu có)
            vnPayService.updatePaymentHistoryToFailed(hoaDon, "24", null); // 24 = Khách hàng hủy

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Đã hủy thanh toán VNPay");
            response.put("orderCode", maHoaDon);
            response.put("newStatus", hoaDon.getTrangThai());

            log.info("✅ Payment cancelled for order: {}", maHoaDon);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error cancelling payment: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi hủy thanh toán"));
        }
    }

    /**
     * Lấy danh sách ngân hàng hỗ trợ VNPay
     */
    @GetMapping("/supported-banks")
    public ResponseEntity<?> getSupportedBanks() {
        try {
            Map<String, String> banks = new HashMap<>();
            banks.put("VNPAYQR", "VNPAY QR");
            banks.put("VNBANK", "Ngân hàng nội địa");
            banks.put("INTCARD", "Thẻ quốc tế");
            banks.put("VISA", "VISA");
            banks.put("MASTERCARD", "MASTERCARD");
            banks.put("JCB", "JCB");
            banks.put("UPI", "UPI");
            banks.put("NCB", "Ngân hàng NCB");
            banks.put("SACOMBANK", "Ngân hàng SacomBank");
            banks.put("EXIMBANK", "Ngân hàng EximBank");
            banks.put("MSBANK", "Ngân hàng MSBANK");
            banks.put("NAMABANK", "Ngân hàng NamABank");
            banks.put("VNMART", "Ví điện tử VNMart");
            banks.put("VIETINBANK", "Ngân hàng VietinBank");
            banks.put("VIETCOMBANK", "Ngân hàng Vietcombank");
            banks.put("BIDV", "Ngân hàng BIDV");
            banks.put("AGRIBANK", "Ngân hàng Agribank");
            banks.put("TPBANK", "Ngân hàng TPBank");
            banks.put("DONGABANK", "Ngân hàng Đông Á");
            banks.put("MB", "Ngân hàng MB");
            banks.put("SHB", "Ngân hàng SHB");
            banks.put("VPBANK", "Ngân hàng VPBank");
            banks.put("SEABANK", "Ngân hàng SeaBank");
            banks.put("OCB", "Ngân hàng OCB");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "banks", banks
            ));

        } catch (Exception e) {
            log.error("❌ Error getting supported banks: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi lấy danh sách ngân hàng"));
        }
    }

    /**
     * Helper method để chuyển đổi mã trạng thái thành text
     */
    private String getStatusText(Integer status) {
        if (status == null) return "Không xác định";

        switch (status) {
            case 0: return "Chờ thanh toán";
            case 1: return "Đã thanh toán";
            case 2: return "Đang chờ thanh toán VNPay";
            case 3: return "Đang xử lý";
            case 4: return "Đã hủy";
            case 5: return "Thanh toán thất bại";
            default: return "Không xác định";
        }
    }
}