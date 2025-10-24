package com.example.the_autumn.model.response;


import com.example.the_autumn.entity.DotGiamGia;

import java.util.List;

public class DotGiamGiaRequest {
    private DotGiamGia dotGiamGia;
    private List<Integer> ctspIds;

    public DotGiamGia getDotGiamGia() {
        return dotGiamGia;
    }

    public void setDotGiamGia(DotGiamGia dotGiamGia) {
        this.dotGiamGia = dotGiamGia;
    }

    public List<Integer> getCtspIds() {
        return ctspIds;
    }

    public void setCtspIds(List<Integer> ctspIds) {
        this.ctspIds = ctspIds;
    }
}
