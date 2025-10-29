package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.DotGiamGia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DotGiamGiaResponse {
    private Integer id;
    private String maGiamGia;
    private String tenDot;
    private Boolean loaiGiamGia;
    private BigDecimal giaTriGiam;
    private BigDecimal giaTriToiThieu;
    private Date ngayTao;
    private LocalDate ngayBatDau;
    private LocalDate ngayKetThuc;
    private Boolean trangThai;

    public DotGiamGiaResponse(DotGiamGia dotGiamGia) {
        this.id = dotGiamGia.getId();
        this.maGiamGia = dotGiamGia.getMaGiamGia();
        this.tenDot = dotGiamGia.getTenDot();
        this.loaiGiamGia = dotGiamGia.getLoaiGiamGia();
        this.giaTriGiam =  dotGiamGia.getGiaTriGiam();
        this.giaTriToiThieu = dotGiamGia.getGiaTriToiThieu();
        this.ngayTao = dotGiamGia.getNgayTao();
        this.ngayBatDau = dotGiamGia.getNgayBatDau();
        this.ngayKetThuc = dotGiamGia.getNgayKetThuc();
        this.trangThai = dotGiamGia.getTrangThai();
    }
}
