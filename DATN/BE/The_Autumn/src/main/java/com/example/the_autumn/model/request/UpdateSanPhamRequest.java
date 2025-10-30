package com.example.the_autumn.model.request;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSanPhamRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 200, message = "Tên sản phẩm tối đa 200 ký tự")
    private String tenSanPham;

    @NotNull(message = "Nhà sản xuất không được để trống")
    private Integer idNhaSanXuat;

    @NotNull(message = "Xuất xứ không được để trống")
    private Integer idXuatXu;

    @NotNull(message = "Chất liệu không được để trống")
    private Integer idChatLieu;

    @NotNull(message = "Kiểu dáng không được để trống")
    private Integer idKieuDang;

    @NotNull(message = "Cổ áo không được để trống")
    private Integer idCoAo;

    @NotNull(message = "Tay áo không được để trống")
    private Integer idTayAo;

    @Size(max = 50, message = "Trọng lượng tối đa 50 ký tự")
    private String trongLuong;

    private Boolean trangThai;
}
