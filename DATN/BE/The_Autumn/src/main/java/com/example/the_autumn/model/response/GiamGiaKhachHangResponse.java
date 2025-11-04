package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class GiamGiaKhachHangResponse {

    private Integer id;
    private Boolean trangThai;
    private Integer kieuPhieuGiamGia;
    private Integer phieuGiamGiaId;
    private String maGiamGia;
    private String tenChuongTrinh;
    private Integer khachHangId;
    private String hoTenKhachHang;
    private String emailKhachHang;
    private String soDienThoai;

    public GiamGiaKhachHangResponse(GiamGiaKhachHang giamGiaKhachHang) {
        this.id = giamGiaKhachHang.getId();
        this.trangThai = giamGiaKhachHang.getTrangThai();
        this.kieuPhieuGiamGia = giamGiaKhachHang.getPhieuGiamGia().getKieu();
        this.phieuGiamGiaId = giamGiaKhachHang.getPhieuGiamGia().getId();
        this.maGiamGia = giamGiaKhachHang.getPhieuGiamGia().getMaGiamGia();
        this.tenChuongTrinh = giamGiaKhachHang.getPhieuGiamGia().getTenChuongTrinh();
        this.khachHangId = giamGiaKhachHang.getKhachHang().getId();
        this.hoTenKhachHang = giamGiaKhachHang.getKhachHang().getHoTen();
        this.emailKhachHang = giamGiaKhachHang.getKhachHang().getEmail();
        this.soDienThoai = giamGiaKhachHang.getKhachHang().getSdt();

    }
}
