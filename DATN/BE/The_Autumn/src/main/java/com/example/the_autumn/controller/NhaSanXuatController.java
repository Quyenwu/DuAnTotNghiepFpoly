package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.NhaSanXuatRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.NhaSanXuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nha-san-xuat")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class NhaSanXuatController {

    @Autowired
    private NhaSanXuatService nsxService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(nsxService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody NhaSanXuatRequest nhaSanXuatRequest){
        nsxService.add(nhaSanXuatRequest);
        return new ResponseObject<>(null, "Thêm nhà sản xuất thành công");
    }
}
