package com.example.the_autumn.service;

import com.example.the_autumn.dto.ListSanPhamResponse;
import com.example.the_autumn.model.response.*;
import com.example.the_autumn.repository.*;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class KnowledgeBaseQueryService {

    private final SanPhamRepository sanPhamRepo;
    private final PhieuGiamGiaRepository phieuGiamRepo;
    private final DotGiamGiaRepository dotGiamRepo;
    private final KhachHangRepository khachHangRepo;
    private final DiaChiRepository diaChiRepo;

    public KnowledgeBaseQueryService(
            SanPhamRepository sanPhamRepo,
            PhieuGiamGiaRepository phieuGiamRepo,
            DotGiamGiaRepository dotGiamRepo,
            KhachHangRepository khachHangRepo,
            DiaChiRepository diaChiRepo
    ) {
        this.sanPhamRepo = sanPhamRepo;
        this.phieuGiamRepo = phieuGiamRepo;
        this.dotGiamRepo = dotGiamRepo;
        this.khachHangRepo = khachHangRepo;
        this.diaChiRepo = diaChiRepo;
    }

    public List<ListSanPhamResponse> getSanPhamData() {
        return sanPhamRepo.findByTrangThai(true).stream()
                .map(ListSanPhamResponse::new)
                .toList();
    }

    public List<PhieuGiamGiaRespone> getActivePhieuGiam(LocalDate today) {
        return phieuGiamRepo.findActive(today).stream()
                .map(PhieuGiamGiaRespone::new)
                .toList();
    }

    public List<DotGiamGiaResponse> getActiveDotGiam(LocalDate today) {
        return dotGiamRepo.findActive(today).stream()
                .map(DotGiamGiaResponse::new)
                .toList();
    }

    public List<KhachHangResponse> getActiveKhachHang() {
        return khachHangRepo.findByTrangThai(true).stream()
                .map(KhachHangResponse::new)
                .toList();
    }

    public List<DiaChiResponse> getActiveDiaChi() {
        return diaChiRepo.findByTrangThai(true).stream()
                .map(DiaChiResponse::new)
                .toList();
    }
}
