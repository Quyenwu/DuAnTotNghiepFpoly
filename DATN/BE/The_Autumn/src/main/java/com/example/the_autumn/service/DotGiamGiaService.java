package com.example.the_autumn.service;


import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import com.example.the_autumn.model.response.ChiTietSanPhamDTO;
import com.example.the_autumn.model.response.DotGiamGiaDTO;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import com.example.the_autumn.repository.DotGiamGiaChiTietRepository;
import com.example.the_autumn.repository.DotGiamGiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
@Service
public class DotGiamGiaService {

    private final DotGiamGiaRepository dotGiamGiaRepository;
    private final DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;

    public DotGiamGiaService(
            DotGiamGiaRepository dotGiamGiaRepository,
            DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository,
            ChiTietSanPhamRepository chiTietSanPhamRepository
    ) {
        this.dotGiamGiaRepository = dotGiamGiaRepository;
        this.dotGiamGiaChiTietRepository = dotGiamGiaChiTietRepository;
        this.chiTietSanPhamRepository = chiTietSanPhamRepository;
    }

    // === Lấy đợt giảm giá theo trang ===
    public List<DotGiamGiaDTO> list() {
        return dotGiamGiaRepository.findAll() .stream() .map(d -> new DotGiamGiaDTO( d.getId(), d.getMaGiamGia(), d.getTenDot(), d.getLoaiGiamGia(), d.getGiaTriGiam(), d.getGiaTriToiThieu(), d.getNgayBatDau(), d.getNgayKetThuc(), d.getTrangThai() )) .toList(); }
    public Page<DotGiamGiaDTO> findAllDTO(Pageable pageable) {

        Page<DotGiamGia> page = dotGiamGiaRepository.findAll(pageable);
        List<DotGiamGiaDTO> dtoList = page.stream()
                .map(d -> new DotGiamGiaDTO(
                        d.getId(),
                        d.getMaGiamGia(),
                        d.getTenDot(),
                        d.getLoaiGiamGia(),
                        d.getGiaTriGiam(),
                        d.getGiaTriToiThieu(),
                        d.getNgayBatDau(),
                        d.getNgayKetThuc(),
                        d.getTrangThai()
                ))
                .toList();
        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
    }
    public Page<ChiTietSanPhamDTO> findAllProductDTO(String q, Pageable pageable) {
        Page<ChiTietSanPham> page = (q == null || q.isBlank())
                ? chiTietSanPhamRepository.findAll(pageable)
                : chiTietSanPhamRepository.search(q, pageable);

        return page.map(p -> new ChiTietSanPhamDTO(
                p.getId(),
                p.getSanPham().getMaSanPham(),
                p.getSanPham().getTenSanPham(),
                p.getGiaBan(),
                (p.getAnhs() != null && !p.getAnhs().isEmpty())
                        ? p.getAnhs().get(0).getDuongDanAnh()
                        : null,
                p.getSanPham().getNhaSanXuat().getTenNhaSanXuat(),
                p.getSoLuongTon()
        ));
    }
    // === Tìm kiếm với phân trang ===
//    public Page<DotGiamGiaDTO> searchDTO(String q, Pageable pageable) {
//        Page<DotGiamGia> page;
//        if (q == null || q.isBlank()) {
//            page = dotGiamGiaRepository.findAll(pageable);
//        } else {
//            // Giả sử repository có method searchByName hoặc searchByMa
//            page = dotGiamGiaRepository.searchByTenDotOrMaGiamGia(q, pageable);
//        }
//        List<DotGiamGiaDTO> dtoList = page.stream()
//                .map(d -> new DotGiamGiaDTO(
//                        d.getId(),
//                        d.getMaGiamGia(),
//                        d.getTenDot(),
//                        d.getLoaiGiamGia(),
//                        d.getGiaTriGiam(),
//                        d.getGiaTriToiThieu(),
//                        d.getNgayBatDau(),
//                        d.getNgayKetThuc(),
//                        d.getTrangThai()
//                ))
//                .toList();
//        return new PageImpl<>(dtoList, pageable, page.getTotalElements());
//    }

    // === Các phương thức còn lại giữ nguyên ===
    public Optional<DotGiamGia> findById(Integer id) { return dotGiamGiaRepository.findById(id); }
    public Optional<DotGiamGia> findByMa(String ma) { return dotGiamGiaRepository.findByMaGiamGia(ma); }
    public DotGiamGia save(DotGiamGia entity) { return dotGiamGiaRepository.save(entity); }
    public void deleteById(Integer id) { dotGiamGiaRepository.deleteById(id); }
    @Transactional
    public DotGiamGia saveWithDetails(DotGiamGia entity, List<Integer> ctspIds) {
//        // === 1️⃣ Tạo mã tự động nếu chưa có ===
//        if (entity.getMaGiamGia() == null || entity.getMaGiamGia().isBlank()) {
//            entity.setMaGiamGia(generateUniqueCode());
//        }
//
//        // === 2️⃣ Kiểm tra trùng mã (chỉ khi thêm mới hoặc đổi mã) ===
//        Optional<DotGiamGia> existing = dotGiamGiaRepository.findByMaGiamGia(entity.getMaGiamGia());
//        if (existing.isPresent() && (entity.getId() == null || !existing.get().getId().equals(entity.getId()))) {
//            throw new IllegalArgumentException("❌ Mã giảm giá '" + entity.getMaGiamGia() + "' đã tồn tại.");
//        }

        // === 3️⃣ Lưu đợt giảm giá ===
        DotGiamGia saved = dotGiamGiaRepository.save(entity);

        // === 4️⃣ Xóa chi tiết cũ (nếu có) ===
        dotGiamGiaChiTietRepository.deleteByDotGiamGia(saved);

        // === 5️⃣ Lưu chi tiết mới (nếu có) ===
        if (ctspIds != null && !ctspIds.isEmpty()) {
            List<ChiTietSanPham> variants = chiTietSanPhamRepository.findAllById(ctspIds);

            List<DotGiamGiaChiTiet> toSave = variants.stream().map(v -> {
                DotGiamGiaChiTiet detail = new DotGiamGiaChiTiet();
                detail.setDotGiamGia(saved);
                detail.setChiTietSanPham(v);
                detail.setDoUuTien(0);
                return detail;
            }).collect(Collectors.toList());

            dotGiamGiaChiTietRepository.saveAll(toSave);
        }

        return saved;
    }


    public List<DotGiamGiaChiTiet> findDetailsByDot(DotGiamGia dot) {
        if (dot == null) {
            // Tránh findAll() nếu dot == null, có thể chỉ trả về danh sách đang active
            return dotGiamGiaChiTietRepository.findByDotGiamGiaTrangThai(true);
        }
        return dotGiamGiaChiTietRepository.findByDotGiamGia(dot);
    }


    private String generateUniqueCode() {
        String base = "DG-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
        String code = base;
        int suffix = 1;
        while (dotGiamGiaRepository.findByMaGiamGia(code).isPresent()) {
            code = base + "-" + (suffix++);
        }
        return code;
    }
}


