package com.example.the_autumn.controller;

import com.example.the_autumn.entity.GiaoCa;
import com.example.the_autumn.model.request.GiaoCaRequest;
import com.example.the_autumn.service.GiaoCaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/giaoca")
public class GiaoCaController {

    @Autowired
    private GiaoCaService giaoCaService;

    @PostMapping("/bat-dau")
    public GiaoCa batDau(@RequestBody GiaoCaRequest req) {
        return giaoCaService.batDauCa(
                req.getNhanVienId(),
                req.getSoTienBatDau(),
                req.getGhiChu()
        );
    }

    @PostMapping("/ket-thuc")
    public GiaoCa ketThuc(@RequestBody GiaoCaRequest req) {
        return giaoCaService.ketThucCa(
                req.getNhanVienId(),
                req.getSoTienKetThuc(),
                req.getGhiChu()
        );
    }

    @GetMapping("/dang-hoat-dong/{idNhanVien}")
    public GiaoCa caDangHoatDong(@PathVariable Integer idNhanVien) {
        return giaoCaService.getCaDangHoatDong(idNhanVien);
    }
}
