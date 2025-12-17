package com.example.the_autumn.controller;

import com.example.the_autumn.entity.LichSuHoaDon;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/lich-su-hoa-don")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174","http://172.20.10.2:5173"})
@Slf4j
public class LichSuHoaDonController {

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepo;

    /**
     * Lấy lịch sử đơn hàng theo mã hóa đơn
     * GET /api/lich-su-hoa-don/{maHoaDon}
     */
    @GetMapping("/{maHoaDon}")
    public ResponseEntity<?> getLichSuByMaHoaDon(@PathVariable String maHoaDon) {
        log.info("📋 Lấy lịch sử đơn hàng: {}", maHoaDon);
        try {
            List<LichSuHoaDon> lichSuList = lichSuHoaDonRepo
                    .findByHoaDonMaHoaDonOrderByNgayCapNhatDesc(maHoaDon);
            log.info("✅ Tìm thấy {} bản ghi lịch sử", lichSuList.size());
            return ResponseEntity.ok(lichSuList);
        } catch (Exception e) {
            log.error("❌ Lỗi khi lấy lịch sử: ", e);
            return ResponseEntity.ok(List.of());
        }
    }
}
















