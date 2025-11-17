package com.example.the_autumn.controller;

import com.example.the_autumn.service.VnPayService;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.LichSuThanhToan;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.LichSuThanhToanRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
@Slf4j
public class PaymentRetryController {

    @Autowired
    private HoaDonRepository hoaDonRepo;

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepo;

    @Autowired
    private VnPayService vnPayService;

    /**
     * Kiểm tra xem đơn hàng có thể thanh toán lại không
     * Điều kiện:
     * - Đơn hàng tồn tại
     * - Chưa có lịch sử thanh toán THÀNH CÔNG (trangThai = true)
     * - Trạng thái hóa đơn là 0 (chờ xử lý) hoặc 5 (thanh toán thất bại)
     */
    @GetMapping("/check-retry/{maHoaDon}")
    public ResponseEntity<?> checkPaymentRetry(@PathVariable String maHoaDon) {
        log.info("🔍 Checking payment retry status for: {}", maHoaDon);

        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);

            if (!hoaDonOpt.isPresent()) {
                return ResponseEntity.ok(Map.of(
                        "canRetry", false,
                        "message", "Không tìm thấy đơn hàng"
                ));
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Kiểm tra trạng thái hóa đơn
            if (hoaDon.getTrangThai() == null ||
                    (hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 5)) {
                return ResponseEntity.ok(Map.of(
                        "canRetry", false,
                        "message", "Đơn hàng không ở trạng thái cho phép thanh toán lại",
                        "currentStatus", hoaDon.getTrangThai()
                ));
            }

            // Kiểm tra lịch sử thanh toán
            List<LichSuThanhToan> lichSuList = lichSuThanhToanRepo.findByHoaDonId(hoaDon.getId());

            boolean hasSuccessfulPayment = lichSuList.stream()
                    .anyMatch(ls -> ls.getTrangThai() != null && ls.getTrangThai());

            if (hasSuccessfulPayment) {
                return ResponseEntity.ok(Map.of(
                        "canRetry", false,
                        "message", "Đơn hàng đã được thanh toán thành công"
                ));
            }

            // Có thể thanh toán lại
            Map<String, Object> response = new HashMap<>();
            response.put("canRetry", true);
            response.put("message", "Đơn hàng có thể thanh toán lại");
            response.put("hoaDon", Map.of(
                    "maHoaDon", hoaDon.getMaHoaDon(),
                    "tongTien", hoaDon.getTongTien(),
                    "tongTienSauGiam", hoaDon.getTongTienSauGiam(),
                    "trangThai", hoaDon.getTrangThai()
            ));

            log.info("✅ Order {} can be retried for payment", maHoaDon);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("❌ Error checking payment retry: ", e);
            return ResponseEntity.ok(Map.of(
                    "canRetry", false,
                    "message", "Lỗi khi kiểm tra trạng thái: " + e.getMessage()
            ));
        }
    }

    /**
     * Tạo lại payment URL cho đơn hàng chưa thanh toán
     */
    @PostMapping("/retry/{maHoaDon}")
    public ResponseEntity<?> retryPayment(
            @PathVariable String maHoaDon,
            HttpServletRequest request) {

        log.info("🔄 Retrying payment for order: {}", maHoaDon);

        try {
            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);

            if (!hoaDonOpt.isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Không tìm thấy đơn hàng"
                ));
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Kiểm tra trạng thái
            if (hoaDon.getTrangThai() == null ||
                    (hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 5)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Đơn hàng không ở trạng thái cho phép thanh toán lại"
                ));
            }

            // Kiểm tra đã thanh toán chưa
            List<LichSuThanhToan> lichSuList = lichSuThanhToanRepo.findByHoaDonId(hoaDon.getId());
            boolean hasSuccessfulPayment = lichSuList.stream()
                    .anyMatch(ls -> ls.getTrangThai() != null && ls.getTrangThai());

            if (hasSuccessfulPayment) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Đơn hàng đã được thanh toán"
                ));
            }

            // Tạo payment URL mới
            String paymentUrl = vnPayService.createPaymentUrl(hoaDon, request);

            log.info("✅ Payment URL created for retry: {}", maHoaDon);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Tạo link thanh toán thành công",
                    "paymentUrl", paymentUrl,
                    "maHoaDon", maHoaDon
            ));

        } catch (Exception e) {
            log.error("❌ Error retrying payment: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi tạo link thanh toán: " + e.getMessage()
            ));
        }
    }

    /**
     * Lấy danh sách đơn hàng chưa thanh toán của khách hàng
     */
    @GetMapping("/unpaid-orders/{khachHangId}")
    public ResponseEntity<?> getUnpaidOrders(@PathVariable Integer khachHangId) {
        log.info("📋 Getting unpaid orders for customer: {}", khachHangId);

        try {
            List<HoaDon> allOrders = hoaDonRepo.findByKhachHangIdOrderByNgayTaoDesc(khachHangId);

            // Lọc đơn hàng chưa thanh toán
            List<Map<String, Object>> unpaidOrders = allOrders.stream()
                    .filter(hd -> {
                        // Chỉ lấy đơn trạng thái 0 hoặc 5
                        if (hd.getTrangThai() == null ||
                                (hd.getTrangThai() != 0 && hd.getTrangThai() != 5)) {
                            return false;
                        }

                        // Kiểm tra chưa có thanh toán thành công
                        List<LichSuThanhToan> lichSuList =
                                lichSuThanhToanRepo.findByHoaDonId(hd.getId());

                        return lichSuList.stream()
                                .noneMatch(ls -> ls.getTrangThai() != null && ls.getTrangThai());
                    })
                    .map(hd -> {
                        Map<String, Object> orderInfo = new HashMap<>();
                        orderInfo.put("maHoaDon", hd.getMaHoaDon());
                        orderInfo.put("tongTien", hd.getTongTien());
                        orderInfo.put("tongTienSauGiam", hd.getTongTienSauGiam());
                        orderInfo.put("trangThai", hd.getTrangThai());
                        orderInfo.put("ngayTao", hd.getNgayTao());

                        // Lấy số lần thử thanh toán
                        List<LichSuThanhToan> lichSuList =
                                lichSuThanhToanRepo.findByHoaDonId(hd.getId());
                        orderInfo.put("paymentAttempts", lichSuList.size());

                        return orderInfo;
                    })
                    .toList();

            log.info("✅ Found {} unpaid orders", unpaidOrders.size());

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", unpaidOrders,
                    "total", unpaidOrders.size()
            ));

        } catch (Exception e) {
            log.error("❌ Error getting unpaid orders: ", e);
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy danh sách đơn hàng: " + e.getMessage()
            ));
        }
    }
}
