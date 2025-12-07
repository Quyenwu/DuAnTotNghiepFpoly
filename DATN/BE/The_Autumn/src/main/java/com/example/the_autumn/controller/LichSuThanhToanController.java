package com.example.the_autumn.controller;

import com.example.the_autumn.entity.LichSuThanhToan;
import com.example.the_autumn.repository.LichSuThanhToanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lich-su-thanh-toan")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174","http://192.203.4.118:5173"})
@Slf4j
public class LichSuThanhToanController {

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepo;

    /**
     * Lấy lịch sử thanh toán theo mã hóa đơn
     * GET /api/lich-su-thanh-toan/{maHoaDon}
     */
    @GetMapping("/{maHoaDon}")
    public ResponseEntity<?> getLichSuThanhToanByMaHoaDon(@PathVariable String maHoaDon) {
        log.info("💳 Lấy lịch sử thanh toán: {}", maHoaDon);

        try {
            List<LichSuThanhToan> lichSuList = lichSuThanhToanRepo
                    .findByHoaDonMaHoaDonOrderByNgayThanhToanDesc(maHoaDon);

            log.info("✅ Tìm thấy {} bản ghi thanh toán", lichSuList.size());
            return ResponseEntity.ok(lichSuList);

        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy lịch sử thanh toán: ", e);
            return ResponseEntity.ok(List.of()); // Trả về list rỗng
        }
    }
}
