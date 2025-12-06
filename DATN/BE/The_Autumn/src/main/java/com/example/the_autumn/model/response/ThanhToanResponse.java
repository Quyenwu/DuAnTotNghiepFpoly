// ThanhToanResponse.java
package com.example.the_autumn.model.response;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ThanhToanResponse {
    private boolean success;
    private String message;
    private Integer idHoaDon;
    private String maHoaDon;
    private BigDecimal soTienCanThanhToanTruoc;
    private BigDecimal soTienDaThanhToan;
    private BigDecimal soTienConLai;
    private Date ngayThanhToan;
    private Integer idPhuongThucThanhToan;
    private String tenPhuongThucThanhToan;
    private String maGiaoDich;
    private String ghiChu;
}