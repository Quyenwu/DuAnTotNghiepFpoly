package com.example.the_autumn.service;

import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.LichSuThanhToan;
import com.example.the_autumn.entity.PhuongThucThanhToan;
import com.example.the_autumn.repository.LichSuThanhToanRepository;
import com.example.the_autumn.repository.PhuongThucThanhToanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@Service
@Slf4j
public class LichSuThanhToanService {

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    /**
     * Tạo lịch sử thanh toán tạm thời cho VNPay (trạng thái false)
     */
    @Transactional
    public Integer createPendingVnPayPayment(HoaDon hoaDon) {
        try {
            Optional<PhuongThucThanhToan> ptttOpt = phuongThucThanhToanRepository.findByTenPhuongThucThanhToan("Chuyển khoản");

            if (!ptttOpt.isPresent()) {
                log.warn("⚠️ Payment method 'Chuyển khoản' not found");
                return null;
            }

            LichSuThanhToan lichSu = new LichSuThanhToan();
            lichSu.setHoaDon(hoaDon);
            lichSu.setPhuongThucThanhToan(ptttOpt.get());
            lichSu.setSoTien(hoaDon.getTongTienSauGiam());
            lichSu.setNgayThanhToan(new Date());
            lichSu.setTrangThai(false); // ⭐ TRẠNG THÁI TẠM THỜI (CHỜ)
            lichSu.setGhiChu("VNPAY - ĐANG CHỜ THANH TOÁN");

            LichSuThanhToan saved = lichSuThanhToanRepository.save(lichSu);
            log.info("✅ Created pending payment history with ID: {}", saved.getId());

            return saved.getId();

        } catch (Exception e) {
            log.error("❌ Error creating pending payment history: ", e);
            return null;
        }
    }
}