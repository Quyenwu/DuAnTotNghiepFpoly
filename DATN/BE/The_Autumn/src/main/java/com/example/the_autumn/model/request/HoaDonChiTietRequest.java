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
public class HoaDonChiTietRequest {

    private Integer idChiTietSanPham;
    private Integer soLuong;
    private BigDecimal giaBan;
    private String ghiChu;
}
