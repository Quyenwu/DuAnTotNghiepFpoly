package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BienTheTamResponse {
    // ✅ THÊM: ID tạm thời để frontend track
    private String id;

    // ✅ Thông tin hiển thị
    private String tenSanPham;
    private String tenMauSac;
    private String tenKichThuoc;
    private String tenTrongLuong;
    private String tenCoAo;
    private String tenTayAo;

    // ✅ QUAN TRỌNG: Các ID cần thiết để tạo sản phẩm
    private Integer idMauSac;
    private Integer idKichThuoc;
    private Integer idTrongLuong;
    private Integer idCoAo;
    private Integer idTayAo;

    // ✅ Thông tin giá (optional)
    private BigDecimal giaBan;
    private Integer soLuong;

    // Constructor để tạo nhanh
    public BienTheTamResponse(String tenSanPham,
                              String tenMauSac, Integer idMauSac,
                              String tenKichThuoc, Integer idKichThuoc,
                              String tenTrongLuong, Integer idTrongLuong,
                              String tenCoAo, Integer idCoAo,
                              String tenTayAo, Integer idTayAo) {
        this.id = "temp-" + System.currentTimeMillis() + "-" + (int)(Math.random() * 1000);
        this.tenSanPham = tenSanPham;
        this.tenMauSac = tenMauSac;
        this.idMauSac = idMauSac;
        this.tenKichThuoc = tenKichThuoc;
        this.idKichThuoc = idKichThuoc;
        this.tenTrongLuong = tenTrongLuong;
        this.idTrongLuong = idTrongLuong;
        this.tenCoAo = tenCoAo;
        this.idCoAo = idCoAo;
        this.tenTayAo = tenTayAo;
        this.idTayAo = idTayAo;
        this.giaBan = BigDecimal.ZERO;
        this.soLuong = 1;
    }
}