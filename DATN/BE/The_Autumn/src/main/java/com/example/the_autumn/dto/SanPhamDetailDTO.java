package com.example.the_autumn.dto;

import lombok.Data;

import java.util.List;

@Data
public class SanPhamDetailDTO {
    private Integer idSanPham;
    private String tenSanPham;
    // (Bạn có thể thêm giá gốc hoặc mô tả ở đây nếu cần)
    private List<MauSacVariantDTO> mauSacList;

}
