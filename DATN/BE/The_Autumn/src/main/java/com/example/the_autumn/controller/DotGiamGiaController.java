package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.DotGiamGiaRequest;
import com.example.the_autumn.model.response.DotGiamGiaResponse;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.DotGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/dot-giam-gia")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class DotGiamGiaController {

    @Autowired
    private DotGiamGiaService dotGiamGiaService;

    @GetMapping
    public ResponseObject<?> getAllDotGiamGia() {
        return new ResponseObject<>(dotGiamGiaService.getAllDotGiamGia());
    }

    @GetMapping("/phan-trang")
    public ResponseObject<?> phanTrang(@RequestParam(value = "pageNo",defaultValue = "0") Integer pageNo, @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        return new ResponseObject<>(dotGiamGiaService.phanTrang(pageNo,pageSize));
    }

    @GetMapping("/detail/{id}")
    public ResponseObject<?> detail(@PathVariable Integer id){
        return new ResponseObject<>(dotGiamGiaService.getDotGiamGiaById(id));
    }

    @GetMapping("/san-pham/{idDot}")
    public ResponseObject<?> getSanPhamByDot(@PathVariable Integer idDot) {
        return new ResponseObject<>(dotGiamGiaService.getSanPhamByDot(idDot));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseObject<?> deletePhieuGiamGia(@PathVariable("id") Integer id){
        dotGiamGiaService.delete(id);
        return new ResponseObject<>(null,"Xoa thành công");
    }

    @PostMapping("/add")
    public ResponseObject<?> addDotGiamGia(@RequestBody DotGiamGiaRequest dotGiamGiaRequest){
        dotGiamGiaService.add(dotGiamGiaRequest);
        return new ResponseObject<>(null,"Thêm thành công");
    }

    @PutMapping("/update/{id}")
    public ResponseObject<?> updateDotGiamGia(@PathVariable Integer id,@RequestBody DotGiamGiaRequest dotGiamGiaRequest){
        dotGiamGiaService.update(id,dotGiamGiaRequest);
        return new ResponseObject<>(null,"Sửa thành công");
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(@PathVariable Integer id, @RequestParam Boolean trangThai) {
        dotGiamGiaService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhập trạng thái thành công");
    }
    @GetMapping("/search")
    public ResponseObject<?> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "tuNgay", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(value = "denNgay", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(value = "loaiGiamGia", required = false) Boolean loaiGiamGia,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai
    ) {
        List<DotGiamGiaResponse> result = dotGiamGiaService.searchDotGiamGia(
                keyword, tuNgay, denNgay, loaiGiamGia, trangThai
        );
        return new ResponseObject<>(result);
    }


}
