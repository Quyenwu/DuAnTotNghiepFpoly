package com.example.the_autumn.model.response;

import com.example.the_autumn.dto.KhachHangDTO;
import com.example.the_autumn.dto.NhanVienDTO;
import com.example.the_autumn.entity.HoaDon;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonRespone {
    private Integer id;
    private String maHoaDon;
    private Boolean loaiHoaDon;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private String ghiChu;
    private String diaChiKhachHang;
    private Date ngayThanhToan;
    private Date ngayTao;
    private Date ngaySua;
    private Integer trangThai;

    private String hinhThucThanhToan;
    private BigDecimal soTienThanhToan;
    private KhachHangDTO khachHang;
    private NhanVienDTO nhanVien;

    public HoaDonRespone(HoaDon hoaDon) {
        if (hoaDon == null) {
            return;
        }

        this.id = hoaDon.getId();
        this.maHoaDon = hoaDon.getMaHoaDon();
        this.loaiHoaDon = hoaDon.getLoaiHoaDon();
        this.phiVanChuyen = hoaDon.getPhiVanChuyen();
        this.tongTien = hoaDon.getTongTien();
        this.tongTienSauGiam = hoaDon.getTongTienSauGiam();
        this.ghiChu = hoaDon.getGhiChu();
        this.diaChiKhachHang = hoaDon.getDiaChiKhachHang();
        this.ngayThanhToan = hoaDon.getNgayThanhToan();
        this.ngayTao = hoaDon.getNgayTao();
        this.ngaySua = hoaDon.getNgaySua();
        this.trangThai = hoaDon.getTrangThai();
        this.soTienThanhToan = hoaDon.getSoTienThanhToan();

        this.loaiHoaDon = hoaDon.getLoaiHoaDon();
        if (hoaDon.getKhachHang() != null) {
            this.khachHang = new KhachHangDTO(hoaDon.getKhachHang());
        }

        if (hoaDon.getNhanVien() != null) {
            this.nhanVien = new NhanVienDTO(hoaDon.getNhanVien());
        }

        if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
            this.hinhThucThanhToan = hoaDon.getHinhThucThanhToans().get(0)
                    .getPhuongThucThanhToan()
                    .getTenPhuongThucThanhToan();
        }

    }

}