package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class XuatXuRequest {
    private Integer id;

    private String maXuatXu;

    @NotBlank(message = "Tên xuất xứ không được để trống")
    private String tenXuatXu;

    private Boolean trangThai;
}
