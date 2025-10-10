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

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "nhan_vien")
public class NhanVien {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_chuc_vu", referencedColumnName = "id", nullable = false)
    private ChucVu chucVu;

    @Column(name = "ma_nhan_vien",length = 20, unique = true)
    private String maNhanVien;

    @Column(name = "ho_ten" , length = 100)
    private String hoTen;

    @Column(name = "gioi_tinh")
    private Boolean gioiTinh;

    @Column(name = "ngay_sinh")
    private Date ngaySinh;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "sdt", length = 15)
    private String sdt;

    @Column(name = "dia_chi", length = 500)
    private String diaChi;

    @Column(name = "hinh_anh", length = 254)
    private String hinhAnh;


    @Column(name = "mat_khau", length = 50, nullable = false)
    private String matKhau;

    @Column(name = "ngay_tao")
    private Date ngayTao;

    @Column(name = "ngay_sua")
    private Date ngaySua;

    @Column(name = "trang_thai")
    private Boolean trangThai;

    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HoaDon> hoaDons;

    @OneToMany(mappedBy = "nhanVien", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<LichSuHoaDon> lichSuHoaDons;
}
