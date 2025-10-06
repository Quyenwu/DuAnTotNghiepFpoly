package com.example.the_autumn.service;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.PhieuGiamGia;
import com.example.the_autumn.repository.GiamGiaKhachHangRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.PhieuGiamGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GiamGiaKhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private PhieuGiamGiaRepository  phieuGiamGiaRepository;

    @Autowired
    private GiamGiaKhachHangRepository GiamGiaKhachHangRepository;
    @Autowired
    private GiamGiaKhachHangRepository giamGiaKhachHangRepository;

    public void ganPhieuChoKhachHang(Integer idPhieu, List<Integer> idsKhachHang) {
        PhieuGiamGia phieu = phieuGiamGiaRepository.findById(idPhieu)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu"));

        for (Integer idKH : idsKhachHang) {
            KhachHang kh = khachHangRepository.findById(idKH)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng: " + idKH));

            GiamGiaKhachHang khPhieu = new GiamGiaKhachHang();
            khPhieu.setPhieuGiamGia(phieu);
            khPhieu.setKhachHang(kh);

            giamGiaKhachHangRepository.save(khPhieu);
        }
    }

    public List<KhachHang> getKhachHangTheoPhieuGiamGia(Integer idPhieu) {
        return giamGiaKhachHangRepository.findByPhieuGiamGia_Id(idPhieu)
                .stream()
                .map(GiamGiaKhachHang::getKhachHang)
                .toList();
    }
}
