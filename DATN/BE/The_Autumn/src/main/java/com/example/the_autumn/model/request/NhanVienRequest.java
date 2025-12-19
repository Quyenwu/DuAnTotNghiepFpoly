package com.example.the_autumn.model.request;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.LichSuHoaDon;
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
public class NhanVienRequest {


    private Integer id;
    private Integer chucVuId;
    private String hoTen;
    private Boolean gioiTinh;
    private Date ngaySinh;
    private String email;
    private String sdt;
    private String diaChi;
    private String hinhAnh;
    private String matKhau;
    private Date ngaySua;
    private Boolean trangThai;
//    private List<HoaDon> hoaDons;
//    private List<LichSuHoaDon> lichSuHoaDons;
}
