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
@Table(name = "quan_huyen")
public class QuanHuyen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_quan", nullable = false, length = 100)
    private String tenQuan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tinh",referencedColumnName = "id", nullable = false)
    private TinhThanh tinhThanh;

    @OneToMany(mappedBy = "quanHuyen", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaChi> diaChis;
}
