package com.example.the_autumn.controller;

import com.example.the_autumn.dto.PhanCaDTO;
import com.example.the_autumn.service.PhanCaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/phan-ca")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000","http://172.20.10.2:5173"})
public class PhanCaController {
    @Autowired
    private PhanCaService service;
    @GetMapping
    public ResponseEntity<List<PhanCaDTO>> getAll() {
        return ResponseEntity.ok(service.getAllPhanCa());
    }
    @GetMapping("/nhan-vien/{idNhanVien}")
    public ResponseEntity<List<PhanCaDTO>> getByNhanVien(@PathVariable Integer idNhanVien) {
        return ResponseEntity.ok(service.getPhanCaByNhanVien(idNhanVien));
    }
    @GetMapping("/date/{date}")
    public ResponseEntity<List<PhanCaDTO>> getByDate(@PathVariable String date) {
        return ResponseEntity.ok(service.getPhanCaByDate(LocalDate.parse(date)));
    }
    @GetMapping("/nhan-vien/{idNhanVien}/date-range")
    public ResponseEntity<List<PhanCaDTO>> getByDateRange(
            @PathVariable Integer idNhanVien,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(
                service.getPhanCaByNhanVienAndDateRange(
                        idNhanVien,
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate)
                )
        );
    }
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody PhanCaDTO dto) {
        PhanCaDTO created = service.createPhanCa(dto);
        if (created != null) {
            return ResponseEntity.ok(created);
        }
        // 👇 Nếu service trả null (trùng ca hoặc lỗi logic) thì trả 409 + message
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(Map.of("message", "Ca này trong ngày này đã được phân cho nhân viên khác!"));
    }
    @PutMapping("/{id}")
    public ResponseEntity<PhanCaDTO> update(@Valid @PathVariable Integer id, @RequestBody PhanCaDTO dto) {
        PhanCaDTO updated = service.updatePhanCa(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return service.deletePhanCa(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
