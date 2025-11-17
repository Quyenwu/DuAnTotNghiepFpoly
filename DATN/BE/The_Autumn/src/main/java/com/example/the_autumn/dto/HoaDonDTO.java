package com.example.the_autumn.dto;

import com.example.the_autumn.entity.HoaDon;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HoaDonDTO {

    private Integer id;
    private String maHoaDon;
    private KhachHangDTO khachHang;
    private Integer nhanVienId;
    private String nhanVienHoTen;
    private Integer phieuGiamGiaId;
    private Boolean loaiHoaDon;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienHienThi;
    private BigDecimal tongTienSauGiam;
    private Integer idPhieuGiamGia;
    private String tenVoucher;
    private String ghiChu;
    private String diaChiKhachHang;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngayThanhToan;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date ngayTao;
    private Integer trangThai;
    private List<HoaDonChiTietDTO> hoaDonChiTiets;
    /**
     * Constructor từ Entity
     */
    public HoaDonDTO(HoaDon hoaDon) {
        this.id = hoaDon.getId();
        this.maHoaDon = hoaDon.getMaHoaDon();

        // Xử lý khách hàng
        this.khachHang = hoaDon.getKhachHang() != null
                ? new KhachHangDTO(hoaDon.getKhachHang())
                : null;

        // Xử lý nhân viên
        this.nhanVienId = hoaDon.getNhanVien() != null
                ? hoaDon.getNhanVien().getId()
                : null;
        this.nhanVienHoTen = hoaDon.getNhanVien() != null
                ? hoaDon.getNhanVien().getHoTen()
                : null;

        // ⭐ XỬ LÝ PHIẾU GIẢM GIÁ & SỐ TIỀN HIỂN THỊ
        if (hoaDon.getPhieuGiamGia() != null) {
            // CÓ VOUCHER
            this.phieuGiamGiaId = hoaDon.getPhieuGiamGia().getId();
            this.idPhieuGiamGia = hoaDon.getPhieuGiamGia().getId();
            this.tenVoucher = hoaDon.getPhieuGiamGia().getTenChuongTrinh();

            // ⭐ Hiển thị tổng tiền SAU GIẢM
            this.tongTienHienThi = (hoaDon.getTongTienSauGiam() != null)
                    ? hoaDon.getTongTienSauGiam()
                    : hoaDon.getTongTien();
        } else {
            // KHÔNG CÓ VOUCHER
            this.phieuGiamGiaId = null;
            this.idPhieuGiamGia = null;
            this.tenVoucher = null;

            // ⭐ Hiển thị tổng tiền GỐC
            this.tongTienHienThi = hoaDon.getTongTien();
        }

        this.loaiHoaDon = hoaDon.getLoaiHoaDon();
        this.phiVanChuyen = hoaDon.getPhiVanChuyen();
        this.tongTien = hoaDon.getTongTien();
        this.tongTienSauGiam = hoaDon.getTongTienSauGiam();
        this.ghiChu = hoaDon.getGhiChu();
        this.diaChiKhachHang = hoaDon.getDiaChiKhachHang();
        this.ngayThanhToan = hoaDon.getNgayThanhToan();
        this.ngayTao = hoaDon.getNgayTao();
        this.trangThai = hoaDon.getTrangThai();

        // ✅ CONVERT DANH SÁCH CHI TIẾT SẢN PHẨM
        this.hoaDonChiTiets = hoaDon.getHoaDonChiTiets() != null
                ? hoaDon.getHoaDonChiTiets().stream()
                .map(HoaDonChiTietDTO::new)
                .collect(Collectors.toList())
                : null;
    }

    public boolean hasVoucher() {
        return this.idPhieuGiamGia != null && this.idPhieuGiamGia > 0;
    }
    public BigDecimal getTienDaGiam() {
        if (!hasVoucher()) {
            return BigDecimal.ZERO;
        }

        if (tongTien == null || tongTienSauGiam == null) {
            return BigDecimal.ZERO;
        }

        return tongTien.subtract(tongTienSauGiam);
    }
}
