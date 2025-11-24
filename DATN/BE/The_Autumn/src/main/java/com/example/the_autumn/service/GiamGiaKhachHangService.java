package com.example.the_autumn.service;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.model.response.GiamGiaKhachHangResponse;
import com.example.the_autumn.model.response.MaGiamGiaResponse;
import com.example.the_autumn.repository.GiamGiaKhachHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GiamGiaKhachHangService {

    @Autowired
    private GiamGiaKhachHangRepository giamGiaKhachHangRepository;

    public List<GiamGiaKhachHangResponse>  getAllGiamGiaKhachHang(){
        return giamGiaKhachHangRepository.findAll().stream().map(GiamGiaKhachHangResponse::new).toList();
    }

    public boolean removeCustomerFromDiscount(Long discountId, Long customerId) {
        try {
            Optional<GiamGiaKhachHang> giamGiaKhachHang = giamGiaKhachHangRepository
                    .findByPhieuGiamGiaIdAndKhachHangId(discountId, customerId);

            if (giamGiaKhachHang.isPresent()) {
                giamGiaKhachHangRepository.delete(giamGiaKhachHang.get());
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<MaGiamGiaResponse> getMaGiamGiaByKhachHang(Integer khachHangId) {
        LocalDate now = LocalDate.now();
        List<GiamGiaKhachHang> allDiscounts = giamGiaKhachHangRepository.findByKhachHangId(khachHangId);
        return allDiscounts.stream()
                .filter(ggkh -> ggkh != null && ggkh.getPhieuGiamGia() != null)
                .filter(ggkh -> ggkh.getTrangThai() != null && ggkh.getTrangThai() == true)
                .filter(ggkh -> {
                    Integer trangThai = ggkh.getPhieuGiamGia().getTrangThai();
                    return trangThai != null && trangThai == 1;
                })
                .filter(ggkh -> {
                    LocalDate ngayBatDau = ggkh.getPhieuGiamGia().getNgayBatDau();
                    return ngayBatDau != null && (ngayBatDau.isBefore(now) || ngayBatDau.isEqual(now));
                })
                .filter(ggkh -> {
                    LocalDate ngayKetThuc = ggkh.getPhieuGiamGia().getNgayKetThuc();
                    return ngayKetThuc != null && (ngayKetThuc.isAfter(now) || ngayKetThuc.isEqual(now));
                })
                .filter(ggkh -> {
                    Integer soLuongDung = ggkh.getPhieuGiamGia().getSoLuongDung();
                    return soLuongDung != null && soLuongDung > 0;
                })
                .map(MaGiamGiaResponse::new)
                .collect(Collectors.toList());
    }



}
