package com.example.the_autumn.repository;

import com.example.the_autumn.entity.TayAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TayAoRepository extends JpaRepository<TayAo, Integer> {

    @Query("SELECT ta FROM TayAo ta WHERE LOWER(ta.tenTayAo) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<TayAo> findByNameContaining(String name);

    List<TayAo> findByTenTayAoContainingIgnoreCase(String name);
}
