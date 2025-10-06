package com.example.the_autumn.service;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.PhieuGiamGia;
import com.example.the_autumn.expection.ApiException;
import com.example.the_autumn.model.request.PhieuGiamGiaRequesst;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.PhieuGiamGiaRespone;
import com.example.the_autumn.repository.GiamGiaKhachHangRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.PhieuGiamGiaRepository;
import com.example.the_autumn.util.MapperUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PhieuGiamGiaService {

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private GiamGiaKhachHangRepository giamGiaKhachHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    public List<PhieuGiamGiaRespone> getAllPhieuGiamGia() {
        return phieuGiamGiaRepository.findAll().stream().map(PhieuGiamGiaRespone::new).collect(Collectors.toList());
    }

    public PageableObject<PhieuGiamGiaRespone> phanTrang(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo,pageSize);
        Page<PhieuGiamGia> page = phieuGiamGiaRepository.findAll(pageable);
        Page<PhieuGiamGiaRespone> phieuGiamGiaRespones = page.map(PhieuGiamGiaRespone::new);
        return new PageableObject<>(phieuGiamGiaRespones);
    }

    public PhieuGiamGiaRespone getPhieuGiamGiaById(Integer id){
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id).orElseThrow();
        return new PhieuGiamGiaRespone(p);
    }

    public void delete(Integer id) {
        phieuGiamGiaRepository.findById(id).orElseThrow(
                () -> new ApiException("Khong tim thay Phieu Giam Gia", "404")
        );
        phieuGiamGiaRepository.deleteById(id);
    }

    public void add(PhieuGiamGiaRequesst phieuGiamGiaRequesst) {
        try {
            PhieuGiamGia p = MapperUtils.map(phieuGiamGiaRequesst, PhieuGiamGia.class);
            p.setTrangThai(true);
            PhieuGiamGia savedPGG = phieuGiamGiaRepository.save(p);

            if (phieuGiamGiaRequesst.getKieu() == 1 &&
                    phieuGiamGiaRequesst.getIdKhachHangs() != null &&
                    !phieuGiamGiaRequesst.getIdKhachHangs().isEmpty()) {

                List<GiamGiaKhachHang> list = new ArrayList<>();

                for (Integer khachHangId : phieuGiamGiaRequesst.getIdKhachHangs()) {
                    Optional<KhachHang> khachHangOpt = khachHangRepository.findById(khachHangId);

                    if (khachHangOpt.isPresent()) {
                        GiamGiaKhachHang pgkh = new GiamGiaKhachHang();
                        pgkh.setPhieuGiamGia(savedPGG);
                        pgkh.setKhachHang(khachHangOpt.get());
                        pgkh.setTrangThai(true);
                        list.add(pgkh);
                    }
                }
                if (!list.isEmpty()) {
                    giamGiaKhachHangRepository.saveAll(list);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi thêm phiếu giảm giá: " + e.getMessage());
        }
    }

    @Transactional
    public void update(Integer id, PhieuGiamGiaRequesst phieuGiamGiaRequesst) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + id));
        MapperUtils.mapToExisting(phieuGiamGiaRequesst, p);
        p.setId(id);
        PhieuGiamGia savedPGG = phieuGiamGiaRepository.save(p);
        giamGiaKhachHangRepository.deleteByPhieuGiamGiaId(id);
        if (phieuGiamGiaRequesst.getKieu() == 1 &&
                phieuGiamGiaRequesst.getIdKhachHangs() != null &&
                !phieuGiamGiaRequesst.getIdKhachHangs().isEmpty()) {
            List<GiamGiaKhachHang> list = new ArrayList<>();
            for (Integer khachHangId : phieuGiamGiaRequesst.getIdKhachHangs()) {
                Optional<KhachHang> khachHangOpt = khachHangRepository.findById(khachHangId);

                if (khachHangOpt.isPresent()) {
                    GiamGiaKhachHang pgkh = new GiamGiaKhachHang();
                    pgkh.setPhieuGiamGia(savedPGG);
                    pgkh.setKhachHang(khachHangOpt.get());
                    list.add(pgkh);
                    pgkh.setTrangThai(true);
                }
            }
            if (!list.isEmpty()) {
                giamGiaKhachHangRepository.saveAll(list);
            }
        }
    }

    public void updateTrangThai(Integer id, Boolean trangThai){
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id).orElseThrow(
                () -> new ApiException("Không tìm thấy Phiếu Giảm Giá", "404")
        );
        p.setTrangThai(trangThai);
        phieuGiamGiaRepository.save(p);
    }

    public List<PhieuGiamGiaRespone> searchAllPhieuGiamGia(String keyword) {
        List<PhieuGiamGia> list = phieuGiamGiaRepository
                .findByMaGiamGiaContainingIgnoreCaseOrTenChuongTrinhContainingIgnoreCase(keyword, keyword);
        return list.stream().map(PhieuGiamGiaRespone::new).collect(Collectors.toList());
    }

    public List<PhieuGiamGiaRespone> searchTheoNgay(Date ngayBatDau, Date ngayKetThuc) {
        List<PhieuGiamGia> list = phieuGiamGiaRepository
                .findByNgayBatDauGreaterThanEqualAndNgayKetThucLessThanEqual(ngayBatDau, ngayKetThuc);
        return list.stream().map(PhieuGiamGiaRespone::new).collect(Collectors.toList());
    }

    public List<KhachHang> getKhachHangByPhieuGiamGiaId(Integer pggId) {
        return giamGiaKhachHangRepository.findKhachHangByPhieuGiamGiaId(pggId);
    }

    public List<Integer> getKhachHangIdsByPhieuGiamGiaId(Integer pggId) {
        return giamGiaKhachHangRepository.findKhachHangIdsByPhieuGiamGiaId(pggId);
    }
}
