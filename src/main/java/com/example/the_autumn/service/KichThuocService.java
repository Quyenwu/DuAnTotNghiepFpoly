package com.example.the_autumn.service;

import com.example.the_autumn.entity.KichThuoc;
import com.example.the_autumn.model.request.KichThuocRequest;
import com.example.the_autumn.model.response.KichThuocResponse;
import com.example.the_autumn.repository.KichThuocRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class KichThuocService {

    @Autowired
    private KichThuocRepository kichThuocRepo;

    public List<KichThuocResponse> findAll() {
        return kichThuocRepo.findAll()
                .stream()
                .map(KichThuocResponse::new)
                .toList();
    }

    public void add(KichThuocRequest request) {
        KichThuoc kt = MapperUtils.map(request, KichThuoc.class);
        kt.setTrangThai(true);
        kichThuocRepo.save(kt);
    }

    public List<KichThuocResponse> findByName(String name) {
        return kichThuocRepo.findByTenKichThuocContainingIgnoreCase(name)
                .stream()
                .map(KichThuocResponse::new)
                .collect(Collectors.toList());
    }

    public List<KichThuocResponse> findByName2(String name) {
        return kichThuocRepo.findByNameContaining(name)
                .stream()
                .map(KichThuocResponse::new)
                .collect(Collectors.toList());
    }
}
