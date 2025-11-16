package com.example.the_autumn.repository;

import com.example.the_autumn.entity.MauSac;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MauSacRepository extends JpaRepository<MauSac, Integer> {

    @Query("SELECT ms FROM MauSac ms WHERE LOWER(ms.tenMauSac) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<MauSac> findByNameContaining(String name);

    List<MauSac> findByTenMauSacContainingIgnoreCase(String name);
}
