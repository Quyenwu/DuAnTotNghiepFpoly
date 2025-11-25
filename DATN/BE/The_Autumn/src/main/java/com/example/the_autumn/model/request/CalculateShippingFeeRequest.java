package com.example.the_autumn.model.request;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CalculateShippingFeeRequest {
    private String donViVanChuyen; // GHN, GHTK
    private Integer idTinhGui;
    private Integer idQuanGui;
    private Integer idPhuongGui;
    private Integer idTinhNhan;
    private Integer idQuanNhan;
    private Integer idPhuongNhan;
    private String diaChiCuThe;
    private List<ShippingItem> items;

    @Getter
    @Setter
    public static class ShippingItem {
        private Integer idChiTietSanPham;
        private Integer soLuong;
        private BigDecimal giaBan;
        private Integer khoiLuong;
        private Integer chieuDai;
        private Integer chieuRong;
        private Integer chieuCao;
    }
}