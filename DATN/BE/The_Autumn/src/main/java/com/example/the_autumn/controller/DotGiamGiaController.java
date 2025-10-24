package com.example.the_autumn.controller;

import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import com.example.the_autumn.model.response.DotGiamGiaDTO;
import com.example.the_autumn.service.DotGiamGiaService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dot-giam-gia")
@CrossOrigin(origins = "http://localhost:5173")
public class DotGiamGiaController {

    private final DotGiamGiaService dotGiamGiaService;

    public DotGiamGiaController(DotGiamGiaService dotGiamGiaService) {
        this.dotGiamGiaService = dotGiamGiaService;
    }

    // 🔹 Lấy danh sách tất cả đợt giảm giá
    @GetMapping
    public List<DotGiamGiaDTO> getAll() {
        return dotGiamGiaService.list();
    }

    // 🔹 Lấy theo ID
    @GetMapping("/{id}")
    public ResponseEntity<DotGiamGia> getById(@PathVariable Integer id) {
        Optional<DotGiamGia> found = dotGiamGiaService.findById(id);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Lấy theo mã giảm giá
    @GetMapping("/ma/{ma}")
    public ResponseEntity<DotGiamGia> getByMa(@PathVariable String ma) {
        Optional<DotGiamGia> found = dotGiamGiaService.findByMa(ma);
        return found.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 🔹 Lấy danh sách chi tiết theo đợt giảm giá ID
    @GetMapping("/{id}/chi-tiet")
    public ResponseEntity<List<DotGiamGiaChiTiet>> getDetails(@PathVariable Integer id) {
        Optional<DotGiamGia> found = dotGiamGiaService.findById(id);
        if (found.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dotGiamGiaService.findDetailsByDot(found.get()));
    }

    // 🔹 Tạo mới
    @PostMapping
    public ResponseEntity<DotGiamGia> create(@RequestBody DotGiamGia body) {
        DotGiamGia created = dotGiamGiaService.save(body);
        return ResponseEntity.created(URI.create("/api/dot-giam-gia/" + created.getId()))
                .body(created);
    }

    // 🔹 Cập nhật theo ID
    @PutMapping("/{id}")
    public ResponseEntity<DotGiamGia> update(@PathVariable Integer id, @RequestBody DotGiamGia body) {
        Optional<DotGiamGia> existing = dotGiamGiaService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        body.setId(id);
        DotGiamGia updated = dotGiamGiaService.save(body);
        return ResponseEntity.ok(updated);
    }

    // 🔹 Xóa theo ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        Optional<DotGiamGia> existing = dotGiamGiaService.findById(id);
        if (existing.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        dotGiamGiaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
