// ThanhToanController.java
package com.example.the_autumn.controller;

import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.model.request.ThanhToanRequest;
import com.example.the_autumn.model.response.ThanhToanResponse;
import com.example.the_autumn.service.HoaDonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/thanh-toan")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://192.203.4.118:5173"})
public class ThanhToanController {

    @Autowired
    private HoaDonService hoaDonService;

    @GetMapping("/thong-tin/{idHoaDon}")
    public ResponseEntity<?> getThongTinThanhToan(@PathVariable Integer idHoaDon) {
        try {
            ThanhToanResponse response = hoaDonService.getThongTinThanhToan(idHoaDon);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    // API 2: Cập nhật thanh toán (có thể thanh toán một phần hoặc toàn bộ)
    @PostMapping("/cap-nhat")
    public ResponseEntity<?> updateThanhToan(@RequestBody ThanhToanRequest request) {
        try {
            // Validate request
            if (request.getIdHoaDon() == null) {
                return ResponseEntity.badRequest().body("ID hóa đơn không được để trống");
            }

            if (request.getSoTienThanhToan() == null || request.getSoTienThanhToan().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                return ResponseEntity.badRequest().body("Số tiền thanh toán phải lớn hơn 0");
            }

            ThanhToanResponse response = hoaDonService.updateThanhToan(request);
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    // API 3: Thanh toán toàn bộ (tiện ích)
    @PostMapping("/toan-bo/{idHoaDon}")
    public ResponseEntity<?> thanhToanToanBo(
            @PathVariable Integer idHoaDon,
            @RequestParam(required = false) Integer idPhuongThucThanhToan,
            @RequestParam(required = false) Integer idNhanVienThucHien) {
        try {
            ThanhToanResponse response = hoaDonService.thanhToanToanBo(
                    idHoaDon,
                    idPhuongThucThanhToan,
                    idNhanVienThucHien
            );
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }

    // API 4: Kiểm tra trạng thái thanh toán
    @GetMapping("/trang-thai/{idHoaDon}")
    public ResponseEntity<?> checkTrangThaiThanhToan(@PathVariable Integer idHoaDon) {
        try {
            HoaDon hoaDon = hoaDonService.getById(idHoaDon)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

            java.math.BigDecimal tongTienSauGiam = hoaDon.getTongTienSauGiam() != null ?
                    hoaDon.getTongTienSauGiam() : java.math.BigDecimal.ZERO;

            java.math.BigDecimal daThanhToan = hoaDon.getSoTienThanhToan() != null ?
                    hoaDon.getSoTienThanhToan() : java.math.BigDecimal.ZERO;

            java.math.BigDecimal conLai = tongTienSauGiam.subtract(daThanhToan);
            if (conLai.compareTo(java.math.BigDecimal.ZERO) < 0) {
                conLai = java.math.BigDecimal.ZERO;
            }

            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("idHoaDon", idHoaDon);
            response.put("maHoaDon", hoaDon.getMaHoaDon());
            response.put("tongTienSauGiam", tongTienSauGiam);
            response.put("daThanhToan", daThanhToan);
            response.put("conLai", conLai);
            response.put("trangThai", hoaDon.getTrangThai());
            response.put("daThanhToanDu", daThanhToan.compareTo(tongTienSauGiam) >= 0);

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi server: " + e.getMessage());
        }
    }
}