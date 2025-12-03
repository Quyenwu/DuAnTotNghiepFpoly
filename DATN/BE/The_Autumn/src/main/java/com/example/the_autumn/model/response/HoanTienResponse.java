package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class HoanTienResponse {

    private boolean success;
    private String message;
    private Integer idLichSuHoanTien;
    private BigDecimal soTienHoan;
    private Date ngayHoanTien;
    private String ghiChu;

    public HoanTienResponse(boolean success, String message, Integer idLichSuHoanTien,
                            BigDecimal soTienHoan, Date ngayHoanTien, String ghiChu) {
        this.success = success;
        this.message = message;
        this.idLichSuHoanTien = idLichSuHoanTien;
        this.soTienHoan = soTienHoan;
        this.ngayHoanTien = ngayHoanTien;
        this.ghiChu = ghiChu;
    }
}
