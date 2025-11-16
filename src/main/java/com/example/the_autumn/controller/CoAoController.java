package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.CoAoRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.CoAoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/co-ao")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class CoAoController {

    @Autowired
    private CoAoService caService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(caService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody CoAoRequest coAoRequest){
        caService.add(coAoRequest);
        return new ResponseObject<>(null, "Thêm cổ áo thành công");
    }

}
