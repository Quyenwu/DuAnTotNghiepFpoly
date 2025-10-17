package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KhachHangResponse {

    private Integer id;
    private String hoTen;
    private String sdt;
    private Date ngaySinh;
    private Boolean gioiTinh;
    private String email;
    private Integer soLanMua;
    private Date ngayMuaGanNhat;

    public KhachHangResponse(KhachHang khachHang) {
        this.id = khachHang.getId();
        this.hoTen = khachHang.getHoTen();
        this.sdt = khachHang.getSdt();
        this.ngaySinh = khachHang.getNgaySinh();
        this.gioiTinh = khachHang.getGioiTinh();
        this.email = khachHang.getEmail();
    }

    public KhachHangResponse(KhachHang khachHang, Integer soLanMua, Date ngayMuaGanNhat) {
        this(khachHang);
        this.soLanMua = soLanMua;
        this.ngayMuaGanNhat = ngayMuaGanNhat;
    }
}
