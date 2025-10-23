package com.example.the_autumn.service;

import com.example.the_autumn.entity.KieuDang;
import com.example.the_autumn.model.request.KieuDangRequest;
import com.example.the_autumn.model.response.KieuDangResponse;
import com.example.the_autumn.repository.KieuDangRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KieuDangService {

    @Autowired
    private KieuDangRepository kieuDangRepo;

    public List<KieuDangResponse> findAll() {
        return kieuDangRepo.findAll()
                .stream()
                .map(KieuDangResponse::new)
                .toList();
    }

    public void add(KieuDangRequest request) {
        KieuDang kieuDang = MapperUtils.map(request, KieuDang.class);
        kieuDang.setTrangThai(true);
        kieuDangRepo.save(kieuDang);
    }

    public List<KieuDangResponse> findByName(String name) {
        return kieuDangRepo.findByTenKieuDangContainingIgnoreCase(name)
                .stream()
                .map(KieuDangResponse::new)
                .collect(Collectors.toList());
    }

    public List<KieuDangResponse> findByName2(String name) {
        return kieuDangRepo.findByNameContaining(name)
                .stream()
                .map(KieuDangResponse::new)
                .collect(Collectors.toList());
    }
}
