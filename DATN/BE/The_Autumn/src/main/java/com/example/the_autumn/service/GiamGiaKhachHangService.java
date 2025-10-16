package com.example.the_autumn.service;

import com.example.the_autumn.repository.GiamGiaKhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GiamGiaKhachHangService {

    @Autowired
    private GiamGiaKhachHangRepository giamGiaKhachHangRepository;
}
