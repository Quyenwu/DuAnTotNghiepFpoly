package com.example.the_autumn.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KhachHangAuthResponse {

    private Integer id;
    private String maKhachHang;
    private String hoTen;
    private Boolean gioiTinh;
    private Date ngaySinh;
    private String email;
    private String sdt;
    private Boolean trangThai;
    private Date ngayTao;
    private Date ngaySua;

    private String accessToken;
    private String typeToken;
    private String userType;
}