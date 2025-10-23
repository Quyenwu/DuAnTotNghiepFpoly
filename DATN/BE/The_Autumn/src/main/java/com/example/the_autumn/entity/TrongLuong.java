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

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "trong_luong")
public class TrongLuong {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_trong_luong", insertable = false, updatable = false)
    private String maTrongLuong;

    @Column(name = "ten_trong_luong")
    private String tenTrongLuong;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "trongLuong",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietSanPham> chiTietSanPham;
}
