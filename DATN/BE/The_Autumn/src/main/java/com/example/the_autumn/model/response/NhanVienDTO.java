package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NhanVienDTO {
    private Integer id;
    private Integer idChucVu;
    private String maNhanVien;
    private String hoTen;
    private Boolean gioiTinh;
    private Date ngaySinh;
    private String email;
    private String sdt;
    private String diaChi;
    private String hinhAnh;
    private String taiKhoan;
    private Boolean trangThai;
    private Date ngayTao;
    private Date ngaySua;
}
