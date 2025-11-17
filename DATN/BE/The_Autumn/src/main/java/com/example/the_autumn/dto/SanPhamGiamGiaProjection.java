package com.example.the_autumn.dto;

import java.math.BigDecimal;

public interface SanPhamGiamGiaProjection {
    Integer getIdSanPham();
    String getTenSanPham();
    String getAnhDaiDien();
    BigDecimal getGiaGoc();
    BigDecimal getGiaSauGiam();
    Integer getIdDotGiamGia();
    String getTenDotGiamGia();
}