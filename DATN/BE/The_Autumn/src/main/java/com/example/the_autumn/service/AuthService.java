package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.model.request.RegisterRequest;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    NhanVienRepository nvRepo;

    @Autowired
    KhachHangRepository khRepo;

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
            return "STAFF:" + nv.getId() + ":" + nv.getHoTen();
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

    public String register(RegisterRequest request) {
        try {
            if (isEmailExists(request.getEmail())) {
                throw new RuntimeException("Email đã tồn tại trong hệ thống");
            }

            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(request.getHoTen());
            khachHang.setEmail(request.getEmail());
            khachHang.setMatKhau(request.getPassword());
            khachHang.setSdt(request.getSdt());
            khachHang.setGioiTinh(request.getGioiTinh());
            khachHang.setNgaySinh(request.getNgaySinh());
            khachHang.setTrangThai(true);
            khachHang.setNgayTao(new Date());

            KhachHang savedKhachHang = khRepo.save(khachHang);

            return "CUSTOMER:" + savedKhachHang.getId() + ":" + savedKhachHang.getHoTen();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi đăng ký: " + e.getMessage());
        }
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