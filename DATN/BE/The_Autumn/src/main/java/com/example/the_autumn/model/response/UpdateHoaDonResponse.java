package com.example.the_autumn.model.response;

import com.example.the_autumn.dto.HoaDonDetailDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateHoaDonResponse {
    private boolean success;
    private String message;
    private HoaDonDetailDTO data;

    // Thông tin tính toán
    private BigDecimal tongTienSanPham;
    private BigDecimal phiVanChuyen;
    private BigDecimal tienGiamGia;
    private BigDecimal tongTienSauGiam;

    public UpdateHoaDonResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.data = null;
        this.tongTienSanPham = null;
        this.phiVanChuyen = null;
        this.tienGiamGia = null;
        this.tongTienSauGiam = null;
    }
}