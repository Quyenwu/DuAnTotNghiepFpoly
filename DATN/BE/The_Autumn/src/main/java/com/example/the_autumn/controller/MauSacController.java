package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.MauSacRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mau-sac")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class MauSacController {

    @Autowired
    private MauSacService msService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(msService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody MauSacRequest mauSacRequest){
        msService.add(mauSacRequest);
        return new ResponseObject<>(null, "Thêm màu sắc thành công");
    }

}
