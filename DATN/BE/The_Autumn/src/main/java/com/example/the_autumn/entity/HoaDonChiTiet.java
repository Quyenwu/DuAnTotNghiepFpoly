package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hoa_don_chi_tiet")
public class HoaDonChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don",referencedColumnName = "id", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ctsp",referencedColumnName = "id", nullable = false)
    private ChiTietSanPham chiTietSanPham;

    @Column(name = "ma_hoa_don_chi_tiet", length = 20, unique = true)
    private String maHoaDonChiTiet;

    @Column(name = "so_luong")
    private Integer soLuong;

    @Column(name = "gia_ban", precision = 18, scale = 2)
    private BigDecimal giaBan;

    @Column(name = "thanh_tien", precision = 18, scale = 2)
    private BigDecimal thanhTien;

    @Column(name = "ghi_chu", length = 500)
    private String ghiChu;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
