package com.example.the_autumn.model.response;

import java.math.BigDecimal;
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


    private String tenNhanVien;
    private String sdtNhanVien;


    private Boolean loaiHoaDon;
    private String hinhThucThanhToan;
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTien;
    private BigDecimal tongTienSauGiam;
    private Integer trangThai;
    private String ghiChu;


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

    public String getTenNhanVien() { return tenNhanVien; }
    public void setTenNhanVien(String tenNhanVien) { this.tenNhanVien = tenNhanVien; }

    public String getSdtNhanVien() { return sdtNhanVien; }
    public void setSdtNhanVien(String sdtNhanVien) { this.sdtNhanVien = sdtNhanVien; }

    public Boolean getLoaiHoaDon() { return loaiHoaDon; }
    public void setLoaiHoaDon(Boolean loaiHoaDon) { this.loaiHoaDon = loaiHoaDon; }

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
        private Integer idChiTietSanPham;
        private String tenSanPham;
        private String mauSac;
        private String kichThuoc;
        private Integer soLuong;
        private BigDecimal giaBan;
        private BigDecimal thanhTien;
        private String ghiChu;

        private List<String> anhUrls;


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