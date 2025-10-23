package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.TayAoRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.TayAoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/tay-ao")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class TayAoController {

    @Autowired
    private TayAoService taService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(taService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody TayAoRequest tayAoRequest){
        taService.add(tayAoRequest);
        return new ResponseObject<>(null, "Thêm tay áo thành công");
    }

}
