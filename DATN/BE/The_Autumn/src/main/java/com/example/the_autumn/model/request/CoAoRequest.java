package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CoAoRequest {
    private Integer id;
    @NotBlank(message = "Mã cổ áo không được để trống")
    private String maCoAo;
    @NotBlank(message = "Tên cổ áo không được để trống")
    private String tenCoAo;
    private Boolean trangThai;
}

