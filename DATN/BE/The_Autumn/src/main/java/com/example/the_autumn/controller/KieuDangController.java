package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.KieuDangRequest;
import com.example.the_autumn.model.response.KieuDangResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.KieuDangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/kieu-dang")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class KieuDangController {

    @Autowired
    private KieuDangService kdService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(kdService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody KieuDangRequest kieuDangRequest){
        kdService.add(kieuDangRequest);
        return new ResponseObject<>(null, "Thêm kiểu dáng thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterKieuDang(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maKieuDang,
            @RequestParam(required = false) String tenKieuDang,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            PageableObject<KieuDangResponse> result = kdService.filterKieuDangWithPaging(
                    pageNo, pageSize,
                    searchText, maKieuDang, tenKieuDang, ngayTao, trangThai
            );

            return new ResponseObject<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lọc hãng: " + e.getMessage());
        }
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(
            @PathVariable Integer id,
            @RequestParam Boolean trangThai) {

        kdService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }

}
