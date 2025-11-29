package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class GiaoCaStartRequest {

    @NotNull(message = "Nhân viên không được để trống")
    @Positive(message = "Mã nhân viên phải là số dương")
    private Integer idNhanVien;

    @NotNull(message = "Số tiền bắt đầu không được để trống")
    private BigDecimal soTienBatDau;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;
}
