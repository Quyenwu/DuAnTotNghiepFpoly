package com.example.the_autumn.repository;

import com.example.the_autumn.entity.CoAo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface CoAoRepository extends JpaRepository<CoAo,Integer> {

    @Query("SELECT ca FROM CoAo ca WHERE LOWER(ca.tenCoAo) LIKE LOWER(CONCAT('%', ?1, '%'))")
    List<CoAo> findByNameContaining(String name);

    List<CoAo> findByTenCoAoContainingIgnoreCase(String name);
}
