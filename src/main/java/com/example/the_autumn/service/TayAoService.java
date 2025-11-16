package com.example.the_autumn.service;

import com.example.the_autumn.entity.TayAo;
import com.example.the_autumn.model.request.TayAoRequest;
import com.example.the_autumn.model.response.TayAoResponse;
import com.example.the_autumn.repository.TayAoRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TayAoService {

    @Autowired
    private TayAoRepository tayAoRepo;

    public List<TayAoResponse> findAll() {
        return tayAoRepo.findAll()
                .stream()
                .map(TayAoResponse::new)
                .toList();
    }

    public void add(TayAoRequest request) {
        TayAo tayAo = MapperUtils.map(request, TayAo.class);
        tayAo.setTrangThai(true);
        tayAoRepo.save(tayAo);
    }

    public List<TayAoResponse> findByName(String name) {
        return tayAoRepo.findByTenTayAoContainingIgnoreCase(name)
                .stream()
                .map(TayAoResponse::new)
                .collect(Collectors.toList());
    }

    public List<TayAoResponse> findByName2(String name) {
        return tayAoRepo.findByNameContaining(name)
                .stream()
                .map(TayAoResponse::new)
                .collect(Collectors.toList());
    }
}
