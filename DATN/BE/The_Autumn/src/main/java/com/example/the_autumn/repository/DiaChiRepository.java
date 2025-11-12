package com.example.the_autumn.repository;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DiaChiRepository extends JpaRepository<DiaChi,Integer> {
    List<DiaChi> findByKhachHangId(Integer khachHangId);
    List<DiaChi> findByTrangThai(Boolean trangThai);
}
