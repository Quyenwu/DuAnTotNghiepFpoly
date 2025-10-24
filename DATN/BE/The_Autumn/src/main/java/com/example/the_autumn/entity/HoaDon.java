package com.example.the_autumn.entity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

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
    @JsonIgnoreProperties({"hoaDons", "hibernateLazyInitializer", "handler"})  // ✅ Thêm
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien",referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"hoaDons", "hibernateLazyInitializer", "handler"})  // ✅ Thêm
    private NhanVien nhanVien;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phieu_giam_gia",referencedColumnName = "id", nullable = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ✅ Thêm
    private PhieuGiamGia phieuGiamGia;

    @Column(name = "ma_hoa_don", insertable = false, updatable = false)
    private String maHoaDon;

    @Column(name = "loai_hoa_don", length = 100)
    private Boolean loaiHoaDon;

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



    @Column(name = "ngay_thanh_toan")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")  // ⭐ THÊM CHO DATE (nếu payload gửi full datetime)
    private Date ngayThanhToan;  // ⭐ ĐỔI THÀNH LOCALDATETIME

    @Column(name = "ngay_tao")
    @JsonFormat(pattern = "yyyy-MM-dd")  // ⭐ THÊM: Parse string "2025-10-13" thành LocalDateTime (default time 00:00:00)
    private Date ngayTao;

    @Column(name = "ngay_sua")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date ngaySua;

    @Column(name = "nguoi_tao")
    private Integer nguoiTao;

    @Column(name = "nguoi_sua")
    private Integer nguoiSua;

    @Column(name = "trang_thai")
    private Integer trangThai;

    @OneToMany(mappedBy = "hoaDon", fetch = FetchType.LAZY,cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // ✅ Thêm để tránh vòng lặp JSON
    private List<LichSuThanhToan> lichSuThanhToans;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference  // ✅ Thêm để tránh vòng lặp JSON
    private List<HoaDonChiTiet> hoaDonChiTiets;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference  // ✅ Thêm để tránh vòng lặp JSON
    private List<LichSuHoaDon> lichSuHoaDons;

    @OneToMany(mappedBy = "hoaDon", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JsonManagedReference
    private List<HinhThucThanhToan> hinhThucThanhToans;


}
