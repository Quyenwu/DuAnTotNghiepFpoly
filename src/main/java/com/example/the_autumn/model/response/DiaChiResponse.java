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
    private String tenDiaChi;
    private String diaChiCuThe;
    private Boolean trangThai;
    private Integer tinhThanhId;
    private Integer quanHuyenId;
    private String tenTinh;
    private String tenQuan;

    public DiaChiResponse(DiaChi diaChi) {
        this.id = diaChi.getId();
        this.tenDiaChi=diaChi.getTenDiaChi();
        this.diaChiCuThe = diaChi.getDiaChiCuThe();
        this.trangThai = diaChi.getTrangThai();

        if (diaChi.getTinhThanh() != null) {
            this.tinhThanhId = diaChi.getTinhThanh().getId();
            this.tenTinh = diaChi.getTinhThanh().getTenTinh();
        }

        if (diaChi.getQuanHuyen() != null) {
            this.quanHuyenId = diaChi.getQuanHuyen().getId();
            this.tenQuan = diaChi.getQuanHuyen().getTenQuan();
        }
    }
}
