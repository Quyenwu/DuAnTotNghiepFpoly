package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.NhanVienService;
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
@RequestMapping("/api/nhan-vien")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping
    public ResponseObject<?> getAllNhanVien(){
        return new ResponseObject<>(nhanVienService.getAllNhanVien());
    }

    @GetMapping("/phan-trang")
    public ResponseObject<?> phanTrang(@RequestParam(value = "pageNo",defaultValue = "0") Integer pageNo, @RequestParam(value = "pageSize",defaultValue = "5") Integer pageSize){
        return new ResponseObject<>(nhanVienService.phanTrang(pageNo,pageSize));
    }

    @GetMapping("/detail/{id}")
    public ResponseObject<?> detail(@PathVariable Integer id){
        return new ResponseObject<>(nhanVienService.getNhanVienById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseObject<?> deleteNhanVien(@PathVariable("id") Integer id){
        nhanVienService.delete(id);
        return new ResponseObject<>(null,"Xoa thành công");
    }

    @PostMapping("/add")
    public ResponseObject<?> addNhanVien(@RequestBody NhanVienRequest nhanVienReQuest){
        nhanVienService.add(nhanVienReQuest);
        return new ResponseObject<>(null,"Thêm thành công");
    }

    @PutMapping("/update/{id}")
    public ResponseObject<?> updateNhanVien(@PathVariable Integer id,@RequestBody NhanVienRequest nhanVienReQuest){
        nhanVienService.update(id,nhanVienReQuest);
        return new ResponseObject<>(null,"Sửa thành công");
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai( @PathVariable Integer id, @RequestParam Boolean trangThai){
        nhanVienService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null,"Cập nhập trạng thái thành công");
    }


}
