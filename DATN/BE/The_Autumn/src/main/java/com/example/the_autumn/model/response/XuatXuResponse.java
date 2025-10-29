package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.XuatXu;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class XuatXuResponse {

    private Integer id;

    private String maXuatXu;

    private String tenXuatXu;

    private Boolean trangThai;

    public XuatXuResponse(XuatXu xx) {
        this.id = xx.getId();
        this.maXuatXu = xx.getMaXuatXu();
        this.tenXuatXu = xx.getTenXuatXu();
        this.trangThai = xx.getTrangThai();
    }
}
