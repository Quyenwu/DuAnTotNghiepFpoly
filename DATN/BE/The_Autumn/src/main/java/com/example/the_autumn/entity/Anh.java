package com.example.the_autumn.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "anh")
public class Anh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ctsp", referencedColumnName = "id", nullable = false)
    private ChiTietSanPham chiTietSanPham;

    @Column(name = "ma_anh")
    private String maAnh;

    @Column(name = "duong_dan_anh", length = 250)
    private String duongDanAnh;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
