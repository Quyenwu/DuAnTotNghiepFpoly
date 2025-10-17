package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    public List<KhachHangResponse> getAllKhachHang() {
        List<KhachHang> khachHangs = khachHangRepository.findAll();

        Map<Integer, Object[]> statsMap = lichSuHoaDonRepository.getSoLanVaNgayMuaGanNhatCuaKhachHang()
                .stream()
                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));

        List<KhachHangResponse> result = new ArrayList<>();

        for (KhachHang k : khachHangs) {
            Object[] stats = statsMap.get(k.getId());
            Long soLanMuaLong = stats != null ? (Long) stats[1] : 0L;
            Integer soLanMua = soLanMuaLong != null ? soLanMuaLong.intValue() : 0;
            Date ngayMuaGanNhat = stats != null ? (Date) stats[2] : null;

            result.add(new KhachHangResponse(k, soLanMua, ngayMuaGanNhat));
        }
        return result;
    }
}
