package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.MauSac;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MauSacResponse {
    private Integer id;
    private String maMauSac;
    private String tenMauSac;
    private Boolean trangThai;
    private String maHex;

    public MauSacResponse(MauSac ms) {
        this.id = ms.getId();
        this.maMauSac = ms.getMaMauSac();
        this.tenMauSac = ms.getTenMauSac();
        this.trangThai = ms.getTrangThai();
        this.maHex = ms.getMaHex();
    }
}
