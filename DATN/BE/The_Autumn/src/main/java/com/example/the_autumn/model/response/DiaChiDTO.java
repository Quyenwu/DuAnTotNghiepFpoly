package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaChiDTO {
    private Integer id;
    private String tenDiaChi;
    private String thanhPho;
    private String quan;
    private String diaChiCuThe;
}
