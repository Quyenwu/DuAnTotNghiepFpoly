package com.example.the_autumn.repository;

import com.example.the_autumn.entity.LichSuThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Integer> {
    @Query("SELECT lst FROM LichSuThanhToan lst WHERE lst.hoaDon.id = :hoaDonId ORDER BY lst.ngayThanhToan DESC")
    List<LichSuThanhToan> findByHoaDonIdOrderByNgayThanhToanDesc(@Param("hoaDonId") Integer hoaDonId);

    @Query("SELECT COUNT(lst) > 0 FROM LichSuThanhToan lst WHERE lst.hoaDon.id = :hoaDonId")
    boolean existsByHoaDonId(@Param("hoaDonId") Integer hoaDonId);
}
