package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.AddKhachHangRequest;
import com.example.the_autumn.model.request.UpdateKhachHangRequest;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public ResponseObject<?> getAllKhachHang() {
        List<KhachHangResponse> danhSachKhachHang = khachHangService.getAllKhachHang();
        return new ResponseObject<>(danhSachKhachHang);
    }
    @GetMapping("/all")
    public ResponseEntity<List<KhachHangResponse>> getData() {
        List<KhachHangResponse> data = khachHangService.getAllKhachHang();
        return ResponseEntity.ok(data);
    }
    @PostMapping("/add")
    public ResponseEntity<KhachHangResponse> addKhachHang(@RequestBody AddKhachHangRequest request) {
        KhachHangResponse response = khachHangService.createKhachHang(request);
        return ResponseEntity.ok(response);
    }
    @PutMapping("/update/{id}")
    public ResponseEntity<KhachHangResponse> updateKhachHang(
            @PathVariable Integer id,
            @RequestBody UpdateKhachHangRequest request) {

        KhachHangResponse response = khachHangService.updateKhachHang(id, request);
        return ResponseEntity.ok(response);
    }
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteKhachHang(@PathVariable Integer id) {
        khachHangService.deleteKhachHang(id);
        return ResponseEntity.ok("Khách hàng và chi tiết liên quan đã được xóa");
    }
    @GetMapping("/detail/{id}")
    public ResponseEntity<KhachHangResponse> getKhachHangDetail(@PathVariable Integer id) {
        KhachHangResponse response = khachHangService.detailKhachHang(id);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/search")
    public List<KhachHangResponse> searchKhachHang(@RequestParam String keyword) {
        return khachHangService.searchKhachHang(keyword);
    }

    @GetMapping("/filter")
    public List<KhachHangResponse> filterKhachHang(
            @RequestParam(required = false) Boolean gioiTinh,
            @RequestParam(required = false) Boolean trangThai
    ) {
        return khachHangService.filterKhachHang(gioiTinh, trangThai);
    }
}
