package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.GiamGiaKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/giam-gia-khach-hang")
public class GiamGiaKhachHangController {

    @Autowired
    private GiamGiaKhachHangService giamGiaKhachHangService;

    @GetMapping
    public ResponseObject<?> getAllGGKH(){
        return new ResponseObject<>(giamGiaKhachHangService.getAllGiamGiaKhachHang());
    }
}
