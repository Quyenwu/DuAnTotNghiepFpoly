package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chi_tiet_san_pham")
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_san_pham",referencedColumnName = "id", nullable = false)
    private SanPham sanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_mau_sac",referencedColumnName = "id", nullable = false)
    private MauSac mauSac;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kich_thuoc",referencedColumnName = "id", nullable = false)
    private KichThuoc kichThuoc;

    @Column(name = "ma_vach", length = 50, unique = true)
    private String maVach;

    @Column(name = "so_luong_ton")
    private Integer soLuongTon;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "gia_ban", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaBan;

    @Column(name = "ngay_tao")
    private LocalDate ngayTao;

    @Column(name = "ngay_sua")
    private LocalDate ngaySua;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @OneToMany(mappedBy = "chiTietSanPham", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Anh> anhs;

    @OneToMany(mappedBy = "chiTietSanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DotGiamGiaChiTiet> dotGiamGiaChiTiets;

    @OneToMany(mappedBy = "chiTietSanPham", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTiets;


}
