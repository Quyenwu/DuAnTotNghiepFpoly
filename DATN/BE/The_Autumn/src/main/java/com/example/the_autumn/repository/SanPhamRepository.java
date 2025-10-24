package com.example.the_autumn.repository;

import com.example.the_autumn.entity.SanPham;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByTenSanPhamAndTrangThai(String tenSanPham, Boolean trangThai);

    @Query("select sp from SanPham sp where (:q is null or lower(sp.tenSanPham) like lower(concat('%', :q, '%')) or lower(sp.maSanPham) like lower(concat('%', :q, '%')))")
    Page<SanPham> search(String q, Pageable pageable);
  
    Optional<SanPham> findByMaSanPham(String maSanPham);

    boolean existsByTenSanPham(String tenSanPham);

    boolean existsByMaSanPham(String maSanPham);

    @EntityGraph(attributePaths = {
            "nhaSanXuat", "xuatXu", "chatLieu", "kieuDang", "coAo", "tayAo",
            "chiTietSanPham", "chiTietSanPham.mauSac", "chiTietSanPham.kichThuoc",
            "chiTietSanPham.trongLuong", "chiTietSanPham.anhs"
    })
    @Query("SELECT sp FROM SanPham sp WHERE sp.id = :id")
    Optional<SanPham> findByIdWithDetails(@Param("id") Integer id);
}

