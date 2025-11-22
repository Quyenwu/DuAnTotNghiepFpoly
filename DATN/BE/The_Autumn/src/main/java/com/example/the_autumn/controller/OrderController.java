package com.example.the_autumn.controller;

import com.example.the_autumn.dto.HoaDonDTO;
import com.example.the_autumn.service.VnPayService;
import com.example.the_autumn.dto.OrderPlacementResponse;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.model.request.OrderRequest;
import com.example.the_autumn.service.OrderService;
import com.example.the_autumn.model.response.ResponseObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class OrderController {
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    @Autowired private OrderService orderService;
    @Autowired private VnPayService vnPayService;

    @PostMapping("/place-order")
    public ResponseEntity<ResponseObject> createOrder(
            @RequestBody OrderRequest request,
            HttpServletRequest httpServletRequest) {

        log.info("=== Nhận yêu cầu đặt hàng ===");
        log.info("Phương thức thanh toán: {}", request.getPaymentMethod());
        log.info("Số lượng sản phẩm: {}", request.getItems().size());
        try {
            HoaDon newOrder = orderService.placeOrder(request);
            log.info("✅ Tạo hóa đơn thành công: {}", newOrder.getMaHoaDon());
            String paymentUrl = null;
            if (request.getPaymentMethod().equalsIgnoreCase("Chuyển khoản")) {
                try {
                    paymentUrl = vnPayService.createPaymentUrl(newOrder, httpServletRequest);
                    log.info("✅ Tạo URL VNPAY thành công");
                } catch (Exception e) {
                    log.error("❌ Lỗi tạo URL VNPAY: ", e);
                    throw new RuntimeException("Lỗi khi tạo URL thanh toán: " + e.getMessage());
                }
            }
            OrderPlacementResponse responseData = new OrderPlacementResponse(newOrder, paymentUrl);

            String message = paymentUrl != null
                    ? "Đơn hàng đã tạo. Đang chuyển đến trang thanh toán..."
                    : "Đặt hàng thành công!";

            log.info("=== Xử lý đơn hàng hoàn tất ===");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(ResponseObject.success(responseData, message));

        } catch (RuntimeException e) {
            log.error("❌ Lỗi nghiệp vụ: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.error(e.getMessage()));

        } catch (Exception e) {
            log.error("❌ Lỗi hệ thống: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/customer/{customerId}")
    public ResponseObject<List<HoaDonDTO>> getOrdersForCustomer(@PathVariable Integer customerId) {
        try {
            log.info("📦 API: Lấy đơn hàng cho khách hàng ID: {}", customerId);
            List<HoaDonDTO> orders = orderService.getOrdersByCustomerId(customerId);
            log.info("✅ API: Trả về {} đơn hàng", orders.size());
            return new ResponseObject<>(orders, "Lấy danh sách đơn hàng thành công");
        } catch (RuntimeException e) {
            log.error("❌ Lỗi: {}", e.getMessage());
            return ResponseObject.error(e.getMessage());
        }
    }

    @GetMapping("/{maHoaDon}")
    public ResponseObject<HoaDonDTO> getOrderDetails(@PathVariable String maHoaDon) {
        try {
            log.info("🔍 API: Lấy chi tiết đơn hàng: {}", maHoaDon);
            HoaDonDTO order = orderService.getOrderByMaHoaDon(maHoaDon);
            return new ResponseObject<>(order, "Lấy chi tiết đơn hàng thành công");
        } catch (RuntimeException e) {
            log.error("❌ Không tìm thấy đơn hàng: {}", e.getMessage());
            return ResponseObject.error(e.getMessage());
        }
    }

    @PostMapping("/by-codes")
    public ResponseObject<List<HoaDonDTO>> getOrdersByCodeList(@RequestBody List<String> maHoaDonList) {
        log.info("📋 API: Nhận {} mã hóa đơn", maHoaDonList != null ? maHoaDonList.size() : 0);
        log.info("📋 Danh sách mã: {}", maHoaDonList);

        if (maHoaDonList == null || maHoaDonList.isEmpty()) {
            log.warn("⚠️ Danh sách mã rỗng");
            return new ResponseObject<>(new ArrayList<>(), "Danh sách rỗng");
        }
        try {
            List<HoaDonDTO> orders = orderService.getOrdersByMaHoaDonList(maHoaDonList);

            log.info("✅ API: Trả về {} hóa đơn cho frontend", orders.size());

            if (orders.isEmpty()) {
                log.warn("⚠️ Không tìm thấy hóa đơn nào");
                return new ResponseObject<>(new ArrayList<>(), "Không tìm thấy hóa đơn");
            }

            return new ResponseObject<>(orders, "Lấy danh sách đơn hàng thành công");

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy danh sách đơn hàng: {}", e.getMessage(), e);
            return ResponseObject.error("Đã xảy ra lỗi: " + e.getMessage());
        }
    }

    @GetMapping("/by-phone/{sdt}")
    public ResponseObject<List<HoaDonDTO>> getOrdersByPhone(@PathVariable String sdt) {
        try {
            log.info("📞 API: Tra cứu đơn hàng theo SĐT: {}", sdt);
            List<HoaDonDTO> orders = orderService.getOrdersByPhone(sdt);
            log.info("✅ API: Tìm thấy {} đơn hàng", orders.size());

            if (orders.isEmpty()) {
                return new ResponseObject<>(new ArrayList<>(), "Không tìm thấy đơn hàng với số điện thoại này");
            }

            return new ResponseObject<>(orders, "Tìm thấy " + orders.size() + " đơn hàng");
        } catch (RuntimeException e) {
            log.error("❌ Lỗi: {}", e.getMessage());
            return ResponseObject.error(e.getMessage());
        }
    }

    @PutMapping("/{maHoaDon}/cancel")
    public ResponseEntity<ResponseObject> cancelOrder(
            @PathVariable String maHoaDon,
            @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            log.info("🚫 API: Hủy đơn hàng: {} - Lý do: {}", maHoaDon, reason);

            HoaDonDTO cancelledOrder = orderService.cancelOrder(maHoaDon, reason);

            return ResponseEntity.ok(ResponseObject.success(
                    cancelledOrder,
                    "Đã hủy đơn hàng thành công"
            ));
        } catch (RuntimeException e) {
            log.error("❌ Lỗi hủy đơn: {}", e.getMessage());
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(ResponseObject.error(e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Lỗi hệ thống: ", e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ResponseObject.error("Lỗi hệ thống: " + e.getMessage()));
        }
    }

}