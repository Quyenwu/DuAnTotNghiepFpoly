package com.example.the_autumn.model.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ShippingFeeResponse {
    private BigDecimal phiVanChuyen;
    private BigDecimal phiVanChuyenGoc;
    private BigDecimal phiDuKien;
    private String thoiGianDuKien;
    private String donViVanChuyen;
    private String maDichVu;
    private String moTaDichVu;
    private Boolean success;
    private String message;
}
