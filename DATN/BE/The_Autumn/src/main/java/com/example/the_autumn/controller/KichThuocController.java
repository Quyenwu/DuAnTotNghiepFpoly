package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.KichThuocRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.KichThuocService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/kich-thuoc")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class KichThuocController {

    @Autowired
    private KichThuocService ktService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(ktService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody KichThuocRequest kichThuocRequest){
        ktService.add(kichThuocRequest);
        return new ResponseObject<>(null, "Thêm kích thước thành công");
    }

}
