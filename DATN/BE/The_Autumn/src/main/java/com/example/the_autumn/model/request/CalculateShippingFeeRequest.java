package com.example.the_autumn.model.request;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class CalculateShippingFeeRequest {
    private String donViVanChuyen;
    private Integer idTinhGui;
    private Integer idQuanGui;
    private Integer idTinhNhan;
    private Integer idQuanNhan;
    private Integer idPhuongNhan; // Có thể null
    private String diaChiCuThe;
    private List<ShippingItem> items;

    @Getter
    @Setter
    public static class ShippingItem {
        private Integer idChiTietSanPham;
        private Integer soLuong;
        private BigDecimal giaBan;
        private Object khoiLuong;
        private Object chieuDai;
        private Object chieuRong;
        private Object chieuCao;

        public Integer getParsedKhoiLuong() {
            return parseIntegerValue(khoiLuong, 200);
        }

        public Integer getParsedChieuDai() {
            return parseIntegerValue(chieuDai, 20);
        }

        public Integer getParsedChieuRong() {
            return parseIntegerValue(chieuRong, 15);
        }

        public Integer getParsedChieuCao() {
            return parseIntegerValue(chieuCao, 10);
        }

        private Integer parseIntegerValue(Object value, Integer defaultValue) {
            if (value == null) {
                return defaultValue;
            }

            if (value instanceof Integer) {
                return (Integer) value;
            }

            if (value instanceof String) {
                String stringValue = ((String) value).replaceAll("[^\\d]", "");
                try {
                    return Integer.parseInt(stringValue);
                } catch (NumberFormatException e) {
                    return defaultValue;
                }
            }

            if (value instanceof Number) {
                return ((Number) value).intValue();
            }

            return defaultValue;
        }
    }
}