package com.example.the_autumn.service;

import com.example.the_autumn.entity.PhuongThucThanhToan;
import com.example.the_autumn.model.response.PhuongThucThanhToanResponse;
import com.example.the_autumn.repository.PhuongThucThanhToanRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhuongThucThanhToanService {

    @Autowired
    private PhuongThucThanhToanRepository  phuongThucThanhToanRepository;

    public List<PhuongThucThanhToanResponse>  getPhuongThucThanhToan(){
        return phuongThucThanhToanRepository.findAll().stream().map(PhuongThucThanhToanResponse::new).toList();
    }
}
