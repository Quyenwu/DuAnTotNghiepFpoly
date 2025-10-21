package com.example.the_autumn.repository;

import com.example.the_autumn.entity.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KhachHangRepository extends JpaRepository<KhachHang, Integer> {
    @Query("""
        SELECT kh FROM KhachHang kh
        WHERE 
            LOWER(kh.hoTen) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(kh.sdt) LIKE LOWER(CONCAT('%', :keyword, '%'))
            OR LOWER(kh.email) LIKE LOWER(CONCAT('%', :keyword, '%'))
        ORDER BY kh.ngayTao DESC
    """)
    List<KhachHang> searchByKeyword(@Param("keyword") String keyword);
    @Query("""
        SELECT kh FROM KhachHang kh
        WHERE 
            (:gioiTinh IS NULL OR kh.gioiTinh = :gioiTinh)
        AND (:trangThai IS NULL OR kh.trangThai = :trangThai)
        ORDER BY kh.ngayTao DESC
    """)
    List<KhachHang> filterByGenderAndStatus(
            @Param("gioiTinh") Boolean gioiTinh,
            @Param("trangThai") Boolean trangThai
    );
}
