package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.TaoBienTheRequest;
import com.example.the_autumn.model.response.ChiTietSanPhamResponse;
import com.example.the_autumn.service.ChiTietSanPhamService;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class ChiTietSanPhamController {

    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    @Transactional
    @PostMapping("/tao-bien-the")
    public ResponseEntity<?> taoBienThe(
            @Valid @RequestBody TaoBienTheRequest request,
            BindingResult bindingResult) {

        try {
            System.out.println("🎯 TẠO SẢN PHẨM HOÀN CHỈNH + BIẾN THỂ");

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

            List<ChiTietSanPhamResponse> savedVariants = chiTietSanPhamService.taoBienThe(request);

            System.out.println("✅ Đã tạo " + savedVariants.size() + " biến thể");

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Tạo sản phẩm và biến thể thành công",
                    "data", savedVariants,
                    "total", savedVariants.size()
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi tạo biến thể: " + e.getMessage()
            ));
        }
    }

    @DeleteMapping("/{idChiTietSanPham}")
    public ResponseEntity<?> xoaBienThe(@PathVariable Integer idChiTietSanPham) {
        try {
            chiTietSanPhamService.xoaBienThe(idChiTietSanPham);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Xóa biến thể thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{idChiTietSanPham}/gia")
    public ResponseEntity<?> capNhatGia(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, Object> updates) {
        try {
            // ✅ Validate key tồn tại
            if (!updates.containsKey("donGia")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu trường donGia"
                ));
            }

            BigDecimal donGia = new BigDecimal(updates.get("donGia").toString());
            chiTietSanPhamService.capNhatGiaBienThe(idChiTietSanPham, donGia);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật giá thành công"
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Giá không hợp lệ"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PatchMapping("/{idChiTietSanPham}/so-luong")
    public ResponseEntity<?> capNhatSoLuong(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, Object> updates) {
        try {
            if (!updates.containsKey("soLuong")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu trường soLuong"
                ));
            }

            Integer soLuong = Integer.parseInt(updates.get("soLuong").toString());
            chiTietSanPhamService.capNhatSoLuongBienThe(idChiTietSanPham, soLuong);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật số lượng thành công"
            ));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Số lượng không hợp lệ"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @PostMapping("/preview-bien-the")
    public ResponseEntity<?> previewBienThe(
            @Valid @RequestBody TaoBienTheRequest request,
            BindingResult bindingResult) {

        try {
            System.out.println("🔍 PREVIEW TẠO SẢN PHẨM + BIẾN THỂ");

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

            Map<String, Object> previewInfo = chiTietSanPhamService.previewBienThe(request);

            System.out.println("✅ Đã tính toán preview: " + previewInfo.get("totalVariants") + " biến thể");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Preview tạo sản phẩm thành công",
                    "data", previewInfo,
                    "preview", true
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi preview biến thể: " + e.getMessage()
            ));
        }
    }
}
