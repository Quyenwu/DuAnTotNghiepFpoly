package com.example.the_autumn.model.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnhRequest {
    private Integer idChiTietSanPham;
    private String maAnh;
    private String duongDanAnh;
    private Boolean trangThai;
}

