package com.example.the_autumn.model.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserInfoResponse {
    private Integer id;
    private String hoTen;
    private String email;
    private String userType;
    private String sdt;
    private Boolean gioiTinh;
    private String maUser;
    private String chucVu;

    public UserInfoResponse(Integer id, String hoTen, String email, String userType,
                            String sdt, Boolean gioiTinh, String maUser) {
        this.id = id;
        this.hoTen = hoTen;
        this.email = email;
        this.userType = userType;
        this.sdt = sdt;
        this.gioiTinh = gioiTinh;
        this.maUser = maUser;
    }
}