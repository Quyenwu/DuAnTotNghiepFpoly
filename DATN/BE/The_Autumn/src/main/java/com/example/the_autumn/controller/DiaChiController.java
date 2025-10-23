package com.example.the_autumn.controller;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.QuanHuyen;
import com.example.the_autumn.entity.TinhThanh;
import com.example.the_autumn.model.request.DiaChiRequest;
import com.example.the_autumn.model.response.DiaChiResponse;
import com.example.the_autumn.model.response.QuanHuyenResponse;
import com.example.the_autumn.model.response.TinhThanhResponse;
import com.example.the_autumn.repository.DiaChiRepository;
import com.example.the_autumn.repository.QuanHuyenRepository;
import com.example.the_autumn.repository.TinhThanhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/dia-chi")
public class DiaChiController {
    @Autowired
    private TinhThanhRepository tinhThanhRepository;

    @Autowired
    private QuanHuyenRepository quanHuyenRepository;
    @Autowired
    private DiaChiRepository diaChiRepository;

    @GetMapping("/tinh-thanh")
    public List<TinhThanhResponse> getAllTinhThanh() {
        return tinhThanhRepository.findAll().stream()
                .map(TinhThanhResponse::new)
                .collect(Collectors.toList());

    }
    @GetMapping("/quan-huyen")
    public List<QuanHuyenResponse> getQuanByTinh(@RequestParam("idTinh") Integer idTinh) {
        return quanHuyenRepository.findByTinhThanhId(idTinh).stream()
                .map(QuanHuyenResponse::new)
                .collect(Collectors.toList());
    }


}
