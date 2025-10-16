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
@Table(name = "dia_chi")
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang",referencedColumnName = "id", nullable = false)
    private KhachHang khachHang;

    @Column(name = "ten_dia_chi", length = 200)
    private String tenDiaChi;

    @Column(name = "thanh_pho", length = 100)
    private String thanhPho;

    @Column(name = "quan", length = 100)
    private String quan;

    @Column(name = "dia_chi_cu_the", length = 200)
    private String diaChiCuThe;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
