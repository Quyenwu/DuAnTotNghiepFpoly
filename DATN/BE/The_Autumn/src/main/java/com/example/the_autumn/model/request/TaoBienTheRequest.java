package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class TaoBienTheRequest {

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @NotBlank(message = "Trọng lượng không được để trống")
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

    @NotEmpty(message = "Phải chọn ít nhất 1 màu sắc")
    private List<Integer> idMauSacs;

    @NotNull(message = "Kích thước không được để trống")
    private Integer idKichThuoc;
}