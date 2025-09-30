package com.example.the_autumn.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "phuong_thuc_thanh_toan")
public class PhuongThucThanhToan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_phuong_thuc_thanh_toan", unique = true, length = 20, nullable = false)
    private String maPhuongThucThanhToan;

    @Column(name = "ten_phuong_thuc_thanh_toan", length = 100)
    private String tenPhuongThucThanhToan;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "phuongThucThanhToan", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<HinhThucThanhToan> hinhThucThanhToans;

    @OneToMany(mappedBy = "phuongThucThanhToan", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<LichSuThanhToan> lichSuThanhToans;
}
