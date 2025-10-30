package com.example.the_autumn.repository;

import com.example.the_autumn.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface NhanVienRepository extends JpaRepository<NhanVien, Integer> , JpaSpecificationExecutor<NhanVien> {
    boolean existsByEmail(String email);
    boolean existsBySdt(String sdt);
}
