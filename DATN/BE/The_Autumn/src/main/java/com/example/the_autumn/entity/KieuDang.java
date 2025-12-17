package com.example.the_autumn.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
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

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "kieu_dang")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
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
    @Column(name = "ngay_tao")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngaySua;
}
