package com.example.the_autumn.repository;

import com.example.the_autumn.entity.TinhThanh;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TinhThanhRepository extends JpaRepository<TinhThanh,Integer> {

}
