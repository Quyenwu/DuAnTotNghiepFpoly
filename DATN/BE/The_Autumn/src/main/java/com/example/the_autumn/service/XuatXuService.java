package com.example.the_autumn.service;

import com.example.the_autumn.entity.XuatXu;
import com.example.the_autumn.model.request.XuatXuRequest;
import com.example.the_autumn.model.response.XuatXuResponse;
import com.example.the_autumn.repository.XuatXuRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class XuatXuService {

    @Autowired
    private XuatXuRepository xxRepo;

    public List<XuatXuResponse> findAll() {
        return xxRepo.findAll()
                .stream()
                .map(XuatXuResponse::new)
                .toList();
    }

    public void add(XuatXuRequest request) {
        XuatXu xuatXu = MapperUtils.map(request, XuatXu.class);
        xuatXu.setTrangThai(true);
        xxRepo.save(xuatXu);
    }

    public List<XuatXuResponse> findByName(String name) {
        return xxRepo.findByTenXuatXuContainingIgnoreCase(name)
                .stream()
                .map(XuatXuResponse::new)
                .collect(Collectors.toList());
    }

    public List<XuatXuResponse> findByName2(String name) {
        return xxRepo.findByNameContaining(name)
                .stream()
                .map(XuatXuResponse::new)
                .collect(Collectors.toList());
    }
}
