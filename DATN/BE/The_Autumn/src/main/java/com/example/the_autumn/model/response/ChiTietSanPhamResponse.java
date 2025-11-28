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

    private String maHex;

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
        this.maHex = ctsp.getMauSac().getMaHex();
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

    public BigDecimal tinhGiaSauGiamSafe(ChiTietSanPham ctsp) {
        try {
            LocalDate now = LocalDate.now();

            List<DotGiamGiaChiTiet> dotGiamGiaChiTiets = ctsp.getDotGiamGiaChiTiets();
            if (dotGiamGiaChiTiets == null) {
                return ctsp.getGiaBan();
            }

            List<DotGiamGiaChiTiet> activeDiscounts = dotGiamGiaChiTiets.stream()
                    .filter(dggct -> {
                        if (dggct == null) return false;

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
                    // Giảm giá theo phần trăm
                    giaSauGiam = giaBanGoc.subtract(
                            giaBanGoc.multiply(dgg.getGiaTriGiam().divide(BigDecimal.valueOf(100)))
                    );
                } else {
                    // Giảm giá trực tiếp
                    giaSauGiam = giaBanGoc.subtract(dgg.getGiaTriGiam());
                }

                // Đảm bảo giá sau giảm không âm
                if (giaSauGiam.compareTo(BigDecimal.ZERO) < 0) {
                    giaSauGiam = BigDecimal.ZERO;
                }

                if (giaSauGiam.compareTo(giaThapNhat) < 0) {
                    giaThapNhat = giaSauGiam;
                }
            }

            return giaThapNhat;

        } catch (Exception e) {
            // Fallback an toàn nếu có lỗi
            System.err.println("Lỗi khi tính giá sau giảm cho CTSP ID " + ctsp.getId() + ": " + e.getMessage());
            return ctsp.getGiaBan();
        }
    }

    @Deprecated
    public BigDecimal tinhGiaSauGiam(ChiTietSanPham ctsp) {
        return tinhGiaSauGiamSafe(ctsp);
    }

}
