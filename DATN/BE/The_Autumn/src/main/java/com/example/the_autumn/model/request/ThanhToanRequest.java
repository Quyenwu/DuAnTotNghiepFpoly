// ThanhToanRequest.java
package com.example.the_autumn.model.request;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ThanhToanRequest {
    private Integer idHoaDon;
    private BigDecimal soTienThanhToan; // Số tiền thanh toán thực tế
    private Integer idPhuongThucThanhToan; // ID phương thức thanh toán
    private String maGiaoDich; // Mã giao dịch (nếu có)
    private String ghiChu; // Ghi chú thanh toán
    private Integer idNhanVienThucHien; // ID nhân viên thực hiện
}