package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.PhieuGiamGiaRequesst;
import com.example.the_autumn.model.response.PhieuGiamGiaRespone;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.PhieuGiamGiaService;
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
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class PhieuGiamGiaController {

    @Autowired
    private PhieuGiamGiaService phieuGiamGiaService;

    @GetMapping
    public ResponseObject<?> gettAllPhieuGiamGia(){
        return new ResponseObject<>(phieuGiamGiaService.getAllPhieuGiamGia());
    }

    @GetMapping("/phan-trang")
    public ResponseObject<?> phanTrang(@RequestParam(value = "pageNo",defaultValue = "0") Integer pageNo, @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        return new ResponseObject<>(phieuGiamGiaService.phanTrang(pageNo,pageSize));
    }

    @GetMapping("/detail/{id}")
    public ResponseObject<?> detail(@PathVariable Integer id){
        return new ResponseObject<>(phieuGiamGiaService.getPhieuGiamGiaById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseObject<?> deletePhieuGiamGia(@PathVariable("id") Integer id){
        phieuGiamGiaService.delete(id);
        return new ResponseObject<>(null,"Xoa thành công");
    }

    @PostMapping("/add")
    public ResponseObject<?> addPhieuGiamGia(@RequestBody PhieuGiamGiaRequesst phieuGiamGiaRequesst){
        phieuGiamGiaService.add(phieuGiamGiaRequesst);
        return new ResponseObject<>(null,"Thêm thành công");
    }

    @PutMapping("/update/{id}")
    public ResponseObject<?> updatePhieuGiamGia(@PathVariable Integer id,@RequestBody PhieuGiamGiaRequesst phieuGiamGiaRequesst){
        phieuGiamGiaService.update(id,phieuGiamGiaRequesst);
        return new ResponseObject<>(null,"Sửa thành công");
    }

    @GetMapping("/khach-hang/{idPhieu}")
    public ResponseObject<?> getKhachHangTheoPhieu(@PathVariable("idPhieu") Integer idPhieu) {
        return new ResponseObject<>(phieuGiamGiaService.getKhachHangTheoPhieu(idPhieu));
    }


    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(@PathVariable Integer id, @RequestParam Boolean trangThai) {
        phieuGiamGiaService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhập trạng thái thành công");
    }


    @GetMapping("/search")
    public ResponseObject<?> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "tuNgay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate tuNgay,
            @RequestParam(value = "denNgay", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate denNgay,
            @RequestParam(value = "kieu", required = false) Integer kieu,
            @RequestParam(value = "loaiGiamGia", required = false) Boolean loaiGiamGia,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai
    ) {
        List<PhieuGiamGiaRespone> result = phieuGiamGiaService.searchPhieuGiamGia(
                keyword, tuNgay, denNgay, kieu, loaiGiamGia, trangThai
        );
        return new ResponseObject<>(result);
    }


}
