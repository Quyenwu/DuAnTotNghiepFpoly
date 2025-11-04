package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class ChiTietSanPhamResponse {

    private Integer id;

    private String maVach;

    private Integer soLuongTon;

    private String moTa;

    private BigDecimal giaBan;

    private LocalDate ngayTao;

    private LocalDate ngaySua;

    private Boolean trangThai = true;

    private String tenSanPham;

    private String maMauSac;

    private String tenMauSac;

    private String maKichThuoc;

    private String tenKichThuoc;

    private String tenTrongLuong;

    private BigDecimal giaSauGiam;

    private List<AnhResponse> anhs;

    public ChiTietSanPhamResponse(ChiTietSanPham ctsp) {
        this.id = ctsp.getId();
        this.maVach = ctsp.getMaVach();
        this.soLuongTon = ctsp.getSoLuongTon();
        this.moTa = ctsp.getMoTa();
        this.giaBan = ctsp.getGiaBan();
        this.ngayTao = ctsp.getNgayTao();
        this.ngaySua = ctsp.getNgaySua();
        this.trangThai = ctsp.getTrangThai();
        this.tenTrongLuong = ctsp.getSanPham().getTrongLuong();
        this.tenSanPham = ctsp.getSanPham().getTenSanPham();
        this.maMauSac = ctsp.getMauSac().getMaMauSac();
        this.tenMauSac = ctsp.getMauSac().getTenMauSac();
        this.maKichThuoc = ctsp.getKichThuoc().getMaKichThuoc();
        this.tenKichThuoc = ctsp.getKichThuoc().getTenKichThuoc();
        if (ctsp.getAnhs() != null) {
            this.anhs = ctsp.getAnhs().stream()
                    .map(AnhResponse::new)
                    .toList();
        }
        this.giaSauGiam = tinhGiaSauGiam(ctsp);
    }

    private BigDecimal tinhGiaSauGiam(ChiTietSanPham ctsp) {
        BigDecimal giaGoc = ctsp.getGiaBan();
        DotGiamGiaChiTiet dot = layDotGiamGiaHienTai(ctsp);

        if (dot == null) {
            return giaGoc;
        }

        DotGiamGia dgg = dot.getDotGiamGia();
        if (dgg == null || dgg.getGiaTriGiam() == null) {
            return giaGoc;
        }

        if (!dgg.getLoaiGiamGia()) {
            return giaGoc.subtract(
                    giaGoc.multiply(dgg.getGiaTriGiam().divide(BigDecimal.valueOf(100)))
            );
        } else {
            return giaGoc.subtract(dgg.getGiaTriGiam());
        }
    }

    private DotGiamGiaChiTiet layDotGiamGiaHienTai(ChiTietSanPham ctsp) {
        if (ctsp.getDotGiamGiaChiTiets() == null || ctsp.getDotGiamGiaChiTiets().isEmpty()) {
            return null;
        }

        LocalDate now = LocalDate.now();
        return ctsp.getDotGiamGiaChiTiets().stream()
                .filter(ct -> {
                    DotGiamGia dgg = ct.getDotGiamGia();
                    return dgg != null
                            && dgg.getNgayBatDau() != null
                            && dgg.getNgayKetThuc() != null
                            && (now.isEqual(dgg.getNgayBatDau()) || now.isAfter(dgg.getNgayBatDau()))
                            && (now.isEqual(dgg.getNgayKetThuc()) || now.isBefore(dgg.getNgayKetThuc()))
                            && dgg.getTrangThai() == 1;
                })
                .findFirst()
                .orElse(null);
    }
}
