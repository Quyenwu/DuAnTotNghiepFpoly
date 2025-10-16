package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.ChucVuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chuc-vu")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class ChucVuController {

    @Autowired
    private ChucVuService chucVuService;

    @GetMapping
    public ResponseObject<?> getAllChucVu(){
        return new ResponseObject<>(chucVuService.getAllChucVu());
    }


}
