package com.example.the_autumn.model.request;

import lombok.Data;

@Data
public class VietQRRequest {
    private Long amount;
    private String noiDung;
    private String orderId;
    private String tenKhachHang;
    private String maHoaDon;
}