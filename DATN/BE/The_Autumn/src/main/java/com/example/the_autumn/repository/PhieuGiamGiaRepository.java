package com.example.the_autumn.repository;

import com.example.the_autumn.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer> {

    List<PhieuGiamGia> findByMaGiamGiaContainingIgnoreCaseOrTenChuongTrinhContainingIgnoreCase(String maGiamGia, String tenChuongTrinh);

    List<PhieuGiamGia> findByNgayBatDauBetween(Date ngayBatDau, Date ngayKetThuc);

    List<PhieuGiamGia> findByNgayBatDauGreaterThanEqualAndNgayKetThucLessThanEqual(Date ngayBatDau, Date ngayKetThuc);
}
