package com.example.the_autumn.service;

import com.example.the_autumn.entity.CoAo;
import com.example.the_autumn.model.request.CoAoRequest;
import com.example.the_autumn.model.response.CoAoResponse;
import com.example.the_autumn.repository.CoAoRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoAoService {

    @Autowired
    private CoAoRepository coAoRepo;

    public List<CoAoResponse> findAll() {
        return coAoRepo.findAll()
                .stream()
                .map(CoAoResponse::new)
                .toList();
    }

    public void add(CoAoRequest request) {
        CoAo coAo = MapperUtils.map(request, CoAo.class);
        coAo.setTrangThai(true);
        coAoRepo.save(coAo);
    }

    public List<CoAoResponse> findByName(String name) {
        return coAoRepo.findByTenCoAoContainingIgnoreCase(name)
                .stream()
                .map(CoAoResponse::new)
                .collect(Collectors.toList());
    }

    public List<CoAoResponse> findByName2(String name) {
        return coAoRepo.findByNameContaining(name)
                .stream()
                .map(CoAoResponse::new)
                .collect(Collectors.toList());
    }
}
