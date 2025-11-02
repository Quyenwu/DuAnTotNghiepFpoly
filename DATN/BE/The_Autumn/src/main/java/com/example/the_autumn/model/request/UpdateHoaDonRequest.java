package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
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

    // Thông tin khách hàng
    private Integer idKhachHang;
    private String hoTenKhachHang;
    private String sdtKhachHang;
    private String emailKhachHang;
    private String diaChiKhachHang;

    // Thông tin hóa đơn
    private BigDecimal phiVanChuyen;
    private Integer idPhieuGiamGia;  // null = không dùng phiếu
    private String ghiChu;
    private String hinhThucThanhToan;
    private Integer trangThai;        // ✅ thêm mới
    private String tenNhanVien;
    // Danh sách sản phẩm
    private List<ChiTietSanPhamRequest> chiTietSanPhams;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChiTietSanPhamRequest {
        private Integer id;  // null = thêm mới, có giá trị = update
        private Integer idChiTietSanPham;  // ID của ChiTietSanPham
        private Integer soLuong;
        private BigDecimal giaBan;
        private String ghiChu;
    }
}
