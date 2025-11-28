package com.example.the_autumn.controller;

import com.example.the_autumn.model.response.DanhMucResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.model.response.SanPhamResponse;
import com.example.the_autumn.service.ChiTietSanPhamService;
import com.example.the_autumn.service.SanPhamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.the_autumn.model.request.UpdateSanPhamRequest;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/san-pham")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class SanPhamController {

    @Autowired
    private ChiTietSanPhamService ctspService;

    @Autowired
    private SanPhamService spService;

    @GetMapping
    public ResponseObject<?> hienThiDuLieu() {
        return new ResponseObject<>(spService.findAll());
    }

    @GetMapping("playlist/paging")
    public ResponseObject<?> phanTrangProduct(
            @RequestParam(value = "pageNo1", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize1", defaultValue = "5") Integer pageSize) {

        System.out.println("📄 Controller: phanTrangProduct - pageNo=" + pageNo + ", pageSize=" + pageSize);

        PageableObject<SanPhamResponse> result = spService.phanTrang(pageNo, pageSize);

        System.out.println("✅ Controller trả về: " + result.getData().size() + " sản phẩm, tổng trang: " + result.getTotalPage());

        return new ResponseObject<>(result);
    }

    @GetMapping("/filter")
    public ResponseObject<?> filterSanPham(
            @RequestParam(value = "pageNo1", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize1", defaultValue = "5") Integer pageSize,
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) String maSanPham,
            @RequestParam(required = false) String tenSanPham,
            @RequestParam(required = false) String tenNhaSanXuat,
            @RequestParam(required = false) String tenChatLieu,
            @RequestParam(required = false) String tenKieuDang,
            @RequestParam(required = false) String tenXuatXu,
            @RequestParam(required = false) Boolean trangThai) {

        try {
            System.out.println("🎯 BE nhận filter request với phân trang:");
            System.out.println("- pageNo: " + pageNo + ", pageSize: " + pageSize);
            System.out.println("- searchText: " + searchText);
            System.out.println("- maSanPham: " + maSanPham);
            System.out.println("- tenSanPham: " + tenSanPham);
            System.out.println("- tenNhaSanXuat: " + tenNhaSanXuat);
            System.out.println("- tenChatLieu: " + tenChatLieu);
            System.out.println("- tenKieuDang: " + tenKieuDang);
            System.out.println("- tenXuatXu: " + tenXuatXu);
            System.out.println("- trangThai: " + trangThai);

            PageableObject<SanPhamResponse> result = spService.filterSanPhamWithPaging(
                    pageNo, pageSize,
                    searchText, maSanPham, tenSanPham, tenNhaSanXuat,
                    tenChatLieu, tenKieuDang, tenXuatXu, null, trangThai
            );

            System.out.println("✅ BE trả về trang " + pageNo + " với " + result.getData().size() + " sản phẩm, tổng trang: " + result.getTotalPage());
            return new ResponseObject<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lọc sản phẩm: " + e.getMessage());
        }
    }

    @GetMapping("/{idSanPham}/detail")
    public ResponseEntity<?> getSanPhamDetail(@PathVariable Integer idSanPham) {
        try {
            System.out.println("🔍 GET SAN PHAM DETAIL - ID: " + idSanPham);

            SanPhamResponse sanPhamDetail = spService.getSanPhamDetailWithVariants(idSanPham);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lấy chi tiết sản phẩm thành công",
                    "data", sanPhamDetail
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/update-trang-thai/{id}")
    public ResponseObject<?> updateTrangThai(@PathVariable Integer id, @RequestParam Boolean trangThai) {
        spService.updateTrangThai(id, trangThai);
        ctspService.updateTrangThaiByIdSanPham(id, trangThai);
        return new ResponseObject<>(null, "Cập nhập trạng thái thành công");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateSanPham(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateSanPhamRequest request,
            BindingResult bindingResult) {

        try {
            System.out.println("🔄 UPDATE SẢN PHẨM - ID: " + id);

            if (bindingResult.hasErrors()) {
                Map<String, String> errors = bindingResult.getFieldErrors().stream()
                        .collect(Collectors.toMap(
                                error -> error.getField(),
                                error -> error.getDefaultMessage()
                        ));
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Dữ liệu không hợp lệ",
                        "errors", errors
                ));
            }

            SanPhamResponse updated = spService.updateSanPham(id, request);

            System.out.println("✅ Cập nhật sản phẩm thành công");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật sản phẩm thành công",
                    "data", updated
            ));

        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi cập nhật sản phẩm: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/{id}/edit")
    public ResponseEntity<?> getSanPhamForEdit(@PathVariable Integer id) {
        try {
            System.out.println("📝 GET SAN PHAM FOR EDIT - ID: " + id);

            SanPhamResponse sanPham = spService.getSanPhamDetailWithVariants(id);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lấy thông tin sản phẩm thành công",
                    "data", sanPham
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi lấy thông tin sản phẩm: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/danh-muc")
    public ResponseObject<?> getSanPhamByDanhMuc(
            @RequestParam(value = "pageNo1", defaultValue = "0") Integer pageNo,
            @RequestParam(value = "pageSize1", defaultValue = "12") Integer pageSize,
            @RequestParam(required = false) String danhMuc) {

        try {

            PageableObject<DanhMucResponse> result;

            if (danhMuc != null && !danhMuc.isEmpty()) {
                result = spService.filterByDanhMucWithPaging(pageNo, pageSize, danhMuc);
            } else {
                result = spService.phanTrangDanhMuc(pageNo, pageSize);
            }

            return new ResponseObject<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lọc theo danh mục: " + e.getMessage());
        }
    }

    @GetMapping("/danh-muc/all")
    public ResponseObject<?> getAllDanhMuc() {
        try {
            List<String> danhMucList = spService.getAllDanhMuc();
            return new ResponseObject<>(danhMucList);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lấy danh sách danh mục: " + e.getMessage());
        }
    }

    @GetMapping("/danh-muc/{danhMuc}")
    public ResponseObject<?> getSanPhamByDanhMucNoPaging(@PathVariable String danhMuc) {
        try {

            List<DanhMucResponse> result = spService.filterByDanhMuc(danhMuc);

            System.out.println("✅ BE trả về " + result.size() + " sản phẩm cho danh mục: " + danhMuc);
            return new ResponseObject<>(result);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseObject<>("500", "Lỗi khi lọc theo danh mục: " + e.getMessage());
        }
    }
}