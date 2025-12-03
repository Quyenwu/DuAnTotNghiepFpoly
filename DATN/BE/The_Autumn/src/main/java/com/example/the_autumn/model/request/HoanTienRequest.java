package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class HoanTienRequest {
    private BigDecimal soTienHoan; // Có thể null để hoàn toàn bộ
    private String lyDoHoanTien;
    private String ghiChuBoSung;
    private Integer idNhanVienThucHien;
}
