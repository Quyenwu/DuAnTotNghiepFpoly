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

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "tinh_thanh")
public class TinhThanh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_tinh")
    private String tenTinh;

    @OneToMany(mappedBy = "tinhThanh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<QuanHuyen> quanHuyens;

    @OneToMany(mappedBy = "tinhThanh", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaChi> diaChis;
}
