package com.example.the_autumn.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaLamViecDTO {
    private Integer id;
    private String maCa;
    private String tenCa;
    private String gioBatDau;
    private String gioKetThuc;
    private String moTa;
    private Boolean trangThai;
}
