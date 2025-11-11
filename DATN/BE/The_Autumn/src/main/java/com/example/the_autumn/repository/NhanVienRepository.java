package com.example.the_autumn.repository;

import com.example.the_autumn.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> , JpaSpecificationExecutor<NhanVien> {
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
    Optional<NhanVien> findByEmail(String email);

    @Query("SELECT n FROM NhanVien n LEFT JOIN FETCH n.chucVu WHERE n.email = :email")
    NhanVien getNhanVienByEmail(@Param("email") String email);
}
