package com.example.the_autumn.controller;

import com.example.the_autumn.dto.MaGiamGiaResponse;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.GiamGiaKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/giam-gia-khach-hang")
public class GiamGiaKhachHangController {

    @Autowired
    private GiamGiaKhachHangService giamGiaKhachHangService;

    @GetMapping
    public ResponseObject<?> getAllGGKH(){
        return new ResponseObject<>(giamGiaKhachHangService.getAllGiamGiaKhachHang());
    }
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
}
