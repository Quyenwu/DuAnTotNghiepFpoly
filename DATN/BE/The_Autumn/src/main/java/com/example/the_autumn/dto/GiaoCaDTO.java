package com.example.the_autumn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GiaoCaDTO {
    private Integer id;
    private String maGiaoCa;
    private Integer idNhanVien;
    private String hoTenNhanVien;
    private String thoiGianBatDau;
    private String thoiGianKetThuc;
    private BigDecimal soTienBatDau;
    private BigDecimal soTienKetThuc;
    private BigDecimal tongDoanhThu;
    private BigDecimal soTienChenhLech;
    private String ghiChu;
    private Boolean trangThai;
    private String ngayTao;
}
