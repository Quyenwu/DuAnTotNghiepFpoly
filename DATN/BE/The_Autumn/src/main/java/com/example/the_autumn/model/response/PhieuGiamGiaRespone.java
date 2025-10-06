package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.PhieuGiamGia;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PhieuGiamGiaRespone {

    private Integer id;
    private String maGiamGia;
    private String tenChuongTrinh;
    private Boolean loaiGiamGia;
    private BigDecimal giaTriGiamGia;
    private BigDecimal mucGiaGiamToiDa;
    private BigDecimal giaTriDonHangToiThieu;
    private String moTa;
    private Integer soLuong;
    private Integer kieu;
    private Date ngayTao;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Boolean trangThai;

    public PhieuGiamGiaRespone(PhieuGiamGia p) {
        this.id = p.getId();
        this.maGiamGia = p.getMaGiamGia();
        this.tenChuongTrinh = p.getTenChuongTrinh();
        this.loaiGiamGia = p.getLoaiGiamGia();
        this.giaTriGiamGia = p.getGiaTriGiamGia();
        this.mucGiaGiamToiDa = p.getMucGiaGiamToiDa();
        this.giaTriDonHangToiThieu = p.getGiaTriDonHangToiThieu();
        this.moTa = p.getMoTa();
        this.soLuong = p.getSoLuong();
        this.kieu = p.getKieu();
        this.ngayTao = p.getNgayTao();
        this.ngayBatDau = p.getNgayBatDau();
        this.ngayKetThuc = p.getNgayKetThuc();
        this.trangThai = p.getTrangThai();
    }
}
