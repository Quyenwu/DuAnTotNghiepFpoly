package com.example.the_autumn.repository;

import com.example.the_autumn.entity.NhaSanXuat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NhaSanXuatRepository extends JpaRepository<NhaSanXuat,Integer> {

    @Query("SELECT nsx FROM NhaSanXuat nsx WHERE LOWER(nsx.tenNhaSanXuat) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<NhaSanXuat> findByNameContaining(String name);

    List<NhaSanXuat> findByTenNhaSanXuatContainingIgnoreCase(String name);
}
