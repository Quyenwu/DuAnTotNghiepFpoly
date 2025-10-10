package com.example.the_autumn.repository;

import com.example.the_autumn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    Optional<KhachHang> findByTenTaiKhoan(String tenTaiKhoan);
    boolean existsByTenTaiKhoan(String tenTaiKhoan);
    Optional<KhachHang> findById(Integer id);
}
