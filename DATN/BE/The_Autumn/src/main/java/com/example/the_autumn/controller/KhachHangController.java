package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.KhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/khach-hang")
public class KhachHangController {

    @Autowired
    private KhachHangService khachHangService;

    @GetMapping
    public ResponseObject<?> getAllKhachHang() {
        List<KhachHangResponse> danhSachKhachHang = khachHangService.getKhachHangByPGG();
        return new ResponseObject<>(danhSachKhachHang);
    }

}
