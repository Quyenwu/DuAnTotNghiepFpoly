package com.example.the_autumn.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "dia_chi")
public class DiaChi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang",referencedColumnName = "id", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinh",referencedColumnName = "id", nullable = false)
    private TinhThanh tinhThanh;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_quan",referencedColumnName = "id", nullable = false)
    private QuanHuyen quanHuyen;

    @Column(name = "ten_dia_chi", length = 200)
    private String tenDiaChi;

    @Column(name = "dia_chi_cu_the", length = 200)
    private String diaChiCuThe;

    @Column(name = "trang_thai", nullable = false)
    private Boolean trangThai = true;


}
