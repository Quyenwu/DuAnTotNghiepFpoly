package com.example.the_autumn.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "dot_giam_gia")
public class DotGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ma_dot_giam_gia",  insertable = false, updatable = false)
    private String maGiamGia;

    @Column(name = "ten_dot", length = 500)
    private String tenDot;

    @Column(name = "loai_giam_gia")
    private Boolean loaiGiamGia;

    @Column(name = "gia_tri_giam", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriGiam;

    @Column(name = "gia_tri_toi_thieu", precision = 18, scale = 2, nullable = false)
    private BigDecimal giaTriToiThieu;

    @Column(name = "ngay_tao")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date ngayTao;

    @Column(name = "ngay_bat_dau")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date ngayBatDau;

    @Column(name = "ngay_ket_thuc")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date ngayKetThuc;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "dotGiamGia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DotGiamGiaChiTiet> dotGiamGiaChiTiets;
}
