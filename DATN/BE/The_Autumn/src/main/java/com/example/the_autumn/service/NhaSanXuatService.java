package com.example.the_autumn.service;

import com.example.the_autumn.entity.NhaSanXuat;
import com.example.the_autumn.model.request.NhaSanXuatRequest;
import com.example.the_autumn.model.response.NhaSanXuatResponse;
import com.example.the_autumn.repository.NhaSanXuatRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NhaSanXuatService {

    @Autowired
    private NhaSanXuatRepository nsxRepo;

    public List<NhaSanXuatResponse> findAll() {return nsxRepo.findAll().stream().map(NhaSanXuatResponse::new).toList();}

    public void add(NhaSanXuatRequest request) {
        NhaSanXuat nsx = MapperUtils.map(request,NhaSanXuat.class);
        nsx.setTrangThai(true);
        nsxRepo.save(nsx);
    }

    public List<NhaSanXuatResponse> findByName(String name) {
        return nsxRepo.findByTenNhaSanXuatContainingIgnoreCase(name)
                .stream()
                .map(NhaSanXuatResponse::new)
                .collect(Collectors.toList());
    }

    public List<NhaSanXuatResponse> findByName2(String name) {
         return nsxRepo.findByNameContaining(name)
                 .stream()
                 .map(NhaSanXuatResponse::new)
                 .collect(Collectors.toList());
    }
}
