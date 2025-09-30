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
@Table(name = "nha_san_xuat")
public class NhaSanXuat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "nhaSanXuat",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SanPham> sanPhams;

    @Column(name = "ma_nha_san_xuat")
    private String maNhaSanXuat;

    @Column(name = "ten_nha_san_xuat")
    private String tenNhaSanXuat;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
