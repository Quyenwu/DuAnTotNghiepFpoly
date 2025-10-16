package com.example.the_autumn.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "phieu_giam_gia")
public class PhieuGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_giam_gia", insertable = false, updatable = false)
    private String maGiamGia;

    @Column(name = "ten_chuong_trinh", length = 200)
    private String tenChuongTrinh;

    @Column(name = "loai_giam_gia", length = 50)
    private Boolean loaiGiamGia;

    @Column(name = "gia_tri_giam_gia", precision = 18, scale = 2)
    private BigDecimal giaTriGiamGia;

    @Column(name = "muc_gia_giam_toi_da", precision = 18, scale = 2)
    private BigDecimal mucGiaGiamToiDa;

    @Column(name = "gia_tri_dh_toi_thieu", precision = 18, scale = 2)
    private BigDecimal giaTriDonHangToiThieu;

    @Column(name = "mo_ta", length = 500)
    private String moTa;

    @Column(name = "kieu", length = 50)
    private Integer kieu;

    @Column(name = "so_luong_dung")
    private Integer soLuongDung;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private Date ngayTao;

    @Column(name = "ngay_bat_dau")
    private LocalDate ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    private LocalDate ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "phieuGiamGia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GiamGiaKhachHang> giamGiaKhachHangs;

    @OneToMany(mappedBy = "phieuGiamGia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;
}
