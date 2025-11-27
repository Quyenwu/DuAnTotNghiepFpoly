package com.example.the_autumn.repository;

import com.example.the_autumn.entity.ShippingAddressMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShippingAddressMappingRepository extends JpaRepository<ShippingAddressMapping, Long> {

    Optional<ShippingAddressMapping> findByQuanHuyenId(Integer quanHuyenId);

    @Query("SELECT m FROM ShippingAddressMapping m WHERE m.tinhThanh.id = :tinhThanhId AND m.quanHuyen.id = :quanHuyenId")
    Optional<ShippingAddressMapping> findByTinhThanhIdAndQuanHuyenId(
            @Param("tinhThanhId") Integer tinhThanhId,
            @Param("quanHuyenId") Integer quanHuyenId);

    @Query("SELECT m FROM ShippingAddressMapping m WHERE m.tinhThanh.id = :tinhThanhId")
    java.util.List<ShippingAddressMapping> findByTinhThanhId(@Param("tinhThanhId") Integer tinhThanhId);
}