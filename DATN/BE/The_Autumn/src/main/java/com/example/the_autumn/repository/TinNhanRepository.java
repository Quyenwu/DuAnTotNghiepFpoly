package com.example.the_autumn.repository;

import com.example.the_autumn.entity.TinNhan;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TinNhanRepository extends JpaRepository<TinNhan, Integer> {
    List<TinNhan> findByPhongChatIdOrderByThoiGianAsc(Integer phongId);
    TinNhan findTopByPhongChatIdOrderByThoiGianDesc(Integer phongId);
}
