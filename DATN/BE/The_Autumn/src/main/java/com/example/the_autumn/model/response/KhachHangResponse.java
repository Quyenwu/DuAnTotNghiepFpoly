package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.KhachHang;
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
public class KhachHangResponse {

    private Integer id;
    private String sdt;
    private String hoTen;
    private Boolean gioiTinh;
    private String maKhachHang;
    private String email;
    private String matKhau;
    private Date ngaySinh;
    private Date ngayTao;
    private Date ngaySua;
    private Boolean trangThai;
    private Integer soLanMua;
    private Date ngayMuaGanNhat;
    private List<DiaChiDTO> diaChi;

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
