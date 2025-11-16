package com.example.the_autumn.model.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CapNhatBienTheRequest {

    @NotBlank(message = "Mã sản phẩm không được để trống")
    private String maSanPham;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String tenSanPham;

    @NotNull(message = "Nhà sản xuất không được để trống")
    private Integer idNhaSanXuat;

    @NotNull(message = "Xuất xứ không được để trống")
    private Integer idXuatXu;

    @NotNull(message = "Chất liệu không được để trống")
    private Integer idChatLieu;

    @NotNull(message = "Kiểu dáng không được để trống")
    private Integer idKieuDang;

    private Integer nguoiTao;
    private Integer nguoiSua;

    @NotEmpty(message = "Danh sách biến thể không được để trống")
    @Valid
    private List<BienTheRequest> danhSachBienThe;

    @Getter
    @Setter
    public static class BienTheRequest {

        @NotNull(message = "ID biến thể không được để trống")
        private Integer idChiTietSanPham;

        @NotNull(message = "Giá bán không được để trống")
        private BigDecimal donGia;

        @NotNull(message = "Số lượng không được để trống")
        private Integer soLuong;

        private String moTa;
    }
}