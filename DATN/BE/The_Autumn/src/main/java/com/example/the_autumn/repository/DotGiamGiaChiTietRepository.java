package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotGiamGiaChiTietRepository extends JpaRepository<DotGiamGiaChiTiet, Integer> {

    List<DotGiamGiaChiTiet> findByDotGiamGia(DotGiamGia dotGiamGia);

    List<DotGiamGiaChiTiet> findByChiTietSanPham(ChiTietSanPham chiTietSanPham);

    void deleteByDotGiamGia(DotGiamGia dotGiamGia);

    List<DotGiamGiaChiTiet> findByDotGiamGiaTrangThai(Boolean dotGiamGiaTrangThai);
}



