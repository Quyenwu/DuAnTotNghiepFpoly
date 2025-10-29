package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.model.response.PhieuGiamGiaRespone;
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

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nhan-vien")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class NhanVienController {

    @Autowired
    private NhanVienService nhanVienService;

    @GetMapping
    public ResponseObject<?> getAllNhanVien() {
        return new ResponseObject<>(nhanVienService.getAllNhanVien());
    }


    @GetMapping("/detail/{id}")
    public ResponseObject<?> detail(@PathVariable Integer id) {
        return new ResponseObject<>(nhanVienService.getNhanVienById(id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseObject<?> deleteNhanVien(@PathVariable("id") Integer id) {
        nhanVienService.delete(id);
        return new ResponseObject<>(null, "Xoa thành công");
    }

    @PostMapping("/add")
    public ResponseObject<?> addNhanVien(@RequestBody NhanVienRequest nhanVienReQuest) {
        nhanVienService.add(nhanVienReQuest);
        return new ResponseObject<>(null, "Thêm thành công");
    }

    @PutMapping("/update/{id}")
    public ResponseObject<?> updateNhanVien(@PathVariable Integer id, @RequestBody NhanVienRequest nhanVienReQuest) {
        nhanVienService.update(id, nhanVienReQuest);
        return new ResponseObject<>(null, "Sửa thành công");
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(@PathVariable Integer id, @RequestParam Boolean trangThai) {
        nhanVienService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhập trạng thái thành công");
    }

    @GetMapping("/search")
    public ResponseObject<?> search(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "gioiTinh", required = false) Boolean gioiTinh,
            @RequestParam(value = "chucVu", required = false) String chucVu,
            @RequestParam(value = "trangThai", required = false) Boolean trangThai
    ) {
        List<NhanVienResponse> result = nhanVienService.searchNhanVien(
                keyword, gioiTinh, chucVu, trangThai
        );
        return new ResponseObject<>(result);
    }
    @GetMapping("/check-email")
    public ResponseObject<?> checkEmail(@RequestParam("email") String email) {
        boolean exists = nhanVienService.checkEmailExists(email);
        return new ResponseObject<>(Map.of("exists", exists));
    }
    @GetMapping("/check-sdt")
    public ResponseObject<?> checkSdt(@RequestParam("sdt") String sdt) {
        boolean exists = nhanVienService.checkSdtExists(sdt);
        return new ResponseObject<>(Map.of("exists", exists));
    }

}
