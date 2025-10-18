package com.example.the_autumn.model.request;

import com.example.the_autumn.model.response.DiaChiDTO;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CreateKhachHang {
    String tenTaiKhoan;
    String matKhau;
    String email;
    String sdt;
    String hoTen;
    Boolean gioiTinh;
    Boolean trangThai;
    Date ngaySinh;
    List<DiaChiDTO> diaChi;
}
