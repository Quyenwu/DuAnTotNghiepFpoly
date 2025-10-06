package com.example.the_autumn.model.request;

import com.example.the_autumn.entity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PhieuGiamGiaRequesst {

    private Integer id;
    private String maGiamGia;
    private String tenChuongTrinh;
    private Boolean loaiGiamGia;
    private BigDecimal giaTriGiamGia;
    private BigDecimal mucGiaGiamToiDa;
    private BigDecimal giaTriDonHangToiThieu;
    private String moTa;
    private Integer soLuong;
    private Integer kieu;
    private Date ngayTao;
    private Date ngayBatDau;
    private Date ngayKetThuc;
    private Boolean trangThai;
    private List<Integer> idKhachHangs;
}
