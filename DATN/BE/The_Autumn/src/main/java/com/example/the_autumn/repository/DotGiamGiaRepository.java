package com.example.the_autumn.repository;

import com.example.the_autumn.entity.DotGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Integer> {

    Optional<DotGiamGia> findByMaGiamGia(String maGiamGia);

    Page<DotGiamGia> searchByTenDotOrMaGiamGia(String tenDot, String maGiamGia, Pageable pageable);
}



