package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.TayAo;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TayAoResponse {
    private Integer id;
    private String maTayAo;
    private String tenTayAo;
    private Boolean trangThai;

    public TayAoResponse(TayAo ta) {
        this.id = ta.getId();
        this.maTayAo = ta.getMaTayAo();
        this.tenTayAo = ta.getTenTayAo();
        this.trangThai = ta.getTrangThai();
    }
}

