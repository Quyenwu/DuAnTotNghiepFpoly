package com.example.the_autumn.repository;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> {
    boolean existsByTaiKhoan(String TaiKhoan);
    Optional<NhanVien> findById(Integer id);
    Optional<NhanVien>findByTaiKhoan(String tk);
}
