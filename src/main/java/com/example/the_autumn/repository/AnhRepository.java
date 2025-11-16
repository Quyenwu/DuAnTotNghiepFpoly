package com.example.the_autumn.repository;

import com.example.the_autumn.entity.Anh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnhRepository extends JpaRepository<Anh, Integer> {
    // ✅ GIỮ LẠI - ĐÚNG SYNTAX
    List<Anh> findByChiTietSanPham_Id(Integer idChiTietSanPham);

    List<Anh> findByChiTietSanPham_IdAndTrangThai(Integer idChiTietSanPham, Boolean trangThai);

}