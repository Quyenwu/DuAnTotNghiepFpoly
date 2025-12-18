package com.example.the_autumn.repository;

import com.example.the_autumn.entity.MauSac;
import com.example.the_autumn.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    @Query("SELECT ms FROM MauSac ms WHERE LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<MauSac> findByNameContaining(String name);

    List<MauSac> findByTenMauSacContainingIgnoreCase(String name);
    List<MauSac> findByTrangThai(Boolean trangThai);

    @Query("SELECT ms FROM MauSac ms WHERE " +
            "(:keyword IS NULL OR " +
            " LOWER(ms.maMauSac) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            " LOWER(ms.maHex) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
            "AND (:trangThai IS NULL OR ms.trangThai = :trangThai) " +
            "ORDER BY ms.ngayTao DESC")
    Page<MauSac> searchWithPaging(
            @Param("keyword") String keyword,
            @Param("trangThai") Boolean trangThai,
            Pageable pageable);
}
