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
@Table(name = "xuat_xu")
public class XuatXu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToMany(mappedBy = "xuatXu", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SanPham> sanPhams;

    @Column(name = "ma_xuat_xu", insertable = false, updatable = false)
    private String maXuatXu;

    @Column(name = "ten_xuat_xu")
    private String tenXuatXu;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
