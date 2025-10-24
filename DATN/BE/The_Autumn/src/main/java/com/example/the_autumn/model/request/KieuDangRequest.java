package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KieuDangRequest {
    private Integer id;
    @NotBlank(message = "Mã kiểu dáng không được để trống")
    private String maKieuDang;
    @NotBlank(message = "Tên kiểu dáng không được để trống")
    private String tenKieuDang;
    private Boolean trangThai;
}
