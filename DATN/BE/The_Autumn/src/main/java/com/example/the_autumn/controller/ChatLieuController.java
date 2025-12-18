package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ChatLieuRequest;
import com.example.the_autumn.model.response.ChatLieuResponse;
import com.example.the_autumn.model.response.MauSacResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.ChatLieuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/chat-lieu")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class ChatLieuController {

    @Autowired
    private ChatLieuService clService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(clService.findAll());}

    @GetMapping("detail/{id}")
    public ResponseObject<?> detail(@PathVariable("id") Integer id){
        return new ResponseObject<>(clService.detail(id));
    }

    @PutMapping("update/{id}")
    public ResponseObject<?> updateChatLieu(@PathVariable("id") Integer id, @RequestBody ChatLieuRequest chatLieuRequest){
        clService.update(id, chatLieuRequest);
        return new ResponseObject<>(null, "update chất liệu thành công");
    }

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody ChatLieuRequest chatLieuRq){
        clService.add(chatLieuRq);
        return new ResponseObject<>(null, "Thêm chất liệu thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterChatLieu(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maChatLieu,
            @RequestParam(required = false) String tenChatLieu,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            PageableObject<ChatLieuResponse> result = clService.filterChatLieuWithPaging(
                    pageNo, pageSize,
                    searchText, maChatLieu, tenChatLieu, ngayTao, trangThai
            );

            System.out.println("✅ BE trả về trang " + pageNo +
                    " với " + result.getData().size() + " màu sắc, " +
                    "tổng: " + result.getTotalElements() + " màu");

            return new ResponseObject<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lọc màu sắc: " + e.getMessage());
        }
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(
            @PathVariable Integer id,
            @RequestParam Boolean trangThai) {

        clService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }
}
