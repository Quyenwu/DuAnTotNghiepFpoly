package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.KichThuoc;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KichThuocResponse {
    private Integer id;
    private String maKichThuoc;
    private String tenKichThuoc;
    private Boolean trangThai;

    public KichThuocResponse(KichThuoc kt) {
        this.id = kt.getId();
        this.maKichThuoc = kt.getMaKichThuoc();
        this.tenKichThuoc = kt.getTenKichThuoc();
        this.trangThai = kt.getTrangThai();
    }
}

