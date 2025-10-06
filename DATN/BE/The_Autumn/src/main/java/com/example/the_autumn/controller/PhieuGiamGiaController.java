package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.PhieuGiamGiaRequesst;
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

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/phieu-giam-gia")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
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

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai( @PathVariable Integer id, @RequestParam Boolean trangThai){
        phieuGiamGiaService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null,"Cập nhập trạng thái thành công");
    }
    @GetMapping("/search-all")
    public ResponseObject<?> searchAllPhieuGiamGia(
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword
    ) {
        return new ResponseObject<>(phieuGiamGiaService.searchAllPhieuGiamGia(keyword));
    }

    @GetMapping("/search-by-date")
    public ResponseObject<?> searchByDateRange(
            @RequestParam("ngayBatDau") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date ngayBatDau,
            @RequestParam("ngayKetThuc") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date ngayKetThuc
    ) {
        return new ResponseObject<>(phieuGiamGiaService.searchTheoNgay(ngayBatDau, ngayKetThuc));
    }

    @GetMapping("/khach-hang/{id}")
    public ResponseObject<?> getKhachHangByPggId(@PathVariable Integer id) {
        List<KhachHang> khachHangs = phieuGiamGiaService.getKhachHangByPhieuGiamGiaId(id);
        return new ResponseObject<>(khachHangs);
    }

    @GetMapping("/khach-hang-id/{id}")
    public ResponseObject<?> getKhachHangIdsByPggId(@PathVariable Integer id) {
        List<Integer> khachHangIds = phieuGiamGiaService.getKhachHangIdsByPhieuGiamGiaId(id);
        return new ResponseObject<>(khachHangIds);
    }

}
