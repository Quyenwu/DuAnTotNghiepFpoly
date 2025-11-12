package com.example.the_autumn.repository;

import com.example.the_autumn.entity.DotGiamGia;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DotGiamGiaRepository extends JpaRepository<DotGiamGia, Integer>, JpaSpecificationExecutor<DotGiamGia> {
    @Query("SELECT d FROM DotGiamGia d WHERE d.trangThai = 1 AND d.ngayBatDau <= :today AND d.ngayKetThuc >= :today")
    List<DotGiamGia> findActive(@Param("today") LocalDate today);
}



