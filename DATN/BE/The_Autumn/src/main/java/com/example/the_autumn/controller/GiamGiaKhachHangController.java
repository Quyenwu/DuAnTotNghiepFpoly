package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.GiamGiaKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/giam-gia-khach-hang")
public class GiamGiaKhachHangController {

    @Autowired
    private GiamGiaKhachHangService giamGiaKhachHangService;

    @GetMapping
    public ResponseObject<?> getAllGGKH(){
        return new ResponseObject<>(giamGiaKhachHangService.getAllGiamGiaKhachHang());
    }

    @DeleteMapping("/{discountId}/customer/{customerId}")
    public ResponseObject<?> removeCustomerFromDiscount(
            @PathVariable Long discountId,
            @PathVariable Long customerId) {
        try {
            boolean isDeleted = giamGiaKhachHangService.removeCustomerFromDiscount(discountId, customerId);
            if (isDeleted) {
                return new ResponseObject<>("Đã xoá khách hàng khỏi phiếu giảm giá thành công");
            } else {
                return new ResponseObject<>("Không tìm thấy bản ghi giảm giá khách hàng");
            }
        } catch (Exception e) {
            return new ResponseObject<>("Lỗi khi xoá khách hàng khỏi giảm giá: " + e.getMessage());
        }
    }
}
