// DotGiamGiaDTO.java
package com.example.the_autumn.model.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
public class DotGiamGiaDTO {
    private Integer id;
    private String maGiamGia;
    private String tenDot;
    private Boolean loaiGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giaTriToiThieu;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Boolean trangThai;

    // Constructor
    public DotGiamGiaDTO(Integer id, String maGiamGia, String tenDot, Boolean loaiGiamGia,
                         BigDecimal giaTriGiam, BigDecimal giaTriToiThieu,
                         Date ngayBatDau, Date ngayKetThuc, Boolean trangThai) {
        this.id = id;
        this.maGiamGia = maGiamGia;
        this.tenDot = tenDot;
        this.loaiGiamGia = loaiGiamGia;
        this.giaTriGiam = giaTriGiam;
        this.giaTriToiThieu = giaTriToiThieu;
        this.ngayBatDau = ngayBatDau;
        this.ngayKetThuc = ngayKetThuc;
        this.trangThai = trangThai;
    }

    // getters + setters
}
