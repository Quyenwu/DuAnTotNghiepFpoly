package com.example.the_autumn.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "khach_hang")
public class KhachHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ho_ten", length = 100)
    private String hoTen;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Column(name = "email", length = 100, unique = true)
    private String email;

    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "mat_khau", length = 100, nullable = false)
    private String matKhau;

    @Column(name = "ngay_tao",insertable = false, updatable = false )
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "khachHang",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<DiaChi> diaChi;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GiamGiaKhachHang> giamGiaKhachHangs;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "khachHang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichSuHoaDon> lichSuHoaDons;
}
