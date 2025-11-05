package com.example.the_autumn.entity;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "lich_su_thanh_toan")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class LichSuThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_hoa_don",referencedColumnName = "id", nullable = false)
    private HoaDon hoaDon;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_phuong_thuc",referencedColumnName = "id",nullable = false)
    private PhuongThucThanhToan phuongThucThanhToan;

    @Column(name = "ma_giao_dich", insertable = false, updatable = false)
    private String maGiaoDich;

    @Column(name = "so_tien", precision = 18, scale = 2)
    private BigDecimal soTien;

    @Column(name = "ghi_chu", length = 200)
    private String ghiChu;

    @Column(name = "ngay_thanh_toan")
    private Date ngayThanhToan;

    @Column(name = "trang_thai")
    private Boolean trangThai;
}
