package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.KieuDang;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KieuDangResponse {
    private Integer id;
    private String maKieuDang;
    private String tenKieuDang;
    private Boolean trangThai;

    public KieuDangResponse(KieuDang kd) {
        this.id = kd.getId();
        this.maKieuDang = kd.getMaKieuDang();
        this.tenKieuDang = kd.getTenKieuDang();
        this.trangThai = kd.getTrangThai();
    }
}
