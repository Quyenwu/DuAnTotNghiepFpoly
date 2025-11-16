package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.NhaSanXuat;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NhaSanXuatResponse {

    private Integer id;

    private String maNhaSanXuat;

    private String tenNhaSanXuat;

    private Boolean trangThai;

    public NhaSanXuatResponse(NhaSanXuat nsx) {
        this.id = nsx.getId();
        this.maNhaSanXuat = nsx.getMaNhaSanXuat();
        this.tenNhaSanXuat = nsx.getTenNhaSanXuat();
        this.trangThai = nsx.getTrangThai();
    }
}
