package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SanPhamRequest {

    private Integer id;

    @NotBlank(message = "Mã sản phẩm không được để trống")
    private String maSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @NotBlank(message = "Trọng lượng sản phẩm không được để trống")
    private String trongLuong;

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

    private Integer nguoiTao;
    private Integer nguoiSua;
    private Boolean trangThai;
}
