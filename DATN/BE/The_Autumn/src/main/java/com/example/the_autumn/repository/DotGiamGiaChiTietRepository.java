package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DotGiamGiaChiTietRepository extends JpaRepository<DotGiamGiaChiTiet, Integer> {

    List<DotGiamGiaChiTiet> findByDotGiamGia(DotGiamGia dot);

    @Query("SELECT COALESCE(MAX(d.doUuTien), 0) FROM DotGiamGiaChiTiet d WHERE d.chiTietSanPham.id = :idCtsp")
    int findMaxDoUuTienByCtspId(@Param("idCtsp") Integer idCtsp);

    @Query("SELECT d FROM DotGiamGiaChiTiet d WHERE d.chiTietSanPham.id = :idCtsp")
    List<DotGiamGiaChiTiet> findByChiTietSanPhamId(@Param("idCtsp") Integer idCtsp);
}



