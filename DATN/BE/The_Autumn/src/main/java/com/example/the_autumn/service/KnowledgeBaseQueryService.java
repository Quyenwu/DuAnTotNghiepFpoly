package com.example.the_autumn.service;

import com.example.the_autumn.dto.ListSanPhamResponse;
import com.example.the_autumn.model.response.*;
import com.example.the_autumn.repository.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    // 🚨 SỬA METHOD NÀY: Thêm sắp xếp theo sản phẩm mới nhất
    public List<ListSanPhamResponse> getSanPhamData() {
        // Tạo pageable để lấy sản phẩm mới nhất
        Pageable pageable = PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "ngayTao"));

        return sanPhamRepo.findByTrangThai(true, pageable).stream()
                .map(ListSanPhamResponse::new)
                .toList();
    }
    // 🚨 THÊM: Lấy sản phẩm theo từ khóa
    public List<ListSanPhamResponse> searchSanPham(String keyword) {
        return sanPhamRepo.findByTrangThai(true).stream()
                .filter(sp -> sp.getTenSanPham().toLowerCase().contains(keyword.toLowerCase()))
                .map(ListSanPhamResponse::new)
                .limit(10)
                .toList();
    }

    // 🚨 THÊM METHOD MỚI: Lấy sản phẩm mới thêm gần đây
    public List<ListSanPhamResponse> getNewProducts() {
        // Lấy sản phẩm được tạo trong 7 ngày gần đây
        LocalDate sevenDaysAgo = LocalDate.now().minusDays(7);

        // Nếu repository không hỗ trợ findByNgayTaoAfter, dùng cách khác
        return sanPhamRepo.findByTrangThai(true).stream()
                .filter(sp -> {
                    // Kiểm tra ngày tạo, lưu ý: ngayTao là Date, cần convert
                    if (sp.getNgayTao() == null) return false;
                    LocalDate createdDate = sp.getNgayTao().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDate();
                    return !createdDate.isBefore(sevenDaysAgo);
                })
                .sorted((sp1, sp2) -> sp2.getNgayTao().compareTo(sp1.getNgayTao()))
                .limit(10)
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