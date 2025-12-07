package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class ThanhToanPhiPhuResponse {
    private boolean success;
    private String message;
    private BigDecimal soTienDaThanhToan;
    private BigDecimal tongTienSauCapNhat;
    private Date ngayThanhToan;
    private String phuongThucThanhToan;
    private String maGiaoDich;

    // Constructor cơ bản
    public ThanhToanPhiPhuResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.soTienDaThanhToan = BigDecimal.ZERO;
        this.tongTienSauCapNhat = BigDecimal.ZERO;
        this.ngayThanhToan = new Date();
        this.phuongThucThanhToan = null;
        this.maGiaoDich = null;
    }

    // Constructor đầy đủ
    public ThanhToanPhiPhuResponse(boolean success, String message,
                                   BigDecimal soTienDaThanhToan, BigDecimal tongTienSauCapNhat,
                                   Date ngayThanhToan, String phuongThucThanhToan, String maGiaoDich) {
        this.success = success;
        this.message = message;
        this.soTienDaThanhToan = soTienDaThanhToan != null ? soTienDaThanhToan : BigDecimal.ZERO;
        this.tongTienSauCapNhat = tongTienSauCapNhat != null ? tongTienSauCapNhat : BigDecimal.ZERO;
        this.ngayThanhToan = ngayThanhToan != null ? ngayThanhToan : new Date();
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.maGiaoDich = maGiaoDich;
    }

    // Builder pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private String message;
        private BigDecimal soTienDaThanhToan = BigDecimal.ZERO;
        private BigDecimal tongTienSauCapNhat = BigDecimal.ZERO;
        private Date ngayThanhToan = new Date();
        private String phuongThucThanhToan;
        private String maGiaoDich;

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder soTienDaThanhToan(BigDecimal soTienDaThanhToan) {
            this.soTienDaThanhToan = soTienDaThanhToan != null ? soTienDaThanhToan : BigDecimal.ZERO;
            return this;
        }

        public Builder tongTienSauCapNhat(BigDecimal tongTienSauCapNhat) {
            this.tongTienSauCapNhat = tongTienSauCapNhat != null ? tongTienSauCapNhat : BigDecimal.ZERO;
            return this;
        }

        public Builder ngayThanhToan(Date ngayThanhToan) {
            this.ngayThanhToan = ngayThanhToan != null ? ngayThanhToan : new Date();
            return this;
        }

        public Builder phuongThucThanhToan(String phuongThucThanhToan) {
            this.phuongThucThanhToan = phuongThucThanhToan;
            return this;
        }

        public Builder maGiaoDich(String maGiaoDich) {
            this.maGiaoDich = maGiaoDich;
            return this;
        }

        public ThanhToanPhiPhuResponse build() {
            return new ThanhToanPhiPhuResponse(
                    success, message, soTienDaThanhToan, tongTienSauCapNhat,
                    ngayThanhToan, phuongThucThanhToan, maGiaoDich
            );
        }
    }

    // Phương thức tiện ích
    public boolean isSuccessful() {
        return success;
    }

    public String getFormattedSoTien() {
        return String.format("%,d ₫", soTienDaThanhToan != null ? soTienDaThanhToan.longValue() : 0);
    }

    public String getFormattedTongTien() {
        return String.format("%,d ₫", tongTienSauCapNhat != null ? tongTienSauCapNhat.longValue() : 0);
    }
}