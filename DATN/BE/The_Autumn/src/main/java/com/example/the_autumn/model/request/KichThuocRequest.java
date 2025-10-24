package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KichThuocRequest {
    private Integer id;
    @NotBlank(message = "Mã kích thước không được để trống")
    private String maKichThuoc;
    @NotBlank(message = "Tên kích thước không được để trống")
    private String tenKichThuoc;
    private Boolean trangThai;
}
