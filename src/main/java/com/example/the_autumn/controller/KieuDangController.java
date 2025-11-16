package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.KieuDangRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.KieuDangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kieu-dang")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class KieuDangController {

    @Autowired
    private KieuDangService kdService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(kdService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody KieuDangRequest kieuDangRequest){
        kdService.add(kieuDangRequest);
        return new ResponseObject<>(null, "Thêm kiểu dáng thành công");
    }

}
