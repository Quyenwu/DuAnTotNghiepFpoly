package com.example.the_autumn.service;

import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryService {

    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    public ProductQueryService(ChiTietSanPhamRepository chiTietSanPhamRepository) {
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
    }

//    public Page<ChiTietSanPham> search(String query, int page, int size) {
//        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));
//        return chiTietSanPhamRepository.search(query == null || query.isBlank() ? null : query.trim(), pageable);
//    }
}






