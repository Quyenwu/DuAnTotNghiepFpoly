package com.example.the_autumn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SanPhamGiamGiaDTO {
    private Integer idSanPham;
    private String tenSanPham;
    private String anhDaiDien;
    private BigDecimal giaGoc;
    private BigDecimal giaSauGiam;
    private Integer idDotGiamGia;
    private String tenDotGiamGia;
}