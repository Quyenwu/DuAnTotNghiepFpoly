package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class AuthService {

    @Autowired
    NhanVienRepository nvRepo;

    @Autowired
    KhachHangRepository khRepo;

    /**
     * ✅ Method login cũ - giữ nguyên để tương thích
     */
    public String login(String email, String password) {
        List<NhanVien> allNhanVien = nvRepo.findAll();
        Optional<NhanVien> nvOpt = allNhanVien.stream()
                .filter(nv -> email.equals(nv.getEmail()) &&
                        nv.getTrangThai() != null &&
                        nv.getTrangThai() &&
                        nv.getMatKhau().equals(password))
                .findFirst();

        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();
            return "STAFF:" + nv.getId() + ":" + nv.getHoTen() + ":" + nv.getChucVu().getId();
        }

        Optional<KhachHang> khOpt = khRepo.findByEmail(email);
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            if (kh.getTrangThai() != null && kh.getTrangThai() &&
                    kh.getMatKhau().equals(password)) {
                return "CUSTOMER:" + kh.getId() + ":" + kh.getHoTen();
            }
        }

        return null;
    }

    /**
     * ✅ Method mới - Trả về User Object để lấy đầy đủ thông tin
     */
    public Object authenticateUser(String email, String password) {
        log.info("🔐 Authenticating user - Email: {}", email);

        // Thử nhân viên trước
        List<NhanVien> allNhanVien = nvRepo.findAll();
        Optional<NhanVien> nvOpt = allNhanVien.stream()
                .filter(nv -> email.equals(nv.getEmail()) &&
                        nv.getTrangThai() != null &&
                        nv.getTrangThai() &&
                        nv.getMatKhau().equals(password))
                .findFirst();

        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();
            log.info("✅ Staff authenticated - ID: {}, Email: {}", nv.getId(), nv.getEmail());
            return nv;
        }

        // Thử khách hàng
        Optional<KhachHang> khOpt = khRepo.findByEmail(email);
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();
            if (kh.getTrangThai() != null && kh.getTrangThai() &&
                    kh.getMatKhau().equals(password)) {
                log.info("✅ Customer authenticated - ID: {}, Email: {}", kh.getId(), kh.getEmail());
                return kh;
            }
        }

        log.warn("❌ Authentication failed for email: {}", email);
        return null;
    }

    public boolean isEmailExists(String email) {
        boolean existsInNhanVien = nvRepo.findAll().stream()
                .anyMatch(nv -> email.equals(nv.getEmail()));

        boolean existsInKhachHang = khRepo.findByEmail(email).isPresent();

        return existsInNhanVien || existsInKhachHang;
    }

    public KhachHang registerKhachHang(KhachHang khachHang) {
        if (isEmailExists(khachHang.getEmail())) {
            throw new RuntimeException("Email đã tồn tại trong hệ thống");
        }

        if (khachHang.getTrangThai() == null) {
            khachHang.setTrangThai(true);
        }
        if (khachHang.getNgayTao() == null) {
            khachHang.setNgayTao(new Date());
        }

        return khRepo.save(khachHang);
    }

    public Object getUserFromToken(String token) {
        if (token == null || !token.contains(":")) {
            return null;
        }

        try {
            String[] parts = token.split(":");
            String userType = parts[0];
            Integer userId = Integer.parseInt(parts[1]);

            if ("STAFF".equals(userType)) {
                return nvRepo.findById(userId).orElse(null);
            } else if ("CUSTOMER".equals(userType)) {
                return khRepo.findById(userId).orElse(null);
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }
}