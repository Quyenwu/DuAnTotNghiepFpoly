package com.example.the_autumn.repository;

import com.example.the_autumn.entity.CaLamViec;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CaLamViecRepository extends JpaRepository<CaLamViec, Integer> {
    List<CaLamViec> findByTrangThai(Boolean trangThai);
}
