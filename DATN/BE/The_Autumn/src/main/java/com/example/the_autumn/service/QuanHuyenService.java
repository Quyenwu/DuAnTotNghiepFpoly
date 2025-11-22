package com.example.the_autumn.service;

import com.example.the_autumn.model.response.QuanHuyenResponse;
import com.example.the_autumn.repository.QuanHuyenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuanHuyenService {
    @Autowired
    private QuanHuyenRepository quanHuyenRepository;
    public List<QuanHuyenResponse> getAll(){
        return quanHuyenRepository.findAll().stream().map(QuanHuyenResponse::new).toList();
    }
}
