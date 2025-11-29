package com.example.the_autumn.repository;

import com.example.the_autumn.entity.PhanCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhanCaRepository extends JpaRepository<PhanCa, Integer> {
    List<PhanCa> findByNhanVienId(Integer idNhanVien);
    List<PhanCa> findByNgayPhanCa(LocalDate ngayPhanCa);
    @Query("SELECT pc FROM PhanCa pc WHERE pc.nhanVien.id = ?1 " +
            "AND pc.ngayPhanCa >= ?2 AND pc.ngayPhanCa <= ?3")
    List<PhanCa> findByNhanVienAndDateRange(Integer idNhanVien,
                                            LocalDate startDate,
                                            LocalDate endDate);
    List<PhanCa> findByNhanVien_IdAndNgayPhanCaAndTrangThaiTrue(Integer idNhanVien,

                                                               LocalDate ngayPhanCa);
    List<PhanCa> findByNhanVien_IdAndNgayPhanCaAndTrangThai(
            Integer idNhanVien,
            LocalDate ngayPhanCa,
            Boolean trangThai
    );
    Optional<PhanCa> findFirstByCaLamViec_IdAndNgayPhanCaAndTrangThaiTrue(
            Integer idCaLamViec,
            LocalDate ngayPhanCa
    );
}
