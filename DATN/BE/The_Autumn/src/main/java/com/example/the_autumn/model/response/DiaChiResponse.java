package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.DiaChi;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DiaChiResponse {
    private Integer id;
    private String diaChiCuThe;
    private Boolean trangThai;
    private String tenTinh;
    private String tenQuan;

    public DiaChiResponse(DiaChi diaChi) {
        this.id = diaChi.getId();
        this.diaChiCuThe = diaChi.getDiaChiCuThe();
        this.trangThai = diaChi.getTrangThai();
        this.tenTinh = diaChi.getTinhThanh().getTenTinh();
        this.tenQuan = diaChi.getQuanHuyen().getTenQuan();
    }
}
