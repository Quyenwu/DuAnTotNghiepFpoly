package com.example.the_autumn.model.request;

import com.example.the_autumn.entity.DiaChi;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateKhachHangRequest {
    String matKhau;
    String email;
    String sdt;
    String hoTen;
    Boolean gioiTinh;
    Boolean trangThai;
    Date ngaySinh;
    List<DiaChi> diaChi;
}
