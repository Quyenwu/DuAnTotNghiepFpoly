package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.QuanHuyen;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class QuanHuyenResponse {
    private Integer id;
    private String tenQuan;

    public QuanHuyenResponse(QuanHuyen entity) {
        this.id = entity.getId();
        this.tenQuan = entity.getTenQuan();
    }
}
