package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.XuatXuRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.XuatXuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/xuat-xu")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class XuatXuController {

    @Autowired
    private XuatXuService xxService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(xxService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody XuatXuRequest xuatXuRequest){
        xxService.add(xuatXuRequest);
        return new ResponseObject<>(null, "Thêm xuất xứ thành công");
    }

}