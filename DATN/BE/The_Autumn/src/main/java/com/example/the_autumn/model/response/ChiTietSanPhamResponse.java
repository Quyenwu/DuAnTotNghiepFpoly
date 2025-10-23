package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.ChiTietSanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ChiTietSanPhamResponse {

    private Integer id;

    private String trongLuong;

    private String maVach;

    private Integer soLuongTon;

    private String moTa;

    private BigDecimal giaBan;

    private LocalDate ngayTao;

    private LocalDate ngaySua;

    private Boolean trangThai = true;

    private String maMauSac;

    private String tenMauSac;

    private String maKichThuoc;

    private String tenKichThuoc;

    private List<AnhResponse> anhs;

    public ChiTietSanPhamResponse(ChiTietSanPham ctsp) {
        this.id = ctsp.getId();
        this.trongLuong = ctsp.getTrongLuong();
        this.maVach = ctsp.getMaVach();
        this.soLuongTon = ctsp.getSoLuongTon();
        this.moTa = ctsp.getMoTa();
        this.giaBan = ctsp.getGiaBan();
        this.ngayTao = ctsp.getNgayTao();
        this.ngaySua = ctsp.getNgaySua();
        this.trangThai = ctsp.getTrangThai();
        this.maMauSac = ctsp.getMauSac().getMaMauSac();
        this.tenMauSac = ctsp.getMauSac().getTenMauSac();
        this.maKichThuoc = ctsp.getKichThuoc().getMaKichThuoc();
        this.tenKichThuoc = ctsp.getKichThuoc().getTenKichThuoc();
        if (ctsp.getAnhs() != null) {
            this.anhs = ctsp.getAnhs().stream()
                    .map(AnhResponse::new)
                    .toList();
        }
    }
}
