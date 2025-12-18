package com.example.the_autumn.repository;

import com.example.the_autumn.entity.HinhThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HinhThucThanhToanRepository extends JpaRepository<HinhThucThanhToan, Integer> {
    List<HinhThucThanhToan> findByHoaDonId(Integer id);
}
