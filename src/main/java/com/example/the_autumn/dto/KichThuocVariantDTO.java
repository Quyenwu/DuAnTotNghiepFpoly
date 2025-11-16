package com.example.the_autumn.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class KichThuocVariantDTO {
    private Integer idCtsp; // ID của ChiTietSanPham (rất quan trọng)
    private String tenKichThuoc;
    private BigDecimal giaBan;
    private Integer soLuongTon;
}
