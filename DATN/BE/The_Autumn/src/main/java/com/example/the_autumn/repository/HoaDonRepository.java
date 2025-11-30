package com.example.the_autumn.repository;


import com.example.the_autumn.entity.HoaDon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;


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

    Optional<HoaDon> findByMaHoaDon(String maHoaDon);
    List<HoaDon> findByTrangThai(Integer trangThai);

    @Query("SELECT MAX(CAST(SUBSTRING(h.maHoaDon, 3) AS integer)) FROM HoaDon h WHERE h.maHoaDon LIKE 'HD%'")
    Optional<Integer> findMaxMaHoaDonNumber();

    List<HoaDon> findByKhachHangIdAndPhieuGiamGiaIdAndTrangThaiNot(
            Integer khachHangId,
            Integer phieuGiamGiaId,
            Integer trangThai
    );

    @Query("SELECT h FROM HoaDon h WHERE h.khachHang.id = :khachHangId ORDER BY h.ngayTao DESC")
    List<HoaDon> findByKhachHangIdOrderByNgayTaoDesc(@Param("khachHangId") Integer khachHangId);

    @Query("SELECT h FROM HoaDon h WHERE h.maHoaDon IN :maHoaDonList ORDER BY h.ngayTao DESC")
    List<HoaDon> findByMaHoaDonInOrderByNgayTaoDesc(@Param("maHoaDonList") List<String> maHoaDonList);

    @Query("SELECT SUM(h.tongTien) FROM HoaDon h " +
            "WHERE h.nhanVien.id = :idNhanVien " +
            "AND h.ngayTao >= :thoiGianBatDau " +
            "AND h.trangThai = 1")
    BigDecimal tinhTongTienTheoCa(Integer idNhanVien, LocalDateTime thoiGianBatDau);


    @Query(value = """
        SELECT COALESCE(SUM(hd.tong_tien_sau_giam), 0)
        FROM hoa_don hd
        WHERE hd.nguoi_tao = :idNhanVien
          AND hd.loai_hoa_don = 1
          AND hd.trang_thai = 3
          -- Lọc theo cột DATETIME (ngay_tao) và khoảng thời gian (giờ/phút) chính xác
          AND hd.ngay_tao BETWEEN :startTime AND :endTime 
        """,
            nativeQuery = true)
    BigDecimal sumDoanhThuTrongCa(
            @Param("idNhanVien") Integer idNhanVien,
            @Param("startTime") LocalDateTime startTime, // Thay thế từ LocalDate
            @Param("endTime") LocalDateTime endTime      // Thay thế từ LocalDate
    );

}


