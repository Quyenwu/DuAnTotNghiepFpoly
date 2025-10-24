package com.example.the_autumn.controller;


import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import com.example.the_autumn.model.response.ChiTietSanPhamDTO;
import com.example.the_autumn.model.response.DotGiamGiaDTO;
import com.example.the_autumn.model.response.DotGiamGiaRequest;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import com.example.the_autumn.service.DotGiamGiaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dot-giam-gia-add")
@CrossOrigin(origins = "http://localhost:5173")
public class DotGiamGiaPageController {

@Autowired
private DotGiamGiaService service;

    @Autowired
    private ChiTietSanPhamRepository ctspRepo;

    // === Danh sách đợt giảm giá phân trang ===
    @GetMapping
    public ResponseEntity<Page<DotGiamGiaDTO>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<DotGiamGiaDTO> result = service.findAllDTO(pageable);
        return ResponseEntity.ok(result);
    }

    // === Tạo mới đợt giảm giá + chi tiết ===
    @PostMapping
    public ResponseEntity<DotGiamGia> create(@RequestBody DotGiamGiaRequest request) {
        // Nếu ngày rỗng thì set null
        if (request.getDotGiamGia().getNgayBatDau() != null && request.getDotGiamGia().getNgayBatDau().toString().isEmpty()) {
            request.getDotGiamGia().setNgayBatDau(null);
        }
        if (request.getDotGiamGia().getNgayKetThuc() != null && request.getDotGiamGia().getNgayKetThuc().toString().isEmpty()) {
            request.getDotGiamGia().setNgayKetThuc(null);
        }

        // Nếu trangThai null → false
        if (request.getDotGiamGia().getTrangThai() == null) {
            request.getDotGiamGia().setTrangThai(false);
        }

        DotGiamGia saved = service.saveWithDetails(request.getDotGiamGia(), request.getCtspIds());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/form-data")
    public ResponseEntity<?> getFormData(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer dotId) {

        Pageable pageable = PageRequest.of(page, size);

        // Lấy danh sách sản phẩm đã map sang DTO qua service
        Page<ChiTietSanPhamDTO> productsDto = service.findAllProductDTO(q, pageable);

        // Nếu muốn có thể đánh dấu các sản phẩm đã có trong dotId
        if (dotId != null) {
            DotGiamGia dot = service.findById(dotId).orElse(null);
            if (dot == null) return ResponseEntity.badRequest().body("Đợt giảm giá không tồn tại");

            List<Integer> selectedIds = service.findDetailsByDot(dot).stream()
                    .map(d -> d.getChiTietSanPham().getId())
                    .collect(Collectors.toList());

            // Trả về DTO kèm danh sách sản phẩm đã chọn
            return ResponseEntity.ok(new FormDataResponse(productsDto, selectedIds));
        }

        return ResponseEntity.ok(productsDto);
    }
    public static class FormDataResponse {
        private Page<ChiTietSanPhamDTO> products;
        private List<Integer> selectedIds;

        public FormDataResponse(Page<ChiTietSanPhamDTO> products, List<Integer> selectedIds) {
            this.products = products;
            this.selectedIds = selectedIds;
        }

        public Page<ChiTietSanPhamDTO> getProducts() { return products; }
        public List<Integer> getSelectedIds() { return selectedIds; }
    }

    // === Cập nhật đợt giảm giá + chi tiết ===
    @PutMapping("/{id}")
    public ResponseEntity<DotGiamGia> update(
            @PathVariable Integer id,
            @RequestBody DotGiamGiaRequest request
    ) {
        return service.findById(id).map(existing -> {
            DotGiamGia updated = request.getDotGiamGia();
            updated.setId(existing.getId());
            updated.setNgayTao(existing.getNgayTao());

            // Xử lý ngày rỗng và trangThai null
            if (updated.getNgayBatDau() != null && updated.getNgayBatDau().toString().isEmpty()) updated.setNgayBatDau(null);
            if (updated.getNgayKetThuc() != null && updated.getNgayKetThuc().toString().isEmpty()) updated.setNgayKetThuc(null);
            if (updated.getTrangThai() == null) updated.setTrangThai(false);
            DotGiamGia saved = service.saveWithDetails(updated, request.getCtspIds());
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }


    // === Xóa đợt giảm giá ===
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        try {
            service.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(400).build();
        }
    }

    // === Kiểm tra sản phẩm đã có trong đợt giảm giá nào chưa ===
    @GetMapping("/check-product/{ctspId}")
    public ResponseEntity<Integer> countActiveSales(@PathVariable Integer ctspId) {
        ChiTietSanPham ctsp = ctspRepo.findById(ctspId).orElse(null);
        if (ctsp == null) return ResponseEntity.ok(0);

        List<DotGiamGiaChiTiet> details = service.findDetailsByDot(null);
        int count = (int) details.stream()
                .filter(d -> d.getChiTietSanPham().getId().equals(ctspId))
                .filter(d -> d.getDotGiamGia().getTrangThai() != null && d.getDotGiamGia().getTrangThai())
                .count();
        return ResponseEntity.ok(count);
    }

    // === Lấy chi tiết đợt giảm giá theo ID (nếu cần) ===
    @GetMapping("/{id}")
    public ResponseEntity<DotGiamGia> getById(@PathVariable Integer id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}