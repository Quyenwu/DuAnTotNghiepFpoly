package com.example.the_autumn.controller;

import com.example.the_autumn.dto.GiaoCaDTO;
import com.example.the_autumn.model.request.GiaoCaStartRequest;
import com.example.the_autumn.model.response.GiaoCaResponse;
import com.example.the_autumn.service.GiaoCaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/giao-ca")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class GiaoCaController {

    @Autowired
    private GiaoCaService giaoCaService;

    @GetMapping
    public ResponseEntity<List<GiaoCaResponse>> getAll() {
        return ResponseEntity.ok(giaoCaService.getAll());
    }
    @PostMapping("/start")
    public ResponseEntity<?> startShift(@Valid @RequestBody GiaoCaStartRequest request) {
        try {
            GiaoCaResponse dto = giaoCaService.startShift(request);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @PostMapping("/{id}/end")
    public ResponseEntity<?> endShift(@Valid
                                      @PathVariable("id") Integer id,
                                      @RequestBody(required = false) Map<String, Object> payload
    ) {
        try {
            String ghiChu = null;
            if (payload != null && payload.get("ghiChu") != null) {
                ghiChu = payload.get("ghiChu").toString();
            }

            GiaoCaResponse dto = giaoCaService.endShift(id, ghiChu);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable("id") Integer id) {
        try {
            giaoCaService.deleteById(id);
            return ResponseEntity.ok("Xóa giao ca thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }
}
