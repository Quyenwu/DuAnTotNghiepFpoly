package com.example.the_autumn.repository;

import com.example.the_autumn.entity.QuanHuyen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuanHuyenRepository extends JpaRepository<QuanHuyen,Integer> {
    List<QuanHuyen> findByTinhThanhId(Integer idTinh);
}
