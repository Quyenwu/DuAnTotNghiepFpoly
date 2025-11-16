package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class SanPhamResponse {

    private Integer id;
    private String maSanPham;
    private String tenSanPham;
    private Date ngayTao;
    private Date ngaySua;
    private Integer nguoiTao;
    private Integer nguoiSua;
    private Boolean trangThai;
    private String maNhaSanXuat;
    private String tenNhaSanXuat;
    private String maXuatXu;
    private String tenXuatXu;
    private String maChatLieu;
    private String tenChatLieu;
    private String maKieuDang;
    private String tenKieuDang;
    private String trongLuong;
    private String maCoAo;
    private String tenCoAo;
    private String maTayAo;
    private String tenTayAo;
    private List<ChiTietSanPhamResponse> chiTietSanPhams;

    private Integer tongSoLuong;

    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;

    public SanPhamResponse(SanPham sp) {
        this.id = sp.getId();
        this.maSanPham = sp.getMaSanPham();
        this.tenSanPham = sp.getTenSanPham();
        this.trongLuong = sp.getTrongLuong();
        this.ngayTao = sp.getNgayTao();
        this.ngaySua = sp.getNgaySua();
        this.nguoiTao = sp.getNguoiTao();
        this.nguoiSua = sp.getNguoiSua();
        this.trangThai = sp.getTrangThai();
        this.maNhaSanXuat = sp.getNhaSanXuat().getMaNhaSanXuat();
        this.tenNhaSanXuat = sp.getNhaSanXuat().getTenNhaSanXuat();
        this.maXuatXu = sp.getXuatXu().getMaXuatXu();
        this.tenXuatXu = sp.getXuatXu().getTenXuatXu();
        this.maChatLieu = sp.getChatLieu().getMaChatLieu();
        this.tenChatLieu = sp.getChatLieu().getTenChatLieu();
        this.maKieuDang = sp.getKieuDang().getMaKieuDang();
        this.tenKieuDang = sp.getKieuDang().getTenKieuDang();
        this.maCoAo = sp.getCoAo().getMaCoAo();
        this.tenCoAo = sp.getCoAo().getTenCoAo();
        this.maTayAo = sp.getTayAo().getMaTayAo();
        this.tenTayAo = sp.getTayAo().getTenTayAo();

        if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {
            this.chiTietSanPhams = sp.getChiTietSanPham().stream()
                    .map(ChiTietSanPhamResponse::new)
                    .toList();

            this.tongSoLuong = sp.getChiTietSanPham().stream()
                    .mapToInt(chiTiet -> chiTiet.getSoLuongTon() != null ? chiTiet.getSoLuongTon() : 0)
                    .sum();

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

        } else {
            this.chiTietSanPhams = List.of();
            this.tongSoLuong = 0;
            this.giaThapNhat = BigDecimal.ZERO;
            this.giaCaoNhat = BigDecimal.ZERO;
        }
    }
}