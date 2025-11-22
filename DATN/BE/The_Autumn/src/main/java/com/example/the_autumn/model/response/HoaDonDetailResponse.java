package com.example.the_autumn.model.response;

import jakarta.persistence.Column;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

public class HoaDonDetailResponse {
    private Integer id;
    private String maHoaDon;

    private Date ngayTao;
    private Date ngayThanhToan;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;
    private String diaChiKhachHang;
    private Integer idNhanVien;
    private String maNhanVien;
    private String tenNhanVien;
    private String sdtNhanVien;
    private String maGiamGia;
    private String tenChuongTrinh;
<<<<<<< HEAD
=======

    public Boolean getLoaiGiamGia() {
        return loaiGiamGia;
    }

    public void setLoaiGiamGia(Boolean loaiGiamGia) {
        this.loaiGiamGia = loaiGiamGia;
    }

    private BigDecimal giaTriGiamGia;
    private Boolean loaiGiamGia;
    private BigDecimal mucGiaGiamToiDa;
    private BigDecimal giaTriDonHangToiThieu;
    private Integer soLuongDung;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Integer trangThaiPhieuGiamGia;

    public Integer getSoLuongDung() {
        return soLuongDung;
    }


    public void setSoLuongDung(Integer soLuongDung) {
        this.soLuongDung = soLuongDung;
    }

    public LocalDate getNgayBatDau() {
        return ngayBatDau;
    }

    public void setNgayBatDau(LocalDate ngayBatDau) {
        this.ngayBatDau = ngayBatDau;
    }

    public LocalDate getNgayKetThuc() {
        return ngayKetThuc;
    }

    public void setNgayKetThuc(LocalDate ngayKetThuc) {
        this.ngayKetThuc = ngayKetThuc;
    }

    public Integer getTrangThaiPhieuGiamGia() {
        return trangThaiPhieuGiamGia;
    }

    public void setTrangThaiPhieuGiamGia(Integer trangThaiPhieuGiamGia) {
        this.trangThaiPhieuGiamGia = trangThaiPhieuGiamGia;
    }

>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753
    private String maGiaoDich;
    private BigDecimal soTien;
    private String ghiChu;
    private Boolean loaiHoaDon;
    private Integer idPhuongThucThanhToan;
    private String hinhThucThanhToan;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private Integer trangThai;
    private String ghiChuThanhToan;

    private KhachHangResponse khachHang;

<<<<<<< HEAD
=======
    public BigDecimal getGiaTriGiamGia() {
        return giaTriGiamGia;
    }

    public void setGiaTriGiamGia(BigDecimal giaTriGiamGia) {
        this.giaTriGiamGia = giaTriGiamGia;
    }

    public BigDecimal getMucGiaGiamToiDa() {
        return mucGiaGiamToiDa;
    }

    public void setMucGiaGiamToiDa(BigDecimal mucGiaGiamToiDa) {
        this.mucGiaGiamToiDa = mucGiaGiamToiDa;
    }

    public BigDecimal getGiaTriDonHangToiThieu() {
        return giaTriDonHangToiThieu;
    }

    public void setGiaTriDonHangToiThieu(BigDecimal giaTriDonHangToiThieu) {
        this.giaTriDonHangToiThieu = giaTriDonHangToiThieu;
    }

>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753
    public KhachHangResponse getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHangResponse khachHang) {
        this.khachHang = khachHang;
    }


    public String getTenChuongTrinh() {
        return tenChuongTrinh;
    }

    public void setTenChuongTrinh(String tenChuongTrinh) {
        this.tenChuongTrinh = tenChuongTrinh;
    }

    public String getMaGiamGia() {
        return maGiamGia;
    }

    public void setMaGiamGia(String maGiamGia) {
        this.maGiamGia = maGiamGia;
    }

    public String getMaGiaoDich() {
        return maGiaoDich;
    }

    public void setMaGiaoDich(String maGiaoDich) {
        this.maGiaoDich = maGiaoDich;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getGhiChuThanhToan() {
        return ghiChuThanhToan;
    }

    public void setGhiChuThanhToan(String ghiChuThanhToan) {
        this.ghiChuThanhToan = ghiChuThanhToan;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    private List<ChiTietSanPhamResponse> chiTietSanPhams;

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getMaHoaDon() { return maHoaDon; }
    public void setMaHoaDon(String maHoaDon) { this.maHoaDon = maHoaDon; }

    public Date getNgayTao() { return ngayTao; }
    public void setNgayTao(Date ngayTao) { this.ngayTao = ngayTao; }

    public Date getNgayThanhToan() { return ngayThanhToan; }
    public void setNgayThanhToan(Date ngayThanhToan) { this.ngayThanhToan = ngayThanhToan; }

    public String getTenKhachHang() { return tenKhachHang; }
    public void setTenKhachHang(String tenKhachHang) { this.tenKhachHang = tenKhachHang; }

    public String getSdtKhachHang() { return sdtKhachHang; }
    public void setSdtKhachHang(String sdtKhachHang) { this.sdtKhachHang = sdtKhachHang; }

    public String getEmailKhachHang() { return emailKhachHang; }
    public void setEmailKhachHang(String emailKhachHang) { this.emailKhachHang = emailKhachHang; }

    public String getDiaChiKhachHang() { return diaChiKhachHang; }
    public void setDiaChiKhachHang(String diaChiKhachHang) { this.diaChiKhachHang = diaChiKhachHang; }

    public Integer getIdNhanVien() { return idNhanVien; }
    public void setIdNhanVien(Integer idNhanVien) { this.idNhanVien = idNhanVien; }

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getSdtNhanVien() { return sdtNhanVien; }
    public void setSdtNhanVien(String sdtNhanVien) { this.sdtNhanVien = sdtNhanVien; }

    public Boolean getLoaiHoaDon() { return loaiHoaDon; }
    public void setLoaiHoaDon(Boolean loaiHoaDon) { this.loaiHoaDon = loaiHoaDon; }

    public Integer getIdPhuongThucThanhToan() { return idPhuongThucThanhToan; }
    public void setIdPhuongThucThanhToan(Integer idPhuongThucThanhToan) { this.idPhuongThucThanhToan = idPhuongThucThanhToan; }

    public String getHinhThucThanhToan() { return hinhThucThanhToan; }
    public void setHinhThucThanhToan(String hinhThucThanhToan) { this.hinhThucThanhToan = hinhThucThanhToan; }

    public BigDecimal getPhiVanChuyen() { return phiVanChuyen; }
    public void setPhiVanChuyen(BigDecimal phiVanChuyen) { this.phiVanChuyen = phiVanChuyen; }

    public BigDecimal getTongTien() { return tongTien; }
    public void setTongTien(BigDecimal tongTien) { this.tongTien = tongTien; }

    public BigDecimal getTongTienSauGiam() { return tongTienSauGiam; }
    public void setTongTienSauGiam(BigDecimal tongTienSauGiam) { this.tongTienSauGiam = tongTienSauGiam; }

    public Integer getTrangThai() { return trangThai; }
    public void setTrangThai(Integer trangThai) { this.trangThai = trangThai; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public List<ChiTietSanPhamResponse> getChiTietSanPhams() { return chiTietSanPhams; }
    public void setChiTietSanPhams(List<ChiTietSanPhamResponse> chiTietSanPhams) { this.chiTietSanPhams = chiTietSanPhams; }


    public static class ChiTietSanPhamResponse {
        private Integer id;
        private Integer idChiTietSanPham;
        private String tenSanPham;
        private String mauSac;
        private String kichThuoc;
        private Integer soLuong;
        private BigDecimal giaBan;
        private BigDecimal thanhTien;
        private String ghiChu;
        private String maVach;
        private BigDecimal giaSauGiam;

        public BigDecimal getGiaSauGiam() {
            return giaSauGiam;
        }

        public void setGiaSauGiam(BigDecimal giaSauGiam) {
            this.giaSauGiam = giaSauGiam;
        }

        public String getMaVach() {
            return maVach;
        }

        public void setMaVach(String maVach) {
            this.maVach = maVach;
        }

        private List<String> anhUrls;

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public Integer getIdChiTietSanPham() { return idChiTietSanPham; }
        public void setIdChiTietSanPham(Integer idChiTietSanPham) { this.idChiTietSanPham = idChiTietSanPham; }

        public String getTenSanPham() { return tenSanPham; }
        public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

        public String getMauSac() { return mauSac; }
        public void setMauSac(String mauSac) { this.mauSac = mauSac; }

        public String getKichThuoc() { return kichThuoc; }
        public void setKichThuoc(String kichThuoc) { this.kichThuoc = kichThuoc; }

        public Integer getSoLuong() { return soLuong; }
        public void setSoLuong(Integer soLuong) { this.soLuong = soLuong; }

        public BigDecimal getGiaBan() { return giaBan; }
        public void setGiaBan(BigDecimal giaBan) { this.giaBan = giaBan; }

        public BigDecimal getThanhTien() { return thanhTien; }
        public void setThanhTien(BigDecimal thanhTien) { this.thanhTien = thanhTien; }

        public String getGhiChu() { return ghiChu; }
        public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

        public List<String> getAnhUrls() { return anhUrls; }
        public void setAnhUrls(List<String> anhUrls) { this.anhUrls = anhUrls; }



    }






}