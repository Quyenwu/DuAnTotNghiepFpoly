package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NhaSanXuatRequest {

    private Integer id;

    @NotBlank(message = "ma khong duoc de trong")
    private String maNhaSanXuat;

    @NotBlank(message = "ten khong duoc de trong")
    private String tenNhaSanXuat;

    private Boolean trangThai;
}
