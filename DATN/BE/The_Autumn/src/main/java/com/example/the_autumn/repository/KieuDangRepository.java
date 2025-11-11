package com.example.the_autumn.repository;

import com.example.the_autumn.entity.KieuDang;
import com.example.the_autumn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KieuDangRepository extends JpaRepository<KieuDang,Integer> {

    @Query("SELECT kd FROM KieuDang kd WHERE LOWER(kd.tenKieuDang) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<KieuDang> findByNameContaining(String name);

    List<KieuDang> findByTenKieuDangContainingIgnoreCase(String name);
    List<KieuDang> findByTrangThai(Boolean trangThai);
}
