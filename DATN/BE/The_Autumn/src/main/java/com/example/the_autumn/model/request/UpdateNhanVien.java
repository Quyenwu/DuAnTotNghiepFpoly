package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateNhanVien {
    private Integer id;
    private Integer idChucVu;
    private String hoTen;
    private Boolean gioiTinh;
    private Date ngaySinh;
    private String email;
    private String sdt;
    private String diaChi;
    private String hinhAnh;
    private String taiKhoan;
    private String matKhau;
    private Boolean trangThai;
}
