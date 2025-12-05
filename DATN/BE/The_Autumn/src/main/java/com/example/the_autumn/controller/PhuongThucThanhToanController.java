package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.PhuongThucThanhToanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/phuong-thuc-thanh-toan")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://192.203.4.118:5173"})
public class PhuongThucThanhToanController {

    @Autowired
    private PhuongThucThanhToanService phuongThucThanhToanService;

    @GetMapping
    public ResponseObject<?> getPhuongThucThanhToan(){
        return new ResponseObject<>(phuongThucThanhToanService.getPhuongThucThanhToan());
    }
}
