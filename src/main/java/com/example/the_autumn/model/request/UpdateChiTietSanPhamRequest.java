package com.example.the_autumn.model.request;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateChiTietSanPhamRequest {

    @NotNull(message = "Kích thước không được để trống")
    private Integer idKichThuoc;

    @NotNull(message = "Màu sắc không được để trống")
    private Integer idMauSac;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.01", message = "Giá bán phải lớn hơn 0")
    private BigDecimal giaBan;

    @NotNull(message = "Số lượng tồn không được để trống")
    @Min(value = 0, message = "Số lượng tồn không được âm")
    private Integer soLuongTon;

    @Size(max = 50, message = "Mã vạch tối đa 50 ký tự")
    private String maVach;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String moTa;

    private Boolean trangThai;
}