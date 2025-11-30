package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.GiaoCaStartRequest;
import com.example.the_autumn.model.response.GiaoCaResponse;
import com.example.the_autumn.service.GiaoCaService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/giao-ca")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000"})
public class GiaoCaController {

    @Autowired
    private GiaoCaService giaoCaService;

    /**
     * Lấy danh sách giao ca của nhân viên đang đăng nhập
     */
    @GetMapping
    public ResponseEntity<?> getAll() {
        try {
            return ResponseEntity.ok(giaoCaService.getAll());
        } catch (IllegalStateException e) {
            // Lỗi chưa đăng nhập hoặc token sai
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }

    /**
     * Bắt đầu ca làm việc
     */
    @PostMapping("/start")
    public ResponseEntity<?> startShift(@Valid @RequestBody GiaoCaStartRequest request) {
        try {
            GiaoCaResponse response = giaoCaService.startShift(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Lỗi hệ thống: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    /**
     * Kết thúc ca làm việc
     */
    @PostMapping("/{id}/end")
    public ResponseEntity<?> endShift(
            @PathVariable("id") Integer id,
            @RequestBody(required = false) Map<String, Object> payload
    ) {
        try {
            String ghiChu = null;
            if (payload != null && payload.containsKey("ghiChu")) {
                ghiChu = payload.get("ghiChu").toString();
            }

            GiaoCaResponse response = giaoCaService.endShift(id, ghiChu);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body(Map.of(
                    "error", "Lỗi hệ thống: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShift(@PathVariable("id") Integer id) {
        try {
            giaoCaService.deleteById(id);
            return ResponseEntity.ok(Map.of(
                    "message", "Xóa giao ca thành công",
                    "id", id,
                    "timestamp", System.currentTimeMillis()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Lỗi: " + e.getMessage(),
                    "timestamp", System.currentTimeMillis()
            ));
        }
    }
}