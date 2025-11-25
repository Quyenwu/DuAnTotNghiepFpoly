package com.example.the_autumn.model.response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ShippingFeeResponse {
    private BigDecimal phiVanChuyen;
    private BigDecimal phiVanChuyenGoc;
    private BigDecimal phiDuKien;
    private String thoiGianDuKien;
    private String donViVanChuyen;
    private String maDichVu;
    private String moTaDichVu;
    private boolean success;
    private String message;
}
