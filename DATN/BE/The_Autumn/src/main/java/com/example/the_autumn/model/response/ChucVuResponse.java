package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.NhanVien;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChucVuResponse {
    private Integer id;

    private String maChucVu;

    private String tenChucVu;

    private Boolean trangThai;

    public ChucVuResponse(ChucVu chucVu) {
        this.id = chucVu.getId();
        this.maChucVu = chucVu.getMaChucVu();
        this.tenChucVu = chucVu.getTenChucVu();
        this.trangThai = chucVu.getTrangThai();
    }
}
