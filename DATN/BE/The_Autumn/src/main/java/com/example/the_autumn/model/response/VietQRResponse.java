package com.example.the_autumn.model.response;

public class VietQRResponse {
    private String status;
    private String qrImageUrl;
    private String paymentUrl;
    private String message;
    private Integer orderId;      // THÊM
    private String maHoaDon;      // THÊM

    // Constructor rỗng
    public VietQRResponse() {}


    public VietQRResponse(String status, String qrImageUrl, String paymentUrl, String message) {
        this.status = status;
        this.qrImageUrl = qrImageUrl;
        this.paymentUrl = paymentUrl;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getQrImageUrl() {
        return qrImageUrl;
    }

    public void setQrImageUrl(String qrImageUrl) {
        this.qrImageUrl = qrImageUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }
}