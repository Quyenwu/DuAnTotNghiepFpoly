package com.example.the_autumn.model.response;

public class TrangThaiHoaDonRespone {
    public static final Integer CHO_XAC_NHAN = 0;
    public static final Integer CHO_GIAO_HANG = 1;
    public static final Integer DANG_VAN_CHUYEN = 2;
    public static final Integer DA_THANH_TOAN = 3;
    public static final Integer HOAN_THANH = 4;
    public static final Integer DA_HUY = 5;

    public static String getText(Integer trangThai) {
        if (trangThai == null) return "Không xác định";
        switch (trangThai) {
            case 0: return "Chờ xác nhận";
            case 1: return "Chờ giao hàng ";
            case 2: return "Đang vận chuyển";
            case 3: return "Đã thanh toán";
            case 4: return "Hoàn thành";
            case 5: return "Đã hủy";
            default: return "Không xác định";
        }
    }
}