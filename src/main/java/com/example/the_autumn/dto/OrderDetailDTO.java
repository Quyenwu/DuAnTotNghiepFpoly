package com.example.the_autumn.model.response;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * DTO chi tiết đầy đủ của một đơn hàng.
 * (Dùng cho đầu ra của hàm getOrderByMaHoaDon)
 */
public class OrderDetailDTO {

    // Thông tin hóa đơn
    private String maHoaDon;
    private Date ngayTao;
    private Integer trangThai;
    private String diaChiKhachHang;
    private BigDecimal tongTien; // Tiền hàng
    private BigDecimal phiVanChuyen;
    private BigDecimal tongTienSauGiam; // Tổng cuối cùng
    private String ghiChu;

    // Thông tin khách hàng
    private String tenKhachHang;
    private String sdtKhachHang;

    // Thông tin thanh toán
    private String tenPhuongThucThanhToan;

    private List<OrderItemDTO> items;

    public static class OrderItemDTO {
        private String tenSanPham;
        private String tenMauSac;
        private String tenKichThuoc;
        private String hinhAnh; // Lấy 1 ảnh đại diện

        private Integer soLuong;
        private BigDecimal giaBan; // Giá tại thời điểm mua
        private BigDecimal thanhTien;

        // Constructors, Getters, Setters...
    }
}