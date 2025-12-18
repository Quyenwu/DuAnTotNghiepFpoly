package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.MauSacRequest;
import com.example.the_autumn.model.response.MauSacResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.MauSacService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@RestController
@RequestMapping("/api/mau-sac")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class MauSacController {

    @Autowired
    private MauSacService msService;

    @GetMapping("playlist")
    public ResponseObject<?> hienThiDuLieu(){return new ResponseObject<>(msService.findAll());}

    @PostMapping("add")
    public ResponseObject<?> add(@RequestBody MauSacRequest mauSacRequest){
        msService.add(mauSacRequest);
        return new ResponseObject<>(null, "Thêm màu sắc thành công");
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterMauSac(
            @RequestParam(value = "pageNo", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maMauSac,
            @RequestParam(required = false) String tenMauSac,
            @RequestParam(required = false) Date ngayTao,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            System.out.println("🎯 BE nhận filter request màu sắc với phân trang:");
            System.out.println("- pageNo: " + pageNo + ", pageSize: " + pageSize);
            System.out.println("- searchText: " + searchText);
            System.out.println("- maMauSac: " + maMauSac);
            System.out.println("- tenMauSac: " + tenMauSac);
            System.out.println("- ngayTao: " + ngayTao);
            System.out.println("- trangThai: " + trangThai);

            PageableObject<MauSacResponse> result = msService.filterMauSacWithPaging(
                    pageNo, pageSize,
                    searchText, maMauSac, tenMauSac, ngayTao, trangThai
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

        System.out.println("🔄 Update trạng thái màu sắc ID=" + id + " -> " + trangThai);

        msService.updateTrangThai(id, trangThai);
        return new ResponseObject<>(null, "Cập nhật trạng thái thành công");
    }

}
