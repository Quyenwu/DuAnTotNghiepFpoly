package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "banner")
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @Column(name = "duong_dan_anh", length = 250)
    private String duongDanAnh;

    @Column(name = "vi_tri")
    private Integer viTri;

    @Column(name = "trang_thai")
    private Boolean trangThai;

}
