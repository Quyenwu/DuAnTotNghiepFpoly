package com.example.the_autumn.repository;

import com.example.the_autumn.entity.XuatXu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface XuatXuRepository extends JpaRepository<XuatXu,Integer> {

    @Query("SELECT xx FROM XuatXu xx WHERE LOWER(xx.tenXuatXu) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<XuatXu> findByNameContaining(String name);

    List<XuatXu> findByTenXuatXuContainingIgnoreCase(String name);
}
