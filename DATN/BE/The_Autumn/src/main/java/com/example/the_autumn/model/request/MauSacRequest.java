package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MauSacRequest {
    private Integer id;
    @NotBlank(message = "Mã màu sắc không được để trống")
    private String maMauSac;
    @NotBlank(message = "Tên màu sắc không được để trống")
    private String tenMauSac;
    private Boolean trangThai;
}
