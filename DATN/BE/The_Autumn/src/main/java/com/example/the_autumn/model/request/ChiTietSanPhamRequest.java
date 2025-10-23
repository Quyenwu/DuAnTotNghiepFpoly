package com.example.the_autumn.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ChiTietSanPhamRequest {
    private Integer id;

    @NotNull(message = "Sản phẩm không được để trống")
    private Integer idSanPham;

    @NotNull(message = "Màu sắc không được để trống")
    private Integer idMauSac;

    @NotNull(message = "Kích thước không được để trống")
    private Integer idKichThuoc;

    @NotNull(message = "Trọng lượng không được để trống")
    private Integer idTrongLuong;

    @NotNull(message = "Giá bán không được để trống")
    private BigDecimal giaBan;

    @NotNull(message = "Số lượng tồn không được để trống")
    private Integer soLuongTon;

    private String moTa;
}
