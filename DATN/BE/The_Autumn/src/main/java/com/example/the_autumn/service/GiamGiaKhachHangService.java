package com.example.the_autumn.service;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.model.response.GiamGiaKhachHangResponse;
import com.example.the_autumn.repository.GiamGiaKhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiamGiaKhachHangService {

    @Autowired
    private GiamGiaKhachHangRepository giamGiaKhachHangRepository;

    public List<GiamGiaKhachHangResponse>  getAllGiamGiaKhachHang(){
        return giamGiaKhachHangRepository.findAll().stream().map(GiamGiaKhachHangResponse::new).toList();
    }
}
