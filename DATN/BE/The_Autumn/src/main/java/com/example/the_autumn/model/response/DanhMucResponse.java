package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.Anh;
import com.example.the_autumn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DanhMucResponse {

    private Integer id;
    private String tenSanPham;
    private Boolean trangThai;
    private List<ChiTietSanPhamResponse> chiTietSanPhams;

    private Integer tongSoLuong;

    private List<String> hinhAnhSanPham;

    public DanhMucResponse(SanPham sp) {
        this.id = sp.getId();
        this.tenSanPham = sp.getTenSanPham();
        this.trangThai = sp.getTrangThai();

        if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {

            this.hinhAnhSanPham = sp.getChiTietSanPham().stream()
                    .flatMap(ctsp -> ctsp.getAnhs().stream())
                    .map(Anh::getDuongDanAnh)
                    .filter(url -> url != null && !url.isEmpty())
                    .distinct()
                    .toList();

            this.chiTietSanPhams = sp.getChiTietSanPham().stream()
                    .map(ChiTietSanPhamResponse::new)
                    .toList();

            this.tongSoLuong = sp.getChiTietSanPham().stream()
                    .mapToInt(chiTiet -> chiTiet.getSoLuongTon() != null ? chiTiet.getSoLuongTon() : 0)
                    .sum();

        } else {
            this.chiTietSanPhams = List.of();
            this.tongSoLuong = 0;
            this.hinhAnhSanPham = List.of();
        }
    }
}