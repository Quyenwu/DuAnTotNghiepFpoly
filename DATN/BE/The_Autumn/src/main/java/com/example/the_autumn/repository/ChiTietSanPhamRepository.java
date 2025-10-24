package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChiTietSanPham;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;










import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ChiTietSanPhamRepository extends JpaRepository<ChiTietSanPham, Integer> {

    boolean existsBySanPham_IdAndMauSac_IdAndKichThuoc_IdAndTrongLuong(
            Integer idSanPham, Integer idMauSac, Integer idKichThuoc, String trongLuong
    );
 @Query("select ct from ChiTietSanPham ct join ct.sanPham sp where (:q is null or lower(sp.tenSanPham) like lower(concat('%', :q, '%')) or lower(sp.maSanPham) like lower(concat('%', :q, '%')))")
    Page<ChiTietSanPham> search(String q, Pageable pageable);
  
    List<ChiTietSanPham> findBySanPham_Id(Integer idSanPham);

    List<ChiTietSanPham> findBySanPham_IdAndTrangThai(Integer idSanPham, Boolean trangThai);

    @Modifying
    @Transactional
    @Query("UPDATE ChiTietSanPham ctsp SET ctsp.giaBan = :giaBan WHERE ctsp.id = :id")
    void capNhatGia(@Param("id") Integer id, @Param("giaBan") BigDecimal giaBan);

    @Modifying
    @Transactional
    @Query("UPDATE ChiTietSanPham ctsp SET ctsp.soLuongTon = :soLuong WHERE ctsp.id = :id")
    void capNhatSoLuong(@Param("id") Integer id, @Param("soLuong") Integer soLuong);

    Optional<ChiTietSanPham> findByMaVach(String maVach);

    Long countBySanPham_Id(Integer idSanPham);

    List<ChiTietSanPham> findBySanPham_IdAndSoLuongTonGreaterThan(Integer idSanPham, Integer soLuong);

    @Modifying
    @Transactional
    void deleteBySanPham_Id(Integer idSanPham);


    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
            "LEFT JOIN FETCH ctsp.sanPham " +
            "LEFT JOIN FETCH ctsp.mauSac " +
            "LEFT JOIN FETCH ctsp.kichThuoc " +
            "LEFT JOIN FETCH ctsp.trongLuong")
    List<ChiTietSanPham> findAllWithRelations();



    @Query("SELECT ctsp FROM ChiTietSanPham ctsp " +
            "WHERE ctsp.sanPham.id = :sanPhamId")
    List<ChiTietSanPham> findBySanPhamId(@Param("sanPhamId") Integer sanPhamId);
}

