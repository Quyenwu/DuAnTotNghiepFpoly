package com.example.the_autumn.dto;

public class TrangThaiHoaDon {
    public static final Integer CHO_XAC_NHAN = 0;
    public static final Integer CHO_THANH_TOAN = 1;
    public static final Integer DA_THANH_TOAN = 2;
    public static final Integer DA_HUY = 3;

    public static String getText(Integer trangThai) {
        if (trangThai == null) return "Không xác định";
        switch (trangThai) {
            case 0: return "Chờ xác nhận";
            case 1: return "Chờ thanh toán";
            case 2: return "Đã thanh toán";
            case 3: return "Đã hủy";
            default: return "Không xác định";
        }
    }
}