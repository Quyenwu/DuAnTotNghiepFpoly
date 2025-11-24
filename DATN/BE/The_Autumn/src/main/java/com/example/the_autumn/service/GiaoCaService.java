package com.example.the_autumn.service;

import com.example.the_autumn.entity.GiaoCa;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.repository.GiaoCaRepository;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Service
public class GiaoCaService {

    @Autowired
    private GiaoCaRepository giaoCaRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    public GiaoCa batDauCa(Integer nhanVienId, BigDecimal soTienBatDau, String ghiChu) {

        NhanVien nv = nhanVienRepository.findById(nhanVienId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        GiaoCa caDangHoatDong = giaoCaRepository
                .findByNhanVienIdAndTrangThai(nhanVienId, true)
                .orElse(null);

        if (caDangHoatDong != null) {
            throw new RuntimeException("Bạn đang có ca chưa kết thúc!");
        }

        GiaoCa caMoi = new GiaoCa();
        caMoi.setNhanVien(nv);
        caMoi.setSoTienBatDau(soTienBatDau);
        caMoi.setTrangThai(true);
        caMoi.setGhiChu(ghiChu);
        caMoi.setThoiGianBatDau(LocalDateTime.now());
        return giaoCaRepository.save(caMoi);
    }


    public GiaoCa ketThucCa(Integer nhanVienId, BigDecimal soTienKetThuc, String ghiChu) {

        GiaoCa ca = giaoCaRepository
                .findByNhanVienIdAndTrangThai(nhanVienId, true)
                .orElseThrow(() -> new RuntimeException("Bạn chưa bắt đầu ca"));

        BigDecimal tongDoanhThu = hoaDonRepository
                .tinhTongTienTheoCa(nhanVienId, ca.getThoiGianBatDau());

        if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;

        BigDecimal duKien = ca.getSoTienBatDau().add(tongDoanhThu);
        BigDecimal chenhLech = soTienKetThuc.subtract(duKien);

        ca.setSoTienKetThuc(soTienKetThuc);
        ca.setTongDoanhThu(tongDoanhThu);
        ca.setSoTienChenhLech(chenhLech);

        ca.setThoiGianKetThuc(LocalDateTime.now());

        ca.setTrangThai(false);

        String old = ca.getGhiChu() == null ? "" : ca.getGhiChu();
        ca.setGhiChu(old + " | " + ghiChu);

        return giaoCaRepository.save(ca);
    }



    public GiaoCa getCaDangHoatDong(Integer nhanVienId) {
        return giaoCaRepository
                .findByNhanVienIdAndTrangThai(nhanVienId, true)
                .orElse(null);
    }
}
