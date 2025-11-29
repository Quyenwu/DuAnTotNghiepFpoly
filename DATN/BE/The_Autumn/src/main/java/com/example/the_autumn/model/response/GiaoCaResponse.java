package com.example.the_autumn.model.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoCaResponse {

    private Integer id;
    private Integer idNhanVien;
    private String hoTenNhanVien;

    private LocalDateTime thoiGianBatDau;
    private LocalDateTime thoiGianKetThuc;

    private BigDecimal soTienBatDau;
    private BigDecimal soTienKetThuc;
    private BigDecimal tongDoanhThu;
    private BigDecimal soTienChenhLech;

    private String ghiChu;
    private Boolean trangThai;
}