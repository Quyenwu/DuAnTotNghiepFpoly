package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietSanPhamDTO {
    private Integer id;
    private String maSanPham;// id chi tiết sản phẩm
    private String tenSanPham;    // tên sản phẩm
    private BigDecimal giaBan;
    private String hinhAnh;
    private String nhaXanXuat;// giá bán
    private Integer soLuongTon;   // số lượng tồn
}
