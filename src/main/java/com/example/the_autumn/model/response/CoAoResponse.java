package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.CoAo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoAoResponse {
    private Integer id;
    private String maCoAo;
    private String tenCoAo;
    private Boolean trangThai;

    public CoAoResponse(CoAo ca) {
        this.id = ca.getId();
        this.maCoAo = ca.getMaCoAo();
        this.tenCoAo = ca.getTenCoAo();
        this.trangThai = ca.getTrangThai();
    }
}
