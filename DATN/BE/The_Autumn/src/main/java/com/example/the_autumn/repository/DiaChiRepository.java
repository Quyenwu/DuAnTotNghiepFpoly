package com.example.the_autumn.repository;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DiaChiRepository extends JpaRepository<DiaChi,Integer> {
    List<DiaChi> findByKhachHangId(Integer khachHangId);
    List<DiaChi> findByTrangThai(Boolean trangThai);
    @Modifying
<<<<<<< HEAD
    @Query("UPDATE DiaChi d SET d.trangThai = :trangThai WHERE d.khachHang.id = :khachHangId")
    void updateTrangThaiByKhachHangId(@Param("khachHangId") Integer khachHangId,
                                      @Param("trangThai") Boolean trangThai);
=======
    @Query("UPDATE DiaChi d SET d.trangThai = false " +
            "WHERE d.khachHang.id = :khachHangId " +
            "AND d.id != :diaChiDuocChonId")
    void disableAllExcept(@Param("khachHangId") Integer khachHangId,
                          @Param("diaChiDuocChonId") Integer diaChiDuocChonId);

    Optional<DiaChi> findByKhachHangAndDiaChiCuThe(KhachHang khachHang, String diaChiKhachHang);
>>>>>>> 4c0bd468bdf0b9c1d09f45c24eb32911088ca753
}
