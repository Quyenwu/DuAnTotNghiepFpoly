package com.example.the_autumn.controller;

import com.example.the_autumn.dto.CaLamViecDTO;
import com.example.the_autumn.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ca-lam-viec")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174", "http://localhost:3000","http://172.20.10.2:5173"})
public class CaLamViecController {

    @Autowired
    private CaLamViecService service;
    @GetMapping
    public ResponseEntity<List<CaLamViecDTO>> getAll() {
        return ResponseEntity.ok(service.getAllCaLamViec());
    }
    @GetMapping("/active")
    public ResponseEntity<List<CaLamViecDTO>> getActive() {
        return ResponseEntity.ok(service.getCaLamViecActive());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CaLamViecDTO> getById(@PathVariable Integer id) {
        CaLamViecDTO dto = service.getCaLamViecById(id);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }
    @PostMapping
    public ResponseEntity<CaLamViecDTO> create(@RequestBody CaLamViecDTO dto) {
        return ResponseEntity.ok(service.createCaLamViec(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CaLamViecDTO> update(@PathVariable Integer id, @RequestBody CaLamViecDTO dto) {
        CaLamViecDTO updated = service.updateCaLamViec(id, dto);
        return updated != null ? ResponseEntity.ok(updated) : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Integer id) {
        return service.deleteCaLamViec(id) ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}
