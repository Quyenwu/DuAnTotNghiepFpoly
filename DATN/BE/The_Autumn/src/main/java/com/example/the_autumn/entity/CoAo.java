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
@Table(name = "co_ao")
public class CoAo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_co_ao", insertable = false, updatable = false)
    private String maCoAo;

    @Column(name = "ten_co_ao")
    private String tenCoAo;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "coAo",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<SanPham> sanPham;
}
