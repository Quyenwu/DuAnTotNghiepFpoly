package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHoaDonRequest {
    private Integer idKhachHang;
    private String hoTenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;
    private String diaChiCuThe;
    private Integer thanhPho;    // id tỉnh
    private Integer quan;        // id quận
    private Integer idDiaChi;
    private BigDecimal phiVanChuyen;
    private Integer idPhieuGiamGia;
    private String ghiChu;
    private Integer trangThai;
    private Boolean loaiHoaDon;
    private Integer idNhanVien;
    private Integer idPhuongThucThanhToan;
    private List<ChiTietSanPhamRequest> chiTietSanPhams;
    private BigDecimal tongTien;
    private BigDecimal phiPhu;
    private BigDecimal phiPhuMoi;
    private List<PhiPhuDetail> phiPhuDetails;
    private BigDecimal soTienThanhToan;  // Số tiền đã thanh toán
    private BigDecimal soTienCanThanhToan;
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietSanPhamRequest {
        private Integer id;  // null = thêm mới, có giá trị = update
        private Integer idChiTietSanPham;  // ID của ChiTietSanPham
        private Integer soLuong;
        private BigDecimal giaBan;  // Chỉ dùng giaBan
        private String ghiChu;
        // KHÔNG có giaSauGiam
    }

    @Data
    public static class PhiPhuDetail {
        private String loai;
        private String ten;
        private BigDecimal soTien;
        private String ghiChu;
    }
}