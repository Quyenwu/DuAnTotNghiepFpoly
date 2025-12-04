package com.example.the_autumn.dto;

import com.example.the_autumn.entity.Anh;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.SanPham;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListSanPhamResponse {
    private Integer id;
    private String tenSanPham;
    private Boolean trangThai;
    private List<ChiTietSanPhamDTO> chiTietSanPhams;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;
    private Integer tongSoLuongDaMua;
    private List<String> hinhAnhSanPham;

    public  ListSanPhamResponse(SanPham sp) {
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
                    .map(ChiTietSanPhamDTO::new)
                    .toList();


            List<BigDecimal> giaList = sp.getChiTietSanPham().stream()
                    .filter(ct -> ct.getGiaBan() != null)
                    .map(ChiTietSanPham::getGiaBan)
                    .toList();

            if (!giaList.isEmpty()) {
                this.giaThapNhat = giaList.stream().min(BigDecimal::compareTo).get();
                this.giaCaoNhat = giaList.stream().max(BigDecimal::compareTo).get();
            } else {
                this.giaThapNhat = BigDecimal.ZERO;
                this.giaCaoNhat = BigDecimal.ZERO;
            }
            if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {
                this.tongSoLuongDaMua = sp.getChiTietSanPham().stream()
                        .flatMap(ct -> ct.getHoaDonChiTiets().stream())
                        .mapToInt(hdct -> hdct.getSoLuong() != null ? hdct.getSoLuong() : 0)
                        .sum();
            } else {
                this.tongSoLuongDaMua = 0;
            }

        } else {
            this.chiTietSanPhams = List.of();
            this.giaThapNhat = BigDecimal.ZERO;
            this.giaCaoNhat = BigDecimal.ZERO;
            this.hinhAnhSanPham = List.of();
        }
    }

}
