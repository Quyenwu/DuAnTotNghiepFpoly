package com.example.the_autumn.model.request;

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
public class UpdateKhachHang {
     String sdt;
     String hoTen;
     Boolean gioiTinh;
     String matKhau;
     Date ngaySua;
     Boolean trangThai;
    List<UpdateDiaChi> diaChi;
}
