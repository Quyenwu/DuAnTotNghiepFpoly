package com.example.the_autumn.model.request;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VariantRequest {

    @NotNull(message = "Màu sắc không được để trống")
    @Positive(message = "ID màu sắc không hợp lệ")
    private Integer idMauSac;

    @NotNull(message = "Kích thước không được để trống")
    @Positive(message = "ID kích thước không hợp lệ")
    private Integer idKichThuoc;

    @NotNull(message = "Cổ áo không được để trống")
    @Positive(message = "ID cổ áo không hợp lệ")
    private Integer idCoAo;

    @NotNull(message = "Tay áo không được để trống")
    @Positive(message = "ID tay áo không hợp lệ")
    private Integer idTayAo;

    @NotNull(message = "Trọng lượng không được để trống")
    @Positive(message = "ID trọng lượng không hợp lệ")
    private Integer idTrongLuong;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "1000", message = "Giá bán phải từ 1,000 VNĐ trở lên")
    @DecimalMax(value = "999999999", message = "Giá bán tối đa 999,999,999 VNĐ")
    private java.math.BigDecimal giaBan;

    @NotNull(message = "Số lượng không được để trống")
    @Min(value = 0, message = "Số lượng không được âm")
    @Max(value = 999999, message = "Số lượng tối đa 999,999")
    private Integer soLuongTon;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;
}
