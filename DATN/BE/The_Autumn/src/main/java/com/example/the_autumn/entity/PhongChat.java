package com.example.the_autumn.entity;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.TinNhan;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "phong_chat")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PhongChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_khach_hang")
    private KhachHang khachHang;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nhan_vien")
    private NhanVien nhanVien;

    @Column(name = "loai")
    private Integer loai = 0; // 0: khách-AI, 1: khách-nhân viên

    @Column(name = "trang_thai")
    private Integer trangThai = 1;

    @Column(name = "ngay_tao")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @OneToMany(mappedBy = "phongChat", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TinNhan> tinNhans;
}
