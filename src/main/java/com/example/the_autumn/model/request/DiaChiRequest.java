package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiRequest {
    private Integer id;
    private String tenDiaChi;
    private Integer tinhThanhId;
    private Integer quanHuyenId;
    private String diaChiCuThe;
    private Boolean trangThai;
}
