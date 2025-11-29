package com.example.the_autumn.repository;

import com.example.the_autumn.entity.GiaoCa;
import com.example.the_autumn.entity.PhanCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Repository
public interface GiaoCaRepository extends JpaRepository<GiaoCa, Integer> {
    List<GiaoCa> findByNhanVienId(Integer idNhanVien);
    @Query("SELECT gc FROM GiaoCa gc WHERE gc.thoiGianKetThuc IS NULL AND gc.trangThai = true")
    List<GiaoCa> findActiveShifts();
    @Query("SELECT gc FROM GiaoCa gc WHERE gc.nhanVien.id = ?1 AND gc.thoiGianKetThuc IS NULL")
    GiaoCa findActiveShiftByNhanVien(Integer idNhanVien);
    @Query("SELECT gc FROM GiaoCa gc WHERE gc.nhanVien.id = ?1 " +
            "AND gc.thoiGianBatDau >= ?2 AND gc.thoiGianBatDau <= ?3")
    List<GiaoCa> findByNhanVienAndDateRange(Integer idNhanVien,
                                            LocalDateTime startDate,
                                            LocalDateTime endDate);
    @Query("SELECT SUM(gc.tongDoanhThu) FROM GiaoCa gc WHERE gc.nhanVien.id = ?1 " +
            "AND gc.thoiGianBatDau >= ?2 AND gc.thoiGianBatDau <= ?3")
    BigDecimal getTotalRevenueByNhanVienAndDateRange(Integer idNhanVien,
                                                     LocalDateTime startDate,
                                                     LocalDateTime endDate);
    List<GiaoCa> findAllByOrderByThoiGianBatDauDesc();
    // Kiểm tra nhân viên đang có ca chưa kết thúc không
    boolean existsByNhanVien_IdAndThoiGianKetThucIsNull(Integer idNhanVien);
    // Lấy ca đã kết thúc gần nhất của nhân viên (để check tiền bắt đầu ca mới)
    Optional<GiaoCa> findFirstByNhanVien_IdAndThoiGianKetThucIsNotNullOrderByThoiGianKetThucDesc(
            Integer idNhanVien
    );

    boolean existsByNhanVien_IdAndThoiGianKetThucIsNullAndTrangThaiTrue(Integer idNhanVien);

    Optional<GiaoCa> findTopByNhanVien_IdAndThoiGianKetThucIsNotNullOrderByThoiGianKetThucDesc(Integer idNhanVien);

    @Query("""
           SELECT g 
           FROM GiaoCa g
           JOIN FETCH g.nhanVien nv
           ORDER BY g.thoiGianBatDau DESC
           """)
    List<GiaoCa> findAllWithNhanVien();
}
