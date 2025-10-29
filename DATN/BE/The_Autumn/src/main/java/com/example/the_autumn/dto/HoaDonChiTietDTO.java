package com.example.the_autumn.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonChiTietDTO {
    private Integer id;
    private Integer idChiTietSanPham;
    private String maHoaDonChiTiet;
    private String tenSanPham;
    private String mauSac;
    private String kichThuoc;
    private String maVach;
    private Integer soLuong;
    private BigDecimal giaBan;
    private BigDecimal thanhTien;
    private String ghiChu;
    private Boolean trangThai;



}
