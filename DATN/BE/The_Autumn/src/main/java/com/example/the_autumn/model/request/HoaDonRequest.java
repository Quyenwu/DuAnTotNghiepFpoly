package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class HoaDonRequest {

    private Integer idKhachHang;
    private Integer idNhanVien;
    private Integer idPhieuGiamGia;
    private Boolean loaiHoaDon;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private String ghiChu;
    private String diaChiKhachHang;
    private Date ngayThanhToan;
    private Integer nguoiTao;
    private Integer trangThai;
    private BigDecimal soTienThanhToan;
    private String ghiChuThanhToan;
    private List<HoaDonChiTietRequest> chiTietList;
    private Integer idPhuongThucThanhToan;

}
