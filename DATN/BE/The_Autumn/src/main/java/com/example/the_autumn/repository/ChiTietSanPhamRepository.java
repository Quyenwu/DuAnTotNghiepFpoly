package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChiTietSanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    @Query("select ct from ChiTietSanPham ct join ct.sanPham sp where (:q is null or lower(sp.tenSanPham) like lower(concat('%', :q, '%')) or lower(sp.maSanPham) like lower(concat('%', :q, '%')))")
    Page<ChiTietSanPham> search(String q, Pageable pageable);
}






