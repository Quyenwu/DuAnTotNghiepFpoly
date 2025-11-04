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
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

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

    public BigDecimal tinhGiaSauGiam(ChiTietSanPham ctsp) {
        LocalDate now = LocalDate.now();

        List<DotGiamGiaChiTiet> activeDiscounts = ctsp.getDotGiamGiaChiTiets().stream()
                .filter(dggct -> {
                    DotGiamGia dgg = dggct.getDotGiamGia();
                    return dgg != null
                            && dgg.getTrangThai() == 1
                            && (now.isEqual(dgg.getNgayBatDau()) || now.isAfter(dgg.getNgayBatDau()))
                            && (now.isEqual(dgg.getNgayKetThuc()) || now.isBefore(dgg.getNgayKetThuc()));
                })
                .collect(Collectors.toList());

        if (activeDiscounts.isEmpty()) {
            return ctsp.getGiaBan();
        }

        BigDecimal giaBanGoc = ctsp.getGiaBan();
        BigDecimal giaThapNhat = giaBanGoc;

        for (DotGiamGiaChiTiet dggct : activeDiscounts) {
            DotGiamGia dgg = dggct.getDotGiamGia();
            BigDecimal giaSauGiam;

            if (!dgg.getLoaiGiamGia()) {
                giaSauGiam = giaBanGoc.subtract(
                        giaBanGoc.multiply(dgg.getGiaTriGiam().divide(BigDecimal.valueOf(100)))
                );
            } else {
                giaSauGiam = giaBanGoc.subtract(dgg.getGiaTriGiam());
            }

            if (giaSauGiam.compareTo(giaThapNhat) < 0) {
                giaThapNhat = giaSauGiam;
            }
        }

        return giaThapNhat;
    }
}
