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

    private String loaiHoaDonText;
    private String hinhThucThanhToan;


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
        this.loaiHoaDon = hoaDon.getLoaiHoaDon();

        // ⭐ Convert KhachHang entity → KhachHangDTO (đầy đủ fields từ DB: id, hoTen, maKhachHang, soDienThoai, email, trangThai)
        if (hoaDon.getKhachHang() != null) {
            this.khachHang = new KhachHangDTO(hoaDon.getKhachHang());  // Giả sử KhachHangDTO có constructor từ entity (thêm nếu chưa)
            // Hoặc thủ công nếu chưa có constructor:
            // this.khachHang = new KhachHangDTO();
            // this.khachHang.setId(hoaDon.getKhachHang().getId());
            // this.khachHang.setHoTen(hoaDon.getKhachHang().getHoTen());
            // this.khachHang.setMaKhachHang(hoaDon.getKhachHang().getMaKhachHang());
            // this.khachHang.setSoDienThoai(hoaDon.getKhachHang().getSoDienThoai());
            // this.khachHang.setEmail(hoaDon.getKhachHang().getEmail());
            // this.khachHang.setTrangThai(hoaDon.getKhachHang().getTrangThai());
        }

        // ⭐ Convert NhanVien entity → NhanVienDTO (đầy đủ: id, hoTen, maNhanVien, email, trangThai)
        if (hoaDon.getNhanVien() != null) {
            this.nhanVien = new NhanVienDTO(hoaDon.getNhanVien());  // Giả sử có constructor từ entity
            // Hoặc thủ công:
            // this.nhanVien = new NhanVienDTO();
            // this.nhanVien.setId(hoaDon.getNhanVien().getId());
            // this.nhanVien.setHoTen(hoaDon.getNhanVien().getHoTen());
            // this.nhanVien.setMaNhanVien(hoaDon.getNhanVien().getMaNhanVien());
            // this.nhanVien.setEmail(hoaDon.getNhanVien().getEmail());
            // this.nhanVien.setTrangThai(hoaDon.getNhanVien().getTrangThai());
        }

        if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
            this.hinhThucThanhToan = hoaDon.getHinhThucThanhToans().get(0)
                    .getPhuongThucThanhToan()
                    .getTenPhuongThucThanhToan();
        }


    }


}