package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KhachHangResponse {

    private Integer id;
    private String maKhachHang;
    private String hoTen;
    private String sdt;
    private Date ngaySinh;
    private Boolean gioiTinh;
    private String email;
    private Boolean trangThai;
    private Date ngayTao;
    private Date ngaySua;
    private Integer soLanMua;
    private Date ngayMuaGanNhat;
    List<DiaChiResponse> diaChi;

    public KhachHangResponse(KhachHang khachHang) {
        this.id = khachHang.getId();
        this.maKhachHang=khachHang.getMaKhachHang();
        this.hoTen = khachHang.getHoTen();
        this.sdt = khachHang.getSdt();
        this.ngaySinh = khachHang.getNgaySinh();
        this.gioiTinh = khachHang.getGioiTinh();
        this.email = khachHang.getEmail();
        this.trangThai=khachHang.getTrangThai();
        this.ngayTao=khachHang.getNgayTao();
        this.ngaySua=khachHang.getNgaySua();
        if (khachHang.getDiaChi()!=null){
            this.diaChi=khachHang.getDiaChi().stream()
                    .map(DiaChiResponse::new)
                    .collect(Collectors.toList());
        }
    }
    public KhachHangResponse(KhachHang khachHang, Integer soLanMua, Date ngayMuaGanNhat) {
        this(khachHang);
        this.soLanMua = soLanMua;
        this.ngayMuaGanNhat = ngayMuaGanNhat;
    }
}
