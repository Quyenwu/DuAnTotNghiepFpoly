package com.example.the_autumn.repository;

import com.example.the_autumn.entity.GiaoCa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GiaoCaRepository extends JpaRepository<GiaoCa,Integer> {

    Optional<GiaoCa> findByNhanVienIdAndTrangThai(Integer nhanVienId, Boolean trangThai);
}
