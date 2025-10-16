package com.example.the_autumn.service;

import com.example.the_autumn.model.response.ChucVuResponse;
import com.example.the_autumn.repository.ChucVuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChucVuService {


    @Autowired
    private ChucVuRepository chucVuRepository;


    public List<ChucVuResponse> getAllChucVu() {
        return chucVuRepository.findAll().stream().map(ChucVuResponse::new).collect(Collectors.toList());
    }




}





