package com.example.the_autumn.repository;

import com.example.the_autumn.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    @Query("SELECT lshd.khachHang.id AS khachHangId, " +
            "COUNT(lshd) AS soLanMua, " +
            "MAX(lshd.ngayCapNhat) AS ngayMuaGanNhat, " +
            "COALESCE(SUM(hd.tongTienSauGiam), 0) AS tongTienDaMua " +
            "FROM LichSuHoaDon lshd " +
            "JOIN lshd.hoaDon hd " +
            "GROUP BY lshd.khachHang.id")
    List<Object[]> getSoLanVaNgayMuaGanNhatCuaKhachHang();



    List<LichSuHoaDon> findByHoaDon_IdOrderByNgayCapNhatDesc(Integer hoaDonId);

    // Lấy lịch sử theo trạng thái
    List<LichSuHoaDon> findByHoaDon_IdAndTrangThaiOrderByNgayCapNhatDesc(Integer hoaDonId, Boolean trangThai);
    boolean existsByHoaDonIdAndTrangThai(Integer hoaDonId, Boolean trangThai);

    @Query("SELECT lsh FROM LichSuHoaDon lsh WHERE lsh.hoaDon.maHoaDon = :maHoaDon ORDER BY lsh.ngayCapNhat DESC")
    List<LichSuHoaDon> findByHoaDonMaHoaDonOrderByNgayCapNhatDesc(@Param("maHoaDon") String maHoaDon);
}
