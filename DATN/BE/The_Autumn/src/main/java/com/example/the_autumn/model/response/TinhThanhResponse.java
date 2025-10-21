package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.TinhThanh;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class TinhThanhResponse {
    private Integer id;
    private String tenTinh;

    public TinhThanhResponse(TinhThanh entity) {
        this.id = entity.getId();
        this.tenTinh = entity.getTenTinh();
    }
}
