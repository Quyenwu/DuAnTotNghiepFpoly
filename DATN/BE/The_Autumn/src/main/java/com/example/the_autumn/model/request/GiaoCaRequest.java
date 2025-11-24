package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GiaoCaRequest {

    private Integer nhanVienId;
    private BigDecimal soTienBatDau;
    private BigDecimal soTienKetThuc;
    private String ghiChu;
}
