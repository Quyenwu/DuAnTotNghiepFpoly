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
@Table(name = "kich_thuoc")
public class KichThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_kich_thuoc")
    private String maKichThuoc;

    @Column(name = "ten_kich_thuoc")
    private String tenKichThuoc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "kichThuoc",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ChiTietSanPham> chiTietSanPham;
}
