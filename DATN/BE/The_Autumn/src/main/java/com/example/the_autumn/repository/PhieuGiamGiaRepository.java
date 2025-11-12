package com.example.the_autumn.repository;

import
        com.example.the_autumn.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer>, JpaSpecificationExecutor<PhieuGiamGia> {
    @Query("SELECT p FROM PhieuGiamGia p WHERE p.trangThai = 1 AND p.ngayBatDau <= :today AND p.ngayKetThuc >= :today")
    List<PhieuGiamGia> findActive(@Param("today") LocalDate today);
}
