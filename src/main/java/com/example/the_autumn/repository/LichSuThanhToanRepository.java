package com.example.the_autumn.repository;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.LichSuHoaDon;
import com.example.the_autumn.entity.LichSuThanhToan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LichSuThanhToanRepository extends JpaRepository<LichSuThanhToan, Integer> {
    List<LichSuThanhToan> findByHoaDonId(Integer hoaDonId);

    List<LichSuThanhToan> findByHoaDonIdAndTrangThai(Integer hoaDonId, Boolean trangThai);
    @Query("SELECT lst FROM LichSuThanhToan lst WHERE lst.hoaDon.maHoaDon = :maHoaDon ORDER BY lst.ngayThanhToan DESC")
    List<LichSuThanhToan> findByHoaDonMaHoaDonOrderByNgayThanhToanDesc(@Param("maHoaDon") String maHoaDon);

    List<LichSuThanhToan> findByHoaDonAndTrangThai(HoaDon hoaDon, Boolean trangThai);

    List<LichSuThanhToan> findByHoaDon(HoaDon hoaDon);



}