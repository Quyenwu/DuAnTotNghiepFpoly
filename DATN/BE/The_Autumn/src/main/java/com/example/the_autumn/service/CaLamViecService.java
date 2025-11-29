package com.example.the_autumn.service;

import com.example.the_autumn.dto.CaLamViecDTO;
import com.example.the_autumn.entity.CaLamViec;
import com.example.the_autumn.repository.CaLamViecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CaLamViecService {

    @Autowired
    private CaLamViecRepository repository;

    public List<CaLamViecDTO> getAllCaLamViec() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<CaLamViecDTO> getCaLamViecActive() {
        return repository.findByTrangThai(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public CaLamViecDTO getCaLamViecById(Integer id) {
        return repository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    public CaLamViecDTO createCaLamViec(CaLamViecDTO dto) {
        CaLamViec entity = new CaLamViec();
        entity.setTenCa(dto.getTenCa());
        entity.setGioBatDau(LocalTime.parse(dto.getGioBatDau()));
        entity.setGioKetThuc(LocalTime.parse(dto.getGioKetThuc()));
        entity.setMoTa(dto.getMoTa());
        entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        entity.setNgayTao(LocalDateTime.now());

        return convertToDTO(repository.save(entity));
    }

    public CaLamViecDTO updateCaLamViec(Integer id, CaLamViecDTO dto) {
        return repository.findById(id).map(entity -> {
            entity.setTenCa(dto.getTenCa());
            entity.setGioBatDau(LocalTime.parse(dto.getGioBatDau()));
            entity.setGioKetThuc(LocalTime.parse(dto.getGioKetThuc()));
            entity.setMoTa(dto.getMoTa());
            entity.setTrangThai(dto.getTrangThai());
            entity.setNgaySua(LocalDateTime.now());
            return convertToDTO(repository.save(entity));
        }).orElse(null);
    }

    public boolean deleteCaLamViec(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private CaLamViecDTO convertToDTO(CaLamViec e) {
        return new CaLamViecDTO(
                e.getId(),
                e.getMaCa(),
                e.getTenCa(),
                e.getGioBatDau().toString(),
                e.getGioKetThuc().toString(),
                e.getMoTa(),
                e.getTrangThai()
        );
    }
}
