package com.example.the_autumn.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "san_pham")
public class SanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nha_san_xuat",referencedColumnName = "id", nullable = false)
    private NhaSanXuat nhaSanXuat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_xuat_xu",referencedColumnName = "id", nullable = false)
    private XuatXu xuatXu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chat_lieu",referencedColumnName = "id", nullable = false)
    private ChatLieu chatLieu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_kieu_dang",referencedColumnName = "id", nullable = false)
    private KieuDang kieuDang;

    @Column(name = "ma_san_pham", length = 20, unique = true)
    private String maSanPham;

    @Column(name = "ten_san_pham", length = 200)
    private String tenSanPham;

    @Column(name = "gia", precision = 18, scale = 2, nullable = false)
    private BigDecimal gia;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "nguoi_sua")
    private Integer nguoiSua;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "sanPham",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietSanPham> chiTietSanPham;
}
