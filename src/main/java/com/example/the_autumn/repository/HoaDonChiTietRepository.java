package com.example.the_autumn.repository;

import com.example.the_autumn.entity.HoaDonChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HoaDonChiTietRepository extends JpaRepository<HoaDonChiTiet, Integer> {
    List<HoaDonChiTiet> findByHoaDon_Id(Integer hoaDonId);

    List<HoaDonChiTiet> findByHoaDonId(Integer hoaDonId);
    Optional<HoaDonChiTiet> findByHoaDonIdAndChiTietSanPhamId(Integer hoaDonId, Integer chiTietSanPhamId);
    void deleteByHoaDonIdAndChiTietSanPhamId(Integer hoaDonId, Integer chiTietSanPhamId);
}
