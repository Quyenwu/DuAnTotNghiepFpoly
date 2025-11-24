package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "giao_ca")
public class GiaoCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien",referencedColumnName = "id", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime  thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thucv")
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "so_tien_bat_dau")
    private BigDecimal soTienBatDau;

    @Column(name = "so_tien_ket_thuc")
    private BigDecimal soTienKetThuc;

    @Column(name = "tong_doanh_thu")
    private BigDecimal tongDoanhThu;

    @Column(name = "so_tien_chenh_lech")
    private BigDecimal soTienChenhLech;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
