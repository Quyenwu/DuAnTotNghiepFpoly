package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KhachHangResponse {

    private Integer id;
    private String hoTen;
    private String sdt;
    private Boolean gioiTinh;
    private String email;

    public KhachHangResponse(KhachHang khachHang) {
        this.id = khachHang.getId();
        this.hoTen = khachHang.getHoTen();
        this.sdt = khachHang.getSdt();
        this.gioiTinh = khachHang.getGioiTinh();
        this.email = khachHang.getEmail();
    }
}
