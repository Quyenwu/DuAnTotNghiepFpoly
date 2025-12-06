package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class UpdateHoaDonResponse {
    private boolean success;
    private String message;
    private HoaDonDetailResponse data;
    private String diaChiCuThe;
    private Integer thanhPho;    // id tỉnh
    private Integer quan;        // id quận
    private BigDecimal phiPhu; // phụ phí đã thanh toán
    private BigDecimal phiPhuMoi; // phụ phí chờ thanh toán (nếu đã thanh toán trước đó)
    private BigDecimal tongTienCanThanhToan;
    private BigDecimal tongTienSanPham;
    private BigDecimal phiVanChuyen;
    private BigDecimal tienGiamGia;
    private BigDecimal tongTienSauGiam;
    private BigDecimal soTienThanhToan;  // Số tiền đã thanh toán
    private BigDecimal soTienCanThanhToan;
    // Constructor cơ bản
    public UpdateHoaDonResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.diaChiCuThe = null;
        this.thanhPho = null;
        this.quan = null;
        this.phiPhu = BigDecimal.ZERO;
        this.phiPhuMoi = BigDecimal.ZERO;
        this.tongTienCanThanhToan = BigDecimal.ZERO;
        this.tongTienSanPham = BigDecimal.ZERO;
        this.phiVanChuyen = BigDecimal.ZERO;
        this.tienGiamGia = BigDecimal.ZERO;
        this.soTienThanhToan = BigDecimal.ZERO;
        this.soTienCanThanhToan = BigDecimal.ZERO;
        this.tongTienSauGiam = BigDecimal.ZERO;
    }

    // Constructor với tất cả tham số
    public UpdateHoaDonResponse(boolean success, String message, HoaDonDetailResponse data,
                                String diaChiCuThe, Integer thanhPho, Integer quan,
                                BigDecimal phiPhu, BigDecimal phiPhuMoi, BigDecimal tongTienCanThanhToan,
                                BigDecimal tongTienSanPham, BigDecimal phiVanChuyen,
                                BigDecimal tienGiamGia, BigDecimal tongTienSauGiam,BigDecimal soTienThanhToan,BigDecimal soTienCanThanhToan) {
        this.success = success;
        this.message = message;
        this.data = data;
        this.diaChiCuThe = diaChiCuThe;
        this.thanhPho = thanhPho;
        this.quan = quan;
        this.phiPhu = phiPhu != null ? phiPhu : BigDecimal.ZERO;
        this.phiPhuMoi = phiPhuMoi != null ? phiPhuMoi : BigDecimal.ZERO;
        this.tongTienCanThanhToan = tongTienCanThanhToan != null ? tongTienCanThanhToan : BigDecimal.ZERO;
        this.tongTienSanPham = tongTienSanPham != null ? tongTienSanPham : BigDecimal.ZERO;
        this.phiVanChuyen = phiVanChuyen != null ? phiVanChuyen : BigDecimal.ZERO;
        this.tienGiamGia = tienGiamGia != null ? tienGiamGia : BigDecimal.ZERO;
        this.tongTienSauGiam = tongTienSauGiam != null ? tongTienSauGiam : BigDecimal.ZERO;
        this.soTienThanhToan = soTienThanhToan != null ? soTienThanhToan : BigDecimal.ZERO;

        this.soTienCanThanhToan = soTienCanThanhToan != null ? soTienCanThanhToan : BigDecimal.ZERO;

    }

    // Constructor với thông tin tài chính
    public UpdateHoaDonResponse(boolean success, String message,
                                BigDecimal tongTienSanPham, BigDecimal phiVanChuyen,
                                BigDecimal tienGiamGia, BigDecimal tongTienSauGiam) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.diaChiCuThe = null;
        this.thanhPho = null;
        this.quan = null;
        this.phiPhu = BigDecimal.ZERO;
        this.phiPhuMoi = BigDecimal.ZERO;
        this.tongTienCanThanhToan = BigDecimal.ZERO;
        this.tongTienSanPham = tongTienSanPham != null ? tongTienSanPham : BigDecimal.ZERO;
        this.phiVanChuyen = phiVanChuyen != null ? phiVanChuyen : BigDecimal.ZERO;
        this.tienGiamGia = tienGiamGia != null ? tienGiamGia : BigDecimal.ZERO;
        this.tongTienSauGiam = tongTienSauGiam != null ? tongTienSauGiam : BigDecimal.ZERO;
    }

    // Constructor với phụ phí
    public UpdateHoaDonResponse(boolean success, String message,
                                BigDecimal phiPhu, BigDecimal phiPhuMoi,
                                BigDecimal tongTienCanThanhToan) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.diaChiCuThe = null;
        this.thanhPho = null;
        this.quan = null;
        this.phiPhu = phiPhu != null ? phiPhu : BigDecimal.ZERO;
        this.phiPhuMoi = phiPhuMoi != null ? phiPhuMoi : BigDecimal.ZERO;
        this.tongTienCanThanhToan = tongTienCanThanhToan != null ? tongTienCanThanhToan : BigDecimal.ZERO;
        this.tongTienSanPham = BigDecimal.ZERO;
        this.phiVanChuyen = BigDecimal.ZERO;
        this.tienGiamGia = BigDecimal.ZERO;
        this.tongTienSauGiam = BigDecimal.ZERO;
    }

    // Builder pattern để dễ tạo đối tượng
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String message;
        private HoaDonDetailResponse data;
        private String diaChiCuThe;
        private Integer thanhPho;
        private Integer quan;
        private BigDecimal phiPhu = BigDecimal.ZERO;
        private BigDecimal phiPhuMoi = BigDecimal.ZERO;
        private BigDecimal tongTienCanThanhToan = BigDecimal.ZERO;
        private BigDecimal tongTienSanPham = BigDecimal.ZERO;
        private BigDecimal phiVanChuyen = BigDecimal.ZERO;
        private BigDecimal tienGiamGia = BigDecimal.ZERO;
        private BigDecimal tongTienSauGiam = BigDecimal.ZERO;
        private BigDecimal soTienThanhToan = BigDecimal.ZERO;
        private BigDecimal soTienCanThanhToan = BigDecimal.ZERO;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder data(HoaDonDetailResponse data) {
            this.data = data;
            return this;
        }

        public Builder diaChiCuThe(String diaChiCuThe) {
            this.diaChiCuThe = diaChiCuThe;
            return this;
        }

        public Builder thanhPho(Integer thanhPho) {
            this.thanhPho = thanhPho;
            return this;
        }

        public Builder quan(Integer quan) {
            this.quan = quan;
            return this;
        }

        public Builder phiPhu(BigDecimal phiPhu) {
            this.phiPhu = phiPhu != null ? phiPhu : BigDecimal.ZERO;
            return this;
        }

        public Builder phiPhuMoi(BigDecimal phiPhuMoi) {
            this.phiPhuMoi = phiPhuMoi != null ? phiPhuMoi : BigDecimal.ZERO;
            return this;
        }

        public Builder tongTienCanThanhToan(BigDecimal tongTienCanThanhToan) {
            this.tongTienCanThanhToan = tongTienCanThanhToan != null ? tongTienCanThanhToan : BigDecimal.ZERO;
            return this;
        }

        public Builder tongTienSanPham(BigDecimal tongTienSanPham) {
            this.tongTienSanPham = tongTienSanPham != null ? tongTienSanPham : BigDecimal.ZERO;
            return this;
        }

        public Builder phiVanChuyen(BigDecimal phiVanChuyen) {
            this.phiVanChuyen = phiVanChuyen != null ? phiVanChuyen : BigDecimal.ZERO;
            return this;
        }

        public Builder tienGiamGia(BigDecimal tienGiamGia) {
            this.tienGiamGia = tienGiamGia != null ? tienGiamGia : BigDecimal.ZERO;
            return this;
        }

        public Builder tongTienSauGiam(BigDecimal tongTienSauGiam) {
            this.tongTienSauGiam = tongTienSauGiam != null ? tongTienSauGiam : BigDecimal.ZERO;
            return this;
        }
        public Builder soTienThanhToan(BigDecimal soTienThanhToan) {
            this.soTienThanhToan = soTienThanhToan != null ? soTienThanhToan : BigDecimal.ZERO;
            return this;
        } public Builder soTienCanThanhToan(BigDecimal soTienCanThanhToan) {
            this.soTienCanThanhToan = soTienCanThanhToan != null ? soTienCanThanhToan : BigDecimal.ZERO;
            return this;
        }
        public UpdateHoaDonResponse build() {
            return new UpdateHoaDonResponse(
                    success, message, data, diaChiCuThe, thanhPho, quan,
                    phiPhu, phiPhuMoi, tongTienCanThanhToan,
                    tongTienSanPham, phiVanChuyen, tienGiamGia, tongTienSauGiam,soTienThanhToan,soTienCanThanhToan
            );
        }
    }

    // Phương thức tiện ích
    public boolean hasPhiPhuMoi() {
        return phiPhuMoi != null && phiPhuMoi.compareTo(BigDecimal.ZERO) > 0;
    }

    public BigDecimal getTongPhiPhu() {
        BigDecimal tong = BigDecimal.ZERO;
        if (phiPhu != null) tong = tong.add(phiPhu);
        if (phiPhuMoi != null) tong = tong.add(phiPhuMoi);
        return tong;
    }
}