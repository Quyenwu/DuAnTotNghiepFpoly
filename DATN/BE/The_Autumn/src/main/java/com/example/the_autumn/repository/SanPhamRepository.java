package com.example.the_autumn.repository;

import com.example.the_autumn.entity.SanPham;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    @Query("select sp from SanPham sp where (:q is null or lower(sp.tenSanPham) like lower(concat('%', :q, '%')) or lower(sp.maSanPham) like lower(concat('%', :q, '%')))")
    Page<SanPham> search(String q, Pageable pageable);

}






