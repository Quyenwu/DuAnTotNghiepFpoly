package com.example.the_autumn.dto;

import lombok.Data;

import java.util.List;

@Data
public class MauSacVariantDTO {
    private Integer idMauSac;
    private String tenMauSac;
    private String duongDanAnh; // Ảnh đại diện cho màu này
    private List<KichThuocVariantDTO> kichThuocList;
}
