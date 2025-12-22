package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.CoAoRequest;
import com.example.the_autumn.model.response.CoAoResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.CoAoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/co-ao")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class CoAoController {

    @Autowired
    private CoAoService caService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(caService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody CoAoRequest coAoRequest){
        caService.add(coAoRequest);
        return new ResponseObject<>(null, "Thêm cổ áo thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterCoAo(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maCoAo,
            @RequestParam(required = false) String tenCoAo,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            PageableObject<CoAoResponse> result = caService.filterCoAoWithPaging(
                    pageNo, pageSize,
                    searchText, maCoAo, tenCoAo, ngayTao, trangThai
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

        caService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }

}
