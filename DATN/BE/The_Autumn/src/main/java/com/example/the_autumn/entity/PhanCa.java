package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "phan_ca")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanCa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    // ma_phan_ca computed trong SQL
    @Column(name = "ma_phan_ca", insertable = false, updatable = false)
    private String maPhanCa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ca_lam_viec", nullable = false)
    private CaLamViec caLamViec;

    @Column(name = "ngay_phan_ca", nullable = false)
    private LocalDate ngayPhanCa;

    @Column(name = "ghi_chu")
    private String ghiChu;

    @Column(name = "trang_thai")
    private Boolean trangThai = true;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao;

    @Column(name = "ngay_sua")
    private LocalDateTime ngaySua;
}
