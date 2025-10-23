package com.example.the_autumn.repository;

import com.example.the_autumn.entity.KichThuoc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KichThuocRepository extends JpaRepository<KichThuoc, Integer> {

    @Query("SELECT kt FROM KichThuoc kt WHERE LOWER(kt.tenKichThuoc) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<KichThuoc> findByNameContaining(String name);

    List<KichThuoc> findByTenKichThuocContainingIgnoreCase(String name);
}
