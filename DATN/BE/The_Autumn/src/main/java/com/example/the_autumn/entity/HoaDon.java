package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "hoa_don")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang",referencedColumnName = "id", nullable = false)
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien",referencedColumnName = "id", nullable = false)
    private NhanVien nhanVien;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia",referencedColumnName = "id", nullable = false)
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "ma_hoa_don", insertable = false, updatable = false)
    private String maHoaDon;

    @Column(name = "loai_hoa_don", length = 100)
    private String loaiHoaDon;

    @Column(name = "phi_van_chuyen", precision = 18, scale = 2)
    private BigDecimal phiVanChuyen;

    @Column(name = "tong_tien", precision = 18, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "tong_tien_sau_giam", precision = 18, scale = 2)
    private BigDecimal tongTienSauGiam;

    @Column(name = "ghi_chu", length = 200)
    private String ghiChu;

    @Column(name = "dia_chi_khach_hang",  length = 200)
    private String diaChiKhachHang;

    @Column(name = "hinh_thuc_thanh_toan",  length = 200)
    private String hinhThucThanhToan;

    @Column(name = "ngay_thanh_toan")
    private Date ngayThanhToan;

    @Column(name = "ngay_tao", insertable = false, updatable = false)
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "nguoi_sua")
    private Integer nguoiSua;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<HinhThucThanhToan> hinhThucThanhToans;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<LichSuThanhToan> lichSuThanhToans;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDonChiTiet> hoaDonChiTiets;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichSuHoaDon> lichSuHoaDons;

}
