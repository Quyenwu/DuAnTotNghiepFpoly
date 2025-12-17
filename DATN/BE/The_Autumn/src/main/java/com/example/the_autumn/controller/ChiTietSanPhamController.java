package com.example.the_autumn.controller;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.model.request.AddVariantRequest;
import com.example.the_autumn.model.request.TaoBienTheRequest;
import com.example.the_autumn.model.request.UpdateChiTietSanPhamRequest;
import com.example.the_autumn.model.response.ChiTietSanPhamResponse;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
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
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chi-tiet-san-pham")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://172.20.10.2:5173"})
public class ChiTietSanPhamController {

    @Autowired
    private ChiTietSanPhamService chiTietSanPhamService;

    @Autowired
    private  ChiTietSanPhamRepository chiTietSanPhamRepository;



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

    @PostMapping("/{idChiTietSanPham}/tao-bien-the-gia-moi")
    public ResponseEntity<?> capNhatGia(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, Object> updates) {
        try {
            if (!updates.containsKey("donGia")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu trường donGia"
                ));
            }

            BigDecimal donGia = new BigDecimal(updates.get("donGia").toString());

        ChiTietSanPhamResponse bienTheMoi = chiTietSanPhamService.capNhatGiaVaTaoBienTheMoi(
                idChiTietSanPham,
                donGia
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "success", true,
                "message", "Đã tạo biến thể mới với giá mới",
                "data", bienTheMoi,
                "oldVariantId", idChiTietSanPham
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

    @PatchMapping("/{idChiTietSanPham}/gia")
    public ResponseEntity<?> capNhatGiaTrucTiep(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, Object> updates) {
        try {
            System.out.println("💰 PATCH CẬP NHẬT GIÁ TRỰC TIẾP - ID: " + idChiTietSanPham);

            if (!updates.containsKey("donGia")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu trường donGia"
                ));
            }

            BigDecimal donGia;
            try {
                donGia = new BigDecimal(updates.get("donGia").toString());
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Giá không hợp lệ. Vui lòng nhập số."
                ));
            }

            // Kiểm tra giá hợp lệ
            if (donGia.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Giá phải lớn hơn hoặc bằng 0"
                ));
            }

            // Gọi service để cập nhật giá
            chiTietSanPhamService.capNhatGiaBienThe(idChiTietSanPham, donGia);

            System.out.println("✅ Cập nhật giá thành công: " + donGia);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật giá thành công",
                    "data", Map.of(
                            "idChiTietSanPham", idChiTietSanPham,
                            "giaMoi", donGia
                    )
            ));

        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi cập nhật giá: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            System.err.println("❌ Lỗi hệ thống: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống: " + e.getMessage()
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

    @GetMapping
    public ResponseObject<?> getAllChucVu(){
        return new ResponseObject<>(chiTietSanPhamService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Integer id) {
        try {
            ChiTietSanPham chiTiet = chiTietSanPhamRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm với ID: " + id));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lấy thông tin thành công!",
                    "data", chiTiet
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(404).body(Map.of(
                    "success", false,
                    "message", e.getMessage(),
                    "data", null
            ));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage(),
                    "data", null
            ));
        }
    }

    @GetMapping("/san-pham/{sanPhamId}")
    public ResponseEntity<?> getBySanPhamId(@PathVariable Integer sanPhamId) {
        try {
            List<ChiTietSanPham> list = chiTietSanPhamRepository.findBySanPhamId(sanPhamId);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Lấy danh sách thành công!",
                    "data", list
            ));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "success", false,
                    "message", "Lỗi: " + e.getMessage(),
                    "data", null
            ));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateChiTietSanPham(
            @PathVariable Integer id,
            @Valid @RequestBody UpdateChiTietSanPhamRequest request,
            BindingResult bindingResult) {

        try {
            System.out.println("🔄 UPDATE CHI TIẾT SẢN PHẨM - ID: " + id);

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

            ChiTietSanPhamResponse updated = chiTietSanPhamService.updateChiTietSanPham(id, request);

            System.out.println("✅ Cập nhật biến thể thành công");

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật biến thể thành công",
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
                    "message", "Lỗi khi cập nhật biến thể: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/tao-bien-the-cho-san-pham")
    public ResponseEntity<?> taoBienTheChoSanPham(
            @Valid @RequestBody AddVariantRequest request,
            BindingResult bindingResult) {

        try {
            System.out.println("THÊM BIẾN THỂ CHO SẢN PHẨM CÓ SẴN");

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

            List<ChiTietSanPhamResponse> savedVariants = chiTietSanPhamService.taoBienTheChoSanPham(request);

            System.out.println("Đã thêm " + savedVariants.size() + " biến thể mới");

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Thêm biến thể thành công",
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
                    "message", "Lỗi khi thêm biến thể: " + e.getMessage()
            ));
        }
    }

    @PatchMapping("/{idChiTietSanPham}/mo-ta")
    public ResponseEntity<?> capNhatMoTa(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, Object> updates) {
        try {
            if (!updates.containsKey("moTa")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu trường moTa"
                ));
            }

            String moTa = updates.get("moTa").toString();
            chiTietSanPhamService.capNhatMoTaBienThe(idChiTietSanPham, moTa);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật mô tả thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/by-san-pham/{id}")
    public ResponseObject<?> getBySanPham(@PathVariable("id") Integer id) {
        List<ChiTietSanPhamResponse> list = chiTietSanPhamService.findBySanPhamId(id);
        return new ResponseObject<>(list, "Lấy chi tiết sản phẩm thành công");
    }

    @PutMapping("/giam-so-luong/{id}")
    @Transactional
    public ResponseObject<?> giamSoLuong(@PathVariable Integer id, @RequestParam Integer soLuong) {
        Optional<ChiTietSanPham> optional = chiTietSanPhamService.findById(id);
        if (optional.isEmpty()) {
            return ResponseObject.error("Không tìm thấy sản phẩm ID: " + id);
        }

        ChiTietSanPham ctsp = optional.get();
        if (ctsp.getSoLuongTon() < soLuong) {
            return ResponseObject.error("Không đủ hàng tồn trong kho");
        }

        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuong);
        chiTietSanPhamService.save(ctsp);
        return ResponseObject.success(ctsp, "Đã trừ " + soLuong + " sản phẩm khỏi kho");
    }

    @PutMapping("/tang-so-luong/{id}")
    @Transactional
    public ResponseObject<?> tangSoLuong(@PathVariable Integer id, @RequestParam Integer soLuong) {
        Optional<ChiTietSanPham> optional = chiTietSanPhamService.findById(id);
        if (optional.isEmpty()) {
            return ResponseObject.error("Không tìm thấy sản phẩm ID: " + id);
        }

        ChiTietSanPham ctsp = optional.get();
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() + soLuong);
        chiTietSanPhamService.save(ctsp);
        return ResponseObject.success(ctsp, "Đã cộng " + soLuong + " sản phẩm vào kho");
    }

    @PostMapping("/{idChiTietSanPham}/gia-moi")
    public ResponseEntity<?> taoBienTheVoiGiaMoi(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, Object> request) {
        try {
            System.out.println("🆕 TẠO BIẾN THỂ MỚI VỚI GIÁ MỚI - ID gốc: " + idChiTietSanPham);

            if (!request.containsKey("giaMoi")) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Thiếu trường giaMoi"
                ));
            }

            BigDecimal giaMoi = new BigDecimal(request.get("giaMoi").toString());

            ChiTietSanPhamResponse bienTheMoi = chiTietSanPhamService.taoBienTheVoiGiaMoi(
                    idChiTietSanPham,
                    giaMoi
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "success", true,
                    "message", "Tạo biến thể mới với giá mới thành công",
                    "data", bienTheMoi,
                    "note", "Biến thể gốc được giữ nguyên"
            ));

        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Giá không hợp lệ"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                    "success", false,
                    "message", "Lỗi khi tạo biến thể mới: " + e.getMessage()
            ));
        }
    }
}
