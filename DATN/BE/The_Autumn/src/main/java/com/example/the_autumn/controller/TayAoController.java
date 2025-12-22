package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.TayAoRequest;
import com.example.the_autumn.model.response.TayAoResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.TayAoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/tay-ao")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class TayAoController {

    @Autowired
    private TayAoService taService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(taService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody TayAoRequest tayAoRequest){
        taService.add(tayAoRequest);
        return new ResponseObject<>(null, "Thêm tay áo thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterTayAo(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maTayAo,
            @RequestParam(required = false) String tenTayAo,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            PageableObject<TayAoResponse> result = taService.filterTayAoWithPaging(
                    pageNo, pageSize,
                    searchText, maTayAo, tenTayAo, ngayTao, trangThai
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

        taService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }

}
