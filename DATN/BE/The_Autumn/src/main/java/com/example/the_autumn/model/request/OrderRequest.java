package com.example.the_autumn.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
@Data
public class OrderRequest {
    private Integer khachHangId;
    private Integer phieuGiamGiaId;
    private String hoTen;
    private String sdt;
    private String diaChiKhachHang;
    private Integer tinhId;
    private Integer quanId;
    // Thông tin đơn hàng
    private BigDecimal tongTien;
    private BigDecimal tienGiam;
    private BigDecimal phiVanChuyen;
    private String paymentMethod;
    private List<CartItemDTO> items;

    private String email;
    public Integer getKhachHangId() {
        return khachHangId;
    }

    public void setKhachHangId(Integer khachHangId) {
        this.khachHangId = khachHangId;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChiKhachHang() {
        return diaChiKhachHang;
    }

    public void setDiaChiKhachHang(String diaChiKhachHang) {
        this.diaChiKhachHang = diaChiKhachHang;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public BigDecimal getPhiVanChuyen() {
        return phiVanChuyen;
    }

    public void setPhiVanChuyen(BigDecimal phiVanChuyen) {
        this.phiVanChuyen = phiVanChuyen;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public List<CartItemDTO> getItems() {
        return items;
    }

    public void setItems(List<CartItemDTO> items) {
        this.items = items;
    }

    // Lớp nội (inner class) cho các sản phẩm trong giỏ hàng
    public static class CartItemDTO {
        private Integer id; // Đây là ID của chi_tiet_san_pham
        private int quantity;

        // Getters and Setters
        // ... (Tự động tạo getters và setters)

        public Integer getId() {
            return id;
        }

        public void setId(Integer id) {
            this.id = id;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }
    }
}