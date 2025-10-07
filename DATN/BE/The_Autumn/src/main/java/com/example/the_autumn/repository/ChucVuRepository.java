package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.NhanVien;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChucVuRepository extends JpaRepository<ChucVu, Integer> {
}
