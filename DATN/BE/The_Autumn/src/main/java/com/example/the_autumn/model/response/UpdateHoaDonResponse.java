package com.example.the_autumn.model.response;


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
    private HoaDonDetailResponse data;
    private String diaChiCuThe;
    private Integer thanhPho;    // id tỉnh
    private Integer quan;        // id quận

    // Getters & Setters

    private BigDecimal tongTienSanPham;
    private BigDecimal phiVanChuyen;
    private BigDecimal tienGiamGia;
    private BigDecimal tongTienSauGiam;

    public String getDiaChiCuThe() {
        return diaChiCuThe;
    }

    public void setDiaChiCuThe(String diaChiCuThe) {
        this.diaChiCuThe = diaChiCuThe;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public HoaDonDetailResponse getData() {
        return data;
    }

    public void setData(HoaDonDetailResponse data) {
        this.data = data;
    }

    public Integer getThanhPho() {
        return thanhPho;
    }

    public void setThanhPho(Integer thanhPho) {
        this.thanhPho = thanhPho;
    }

    public Integer getQuan() {
        return quan;
    }

    public void setQuan(Integer quan) {
        this.quan = quan;
    }

    public BigDecimal getTongTienSanPham() {
        return tongTienSanPham;
    }

    public void setTongTienSanPham(BigDecimal tongTienSanPham) {
        this.tongTienSanPham = tongTienSanPham;
    }

    public BigDecimal getPhiVanChuyen() {
        return phiVanChuyen;
    }

    public void setPhiVanChuyen(BigDecimal phiVanChuyen) {
        this.phiVanChuyen = phiVanChuyen;
    }

    public BigDecimal getTienGiamGia() {
        return tienGiamGia;
    }

    public void setTienGiamGia(BigDecimal tienGiamGia) {
        this.tienGiamGia = tienGiamGia;
    }

    public BigDecimal getTongTienSauGiam() {
        return tongTienSauGiam;
    }

    public void setTongTienSauGiam(BigDecimal tongTienSauGiam) {
        this.tongTienSauGiam = tongTienSauGiam;
    }

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