package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private Integer id;
    private String sdt;
    private String hoTen;
    private Boolean gioiTinh;
    private String maKhachHang;
    private String email;
    private String tenTaiKhoan;
    private String matKhau;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;
    private List<DiaChiDTO> diaChi;
}
