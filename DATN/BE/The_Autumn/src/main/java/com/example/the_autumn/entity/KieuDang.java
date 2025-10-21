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
@Table(name = "kieu_dang")
public class KieuDang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "kieuDang",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SanPham> sanPhams;

    @Column(name = "ma_kieu_dang", insertable = false, updatable = false)
    private String maKieuDang;

    @Column(name = "ten_kieu_dang")
    private String tenKieuDang;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
