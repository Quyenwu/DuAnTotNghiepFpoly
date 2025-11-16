package com.example.the_autumn.repository;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DiaChiRepository extends JpaRepository<DiaChi,Integer> {
    List<DiaChi> findByKhachHangId(Integer khachHangId);
    Optional<DiaChi> findByKhachHangAndDiaChiCuThe(KhachHang khachHang, String diaChiCuThe);
}
