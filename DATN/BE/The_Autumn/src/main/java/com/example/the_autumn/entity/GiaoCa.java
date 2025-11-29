package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "giao_ca")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GiaoCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ma_giao_ca computed trong SQL
    @Column(name = "ma_giao_ca", insertable = false, updatable = false)
    private String maGiaoCa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    @Column(name = "thoi_gian_bat_dau")
    private LocalDateTime thoiGianBatDau;

    @Column(name = "thoi_gian_ket_thuc")
    private LocalDateTime thoiGianKetThuc;

    @Column(name = "so_tien_bat_dau", precision = 18, scale = 2)
    private BigDecimal soTienBatDau;

    @Column(name = "so_tien_ket_thuc", precision = 18, scale = 2)
    private BigDecimal soTienKetThuc;

    @Column(name = "tong_doanh_thu", precision = 18, scale = 2)
    private BigDecimal tongDoanhThu;

    @Column(name = "so_tien_chenh_lech", precision = 18, scale = 2)
    private BigDecimal soTienChenhLech;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @PrePersist
    public void prePersist() {
        if (ngayTao == null) {
            ngayTao = LocalDateTime.now();
        }
        if (trangThai == null) {
            trangThai = true;
        }
    }
}
