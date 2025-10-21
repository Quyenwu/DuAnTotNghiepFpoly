package com.example.the_autumn.repository;

import com.example.the_autumn.entity.LichSuHoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LichSuHoaDonRepository extends JpaRepository<LichSuHoaDon, Integer> {

    @Query("SELECT l.khachHang.id AS khachHangId, COUNT(l) AS soLanMua, MAX(l.ngayCapNhat) AS ngayMuaGanNhat " +
            "FROM LichSuHoaDon l " +
            "GROUP BY l.khachHang.id")
    List<Object[]> getSoLanVaNgayMuaGanNhatCuaKhachHang();
}
