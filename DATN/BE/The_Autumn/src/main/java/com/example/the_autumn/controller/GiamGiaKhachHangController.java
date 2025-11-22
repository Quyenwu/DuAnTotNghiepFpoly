package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.MaGiamGiaResponse;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.GiamGiaKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
<<<<<<< HEAD
import org.springframework.web.bind.annotation.*;
=======
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753

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
<<<<<<< HEAD
=======
    @GetMapping("/khach-hang/{khachHangId}")
    public ResponseEntity<ResponseObject<List<MaGiamGiaResponse>>> getMaGiamGiaByKhachHang(
            @PathVariable("khachHangId") Integer khachHangId) {

        System.out.println("🔍 API Called - khachHangId: " + khachHangId);

        try {
            List<MaGiamGiaResponse> result = giamGiaKhachHangService.getMaGiamGiaByKhachHang(khachHangId);
            System.out.println("✅ Found " + result.size() + " codes");
            return ResponseEntity.ok(new ResponseObject<>(result));
        } catch (Exception e) {
            System.err.println("❌ Error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(new ResponseObject<>(null, "Error: " + e.getMessage()));
        }
    }
>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753
}
