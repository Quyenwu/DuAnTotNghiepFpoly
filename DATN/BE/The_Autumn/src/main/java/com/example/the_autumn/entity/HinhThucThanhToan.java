package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hinh_thuc_thanh_toan")
public class HinhThucThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phuong_thuc",referencedColumnName = "id", nullable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_hoa_don",referencedColumnName = "id", nullable = false)
    private HoaDon hoaDon;

    @Column(name = "loai_thanh_toan", length = 500)
    private String loaiThanhToan;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
