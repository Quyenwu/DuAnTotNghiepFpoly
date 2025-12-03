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
    boolean existsByNhanVien_IdAndThoiGianKetThucIsNull(Integer idNhanVien);
    // Lấy ca đã kết thúc gần nhất của nhân viên (để check tiền bắt đầu ca mới)
    Optional<GiaoCa> findFirstByThoiGianKetThucIsNotNullOrderByThoiGianKetThucDesc();

    // 2. Dùng cho getAll(): Lấy tất cả giao ca trong khoảng thời gian, không lọc theo NV (Lấy ca trong ngày)
    List<GiaoCa> findByThoiGianBatDauBetweenOrderByThoiGianBatDauDesc(
            LocalDateTime start, LocalDateTime end);

}
