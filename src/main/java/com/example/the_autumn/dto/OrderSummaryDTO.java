package com.example.the_autumn.dto;

import java.math.BigDecimal;
import java.util.Date;

/**
 * DTO tóm tắt thông tin đơn hàng để hiển thị trong danh sách.
 * (Dùng cho đầu ra của hàm getOrdersByCustomerId và getOrdersByMaHoaDonList)
 */
public class OrderSummaryDTO {
    private Integer id;
    private String maHoaDon;
    private Date ngayTao;
    private Integer trangThai;
    private BigDecimal tongTienSauGiam;

}