package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.repository.KhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<KhachHangResponse> getAllKhachHang(){
        return khachHangRepository.findAll().stream().map(KhachHangResponse :: new).toList();
    }
}
