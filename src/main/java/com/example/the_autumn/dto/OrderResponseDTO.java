package com.example.the_autumn.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO trả về thông tin cơ bản của đơn hàng vừa tạo.
 * (Dùng cho đầu ra của hàm placeOrder)
 */
public class OrderResponseDTO {
    private Integer id;
    private String maHoaDon;
    private Date ngayTao;
    private Integer trangThai;
    private BigDecimal tongTienSauGiam;

}