package com.example.the_autumn.dto;

import java.math.BigDecimal;
public interface SanPhamTrangChuProjection {
        Long getIdSanPham();
        String getTenSanPham();
        BigDecimal getGiaMin();
        BigDecimal getGiaMax();
        String getAnhDaiDien();

}