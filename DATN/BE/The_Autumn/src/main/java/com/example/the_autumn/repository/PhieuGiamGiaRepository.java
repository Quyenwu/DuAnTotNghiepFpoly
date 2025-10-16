package com.example.the_autumn.repository;

import
        com.example.the_autumn.entity.PhieuGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


@Repository
public interface PhieuGiamGiaRepository extends JpaRepository<PhieuGiamGia, Integer>, JpaSpecificationExecutor<PhieuGiamGia> {

}
