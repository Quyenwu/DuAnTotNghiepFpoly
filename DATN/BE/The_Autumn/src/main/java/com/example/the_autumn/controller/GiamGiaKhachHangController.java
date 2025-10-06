package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.GiamGiaKhachHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/giam-gia-khach-hang")
public class GiamGiaKhachHangController {

    @Autowired
    private GiamGiaKhachHangService giamGiaKhachHangService;

    @GetMapping("/khach-hang/{id}")
    public ResponseObject<?> getGiamGiaKhachHang(@PathVariable("id") Integer idPhieu) {
        return new ResponseObject<>(giamGiaKhachHangService.getKhachHangTheoPhieuGiamGia(idPhieu));
    }

    @PostMapping("/them-phieu")
    public ResponseObject<?> themPhieu(@RequestParam Integer idPhieu, @RequestBody List<Integer> idKhachHang){
        giamGiaKhachHangService.ganPhieuChoKhachHang(idPhieu, idKhachHang);
        return new ResponseObject<>(null,"Thêm phiếu thành công");
    }

}
