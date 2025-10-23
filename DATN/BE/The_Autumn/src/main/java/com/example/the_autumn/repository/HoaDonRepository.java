package com.example.the_autumn.repository;


import com.example.the_autumn.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;


@Repository
public interface HoaDonRepository extends JpaRepository<HoaDon, Integer>, JpaSpecificationExecutor<HoaDon> {

    @Query("SELECT h FROM HoaDon h " +
            "LEFT JOIN h.khachHang k " +
            "LEFT JOIN h.nhanVien n " +
            "WHERE (:maHoaDon IS NULL OR h.maHoaDon LIKE %:maHoaDon%) " +
            "AND (:tenKhachHang IS NULL OR k.hoTen LIKE %:tenKhachHang%) " +
            "AND (:tenNhanVien IS NULL OR n.hoTen LIKE %:tenNhanVien%) " +
            "AND (:loaiHoaDon IS NULL OR h.loaiHoaDon = :loaiHoaDon) " +
            "AND (:trangThai IS NULL OR h.trangThai = :trangThai) " +
            "AND (:ngayTao IS NULL OR CAST(h.ngayTao AS date) = CAST(:ngayTao AS date))")
    Page<HoaDon> searchAndFilter(
            @Param("maHoaDon") String maHoaDon,
            @Param("tenKhachHang") String tenKhachHang,
            @Param("tenNhanVien") String tenNhanVien,
            @Param("loaiHoaDon") String loaiHoaDon,
            @Param("trangThai") Integer trangThai,
            @Param("ngayTao") Date ngayTao,
            Pageable pageable
    );

    @Query("SELECT hd FROM HoaDon hd " +
            "LEFT JOIN FETCH hd.khachHang " +
            "LEFT JOIN FETCH hd.nhanVien")
    List<HoaDon> findAllWithDetails();

    @Query("SELECT COUNT(h) FROM HoaDon h WHERE h.maHoaDon LIKE CONCAT(:prefix, '%')")
    Integer countByMaHoaDonStartingWith(@Param("prefix") String prefix);


}






//