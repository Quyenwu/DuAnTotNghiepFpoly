package com.example.the_autumn.repository;

import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.PhuongThucThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface PhuongThucThanhToanRepository extends JpaRepository<PhuongThucThanhToan, Integer> {

    List<PhuongThucThanhToan> findByTrangThai(Boolean trangThai);
}
