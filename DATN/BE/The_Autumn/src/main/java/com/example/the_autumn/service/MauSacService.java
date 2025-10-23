package com.example.the_autumn.service;

import com.example.the_autumn.entity.MauSac;
import com.example.the_autumn.model.request.MauSacRequest;
import com.example.the_autumn.model.response.MauSacResponse;
import com.example.the_autumn.repository.MauSacRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MauSacService {

    @Autowired
    private MauSacRepository mauSacRepo;

    public List<MauSacResponse> findAll() {
        return mauSacRepo.findAll()
                .stream()
                .map(MauSacResponse::new)
                .toList();
    }

    public void add(MauSacRequest request) {
        MauSac ms = MapperUtils.map(request, MauSac.class);
        ms.setTrangThai(true);
        mauSacRepo.save(ms);
    }

    public List<MauSacResponse> findByName(String name) {
        return mauSacRepo.findByTenMauSacContainingIgnoreCase(name)
                .stream()
                .map(MauSacResponse::new)
                .collect(Collectors.toList());
    }

    public List<MauSacResponse> findByName2(String name) {
        return mauSacRepo.findByNameContaining(name)
                .stream()
                .map(MauSacResponse::new)
                .collect(Collectors.toList());
    }
}
