package com.example.the_autumn.model.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class AddVariantRequest {
    private Integer idSanPham;
    private List<Integer> idMauSacs;
    private Integer idKichThuoc;
}
