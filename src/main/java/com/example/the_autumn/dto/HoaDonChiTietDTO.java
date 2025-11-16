package com.example.the_autumn.dto;

import com.example.the_autumn.entity.HoaDonChiTiet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietDTO {

    private Integer id;
    private String maHoaDonChiTiet;
    private Integer soLuong;
    private BigDecimal giaBan;
    private BigDecimal thanhTien;
    private String ghiChu;
    private ChiTietSanPhamDTO chiTietSanPham;
    public HoaDonChiTietDTO(HoaDonChiTiet hdct) {
        this.id = hdct.getId();
        this.maHoaDonChiTiet = hdct.getMaHoaDonChiTiet();
        this.soLuong = hdct.getSoLuong();
        this.giaBan = hdct.getGiaBan();
        this.thanhTien = hdct.getThanhTien();
        this.ghiChu = hdct.getGhiChu();

        // ✅ Convert chi tiết sản phẩm
        this.chiTietSanPham = hdct.getChiTietSanPham() != null
                ? new ChiTietSanPhamDTO(hdct.getChiTietSanPham())
                : null;
    }
}