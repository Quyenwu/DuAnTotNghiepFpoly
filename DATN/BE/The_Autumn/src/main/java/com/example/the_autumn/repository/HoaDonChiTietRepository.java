package com.example.the_autumn.repository;

import com.example.the_autumn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    List<HoaDonChiTiet> findByHoaDon_Id(Integer hoaDonId);

    List<HoaDonChiTiet> findByHoaDonId(Integer hoaDonId);
    void deleteByHoaDonIdAndChiTietSanPhamId(Integer hoaDonId, Integer chiTietSanPhamId);

    @Query("SELECT hdct FROM HoaDonChiTiet hdct WHERE hdct.hoaDon.id = :idHoaDon AND hdct.chiTietSanPham.id = :idChiTietSanPham")
    Optional<HoaDonChiTiet> findByHoaDonIdAndChiTietSanPhamId(
            @Param("idHoaDon") Integer idHoaDon,
            @Param("idChiTietSanPham") Integer idChiTietSanPham);
}
