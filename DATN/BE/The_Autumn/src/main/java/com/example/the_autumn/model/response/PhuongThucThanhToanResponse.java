package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.HinhThucThanhToan;
import com.example.the_autumn.entity.LichSuThanhToan;
import com.example.the_autumn.entity.PhuongThucThanhToan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class PhuongThucThanhToanResponse {

    private Integer id;
    private String maPhuongThucThanhToan;
    private String tenPhuongThucThanhToan;
    private Boolean trangThai;

    public PhuongThucThanhToanResponse(PhuongThucThanhToan pttt) {
        this.id = pttt.getId();
        this.maPhuongThucThanhToan = pttt.getMaPhuongThucThanhToan();
        this.tenPhuongThucThanhToan = pttt.getTenPhuongThucThanhToan();
        this.trangThai = pttt.getTrangThai();
    }
}
