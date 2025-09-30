package com.example.the_autumn.entity;

import jakarta.persistence.*;
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
@Table(name = "dot_giam_gia_chi_tiet")
public class DotGiamGiaChiTiet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_ctsp", referencedColumnName = "id", nullable = false)
    private ChiTietSanPham chiTietSanPham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_dot_giam_gia", referencedColumnName = "id", nullable = false)
    private DotGiamGia dotGiamGia;

    @Column(name = "do_uu_tien")
    private Integer doUuTien;
}
