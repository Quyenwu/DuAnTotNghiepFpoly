package com.example.the_autumn.repository;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GiamGiaKhachHangRepository extends JpaRepository<GiamGiaKhachHang, Integer> {
    List<GiamGiaKhachHang> findByPhieuGiamGia_Id(Integer phieuId);

    @Modifying
    @Query("DELETE FROM GiamGiaKhachHang g WHERE g.phieuGiamGia.id = :phieuGiamGiaId")
    void deleteByPhieuGiamGiaId(@Param("phieuGiamGiaId") Integer phieuGiamGiaId);

}
