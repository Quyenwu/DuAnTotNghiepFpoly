package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.NhaSanXuatRequest;
import com.example.the_autumn.model.response.NhaSanXuatResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.model.response.XuatXuResponse;
import com.example.the_autumn.service.NhaSanXuatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/nha-san-xuat")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class NhaSanXuatController {

    @Autowired
    private NhaSanXuatService nsxService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(nsxService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody NhaSanXuatRequest nhaSanXuatRequest){
        nsxService.add(nhaSanXuatRequest);
        return new ResponseObject<>(null, "Thêm nhà sản xuất thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterNhaSanXuat(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maNhaSanXuat,
            @RequestParam(required = false) String tenNhaSanXuat,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            PageableObject<NhaSanXuatResponse> result = nsxService.filterNhaSanXuatWithPaging(
                    pageNo, pageSize,
                    searchText, maNhaSanXuat, tenNhaSanXuat, ngayTao, trangThai
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

        nsxService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }
}
