package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.XuatXuRequest;
import com.example.the_autumn.model.response.ChatLieuResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.model.response.XuatXuResponse;
import com.example.the_autumn.service.XuatXuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/xuat-xu")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class XuatXuController {

    @Autowired
    private XuatXuService xxService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(xxService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody XuatXuRequest xuatXuRequest){
        xxService.add(xuatXuRequest);
        return new ResponseObject<>(null, "Thêm xuất xứ thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterXuatXu(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maXuatXu,
            @RequestParam(required = false) String tenXuatXu,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            PageableObject<XuatXuResponse> result = xxService.filterXuatXuWithPaging(
                    pageNo, pageSize,
                    searchText, maXuatXu, tenXuatXu, ngayTao, trangThai
            );

            return new ResponseObject<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lọc xuất xứ: " + e.getMessage());
        }
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(
            @PathVariable Integer id,
            @RequestParam Boolean trangThai) {

        xxService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }

}