package com.example.the_autumn.model.response;

<<<<<<< HEAD
=======
import com.example.the_autumn.entity.QuanHuyen;
>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
<<<<<<< HEAD
=======
import java.util.List;
>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753

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
<<<<<<< HEAD
=======
    private List<QuanHuyen> quanHuyens;
>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753
    private Date ngayTao;
    private Date ngaySua;

    private String accessToken;
    private String typeToken;
    private String userType;
}