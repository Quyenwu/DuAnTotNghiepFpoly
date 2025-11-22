package com.example.the_autumn.dto;

import com.example.the_autumn.entity.HoaDon;

/**
 * Response object khi tạo đơn hàng thành công
 */
public class OrderPlacementResponse {

    private HoaDon hoaDon;
    private String paymentUrl; // null nếu COD, có giá trị nếu VNPAY

    // ===================================================================
    // CONSTRUCTORS
    // ===================================================================

    public OrderPlacementResponse() {
    }

    public OrderPlacementResponse(HoaDon hoaDon, String paymentUrl) {
        this.hoaDon = hoaDon;
        this.paymentUrl = paymentUrl;
    }

    // ===================================================================
    // GETTERS & SETTERS
    // ===================================================================

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public boolean isOnlinePayment() {
        return paymentUrl != null && !paymentUrl.isEmpty();
    }

    /**
     * Lấy mã hóa đơn (shortcut)
     */
    public String getMaHoaDon() {
        return hoaDon != null ? hoaDon.getMaHoaDon() : null;
    }

    @Override
    public String toString() {
        return "OrderPlacementResponse{" +
                "maHoaDon=" + getMaHoaDon() +
                ", hasPaymentUrl=" + isOnlinePayment() +
                '}';
    }
}