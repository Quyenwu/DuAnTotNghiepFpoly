package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.NhanVien;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.Optional;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class NhanVienResponse {
    private Integer id;
    private Integer chucVuId;
    private String chucVuName;
    private String maNhanVien;
    private String hoTen;
    private Boolean gioiTinh;
    private Date ngaySinh;
    private String email;
    private String sdt;
    private String diaChi;
    private String hinhAnh;
    private String taiKhoan;
    private String matKhau;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;

    public NhanVienResponse(NhanVien nhanVien) {
        this.id = nhanVien.getId();
        this.chucVuId = nhanVien.getChucVu().getId();
        this.chucVuName = nhanVien.getChucVu().getTenChucVu();
        this.maNhanVien = nhanVien.getMaNhanVien();
        this.hoTen = nhanVien.getHoTen();
        this.gioiTinh = nhanVien.getGioiTinh();
        this.ngaySinh = nhanVien.getNgaySinh();
        this.email = nhanVien.getEmail();
        this.sdt = nhanVien.getSdt();
        this.diaChi = nhanVien.getDiaChi();
        this.hinhAnh = nhanVien.getHinhAnh();
        this.taiKhoan = nhanVien.getTaiKhoan();
        this.matKhau = nhanVien.getMatKhau();
        this.ngayTao = nhanVien.getNgayTao();
        this.ngaySua = nhanVien.getNgaySua();
        this.trangThai = nhanVien.getTrangThai();
    }


}
