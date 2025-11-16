package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TayAoRequest {
    private Integer id;
    @NotBlank(message = "Mã tay áo không được để trống")
    private String maTayAo;
    @NotBlank(message = "Tên tay áo không được để trống")
    private String tenTayAo;
    private Boolean trangThai;
}

