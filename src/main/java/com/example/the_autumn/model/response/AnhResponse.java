package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.Anh;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnhResponse {

    private Integer id;

    private String maAnh;

    private String duongDanAnh;

    private Boolean trangThai;

    public AnhResponse(Anh a) {
        this.id = a.getId();
        this.maAnh = a.getMaAnh();
        this.duongDanAnh = a.getDuongDanAnh();
        this.trangThai = a.getTrangThai();
    }
}
