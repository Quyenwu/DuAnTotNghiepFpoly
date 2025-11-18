package com.example.the_autumn.dto;

import com.example.the_autumn.entity.ChiTietSanPham;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSanPhamDTO {

    private Integer id;
    private String maVach;
    private Integer soLuongTon;
    private BigDecimal giaBan;
    private String moTa;

    // ✅ THÔNG TIN SẢN PHẨM
    private String tenSanPham;

    // ✅ MÀU SẮC
    private String tenMauSac;

    // ✅ KÍCH THƯỚC
    private String tenKichThuoc;

    // ✅ DANH SÁCH ẢNH
    private List<String> anhs;

    public ChiTietSanPhamDTO(ChiTietSanPham ctsp) {
        this.id = ctsp.getId();
        this.maVach = ctsp.getMaVach();
        this.soLuongTon = ctsp.getSoLuongTon();
        this.giaBan = ctsp.getGiaBan();
        this.moTa = ctsp.getMoTa();

        // ✅ Tên sản phẩm
        this.tenSanPham = ctsp.getSanPham() != null
                ? ctsp.getSanPham().getTenSanPham()
                : null;

        // ✅ Màu sắc
        this.tenMauSac = ctsp.getMauSac() != null
                ? ctsp.getMauSac().getTenMauSac()
                : null;

        // ✅ Kích thước
        this.tenKichThuoc = ctsp.getKichThuoc() != null
                ? ctsp.getKichThuoc().getTenKichThuoc()
                : null;

        // ✅ Danh sách ảnh
        this.anhs = ctsp.getAnhs() != null
                ? ctsp.getAnhs().stream()
                .map(anh -> anh.getDuongDanAnh())
                .collect(Collectors.toList())
                : null;
    }
}