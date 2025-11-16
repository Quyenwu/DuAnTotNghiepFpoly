package com.example.the_autumn.repository;

import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {
    Optional<PhuongThucThanhToan> findByTenPhuongThucThanhToan(String tenPhuongThuc);

}
