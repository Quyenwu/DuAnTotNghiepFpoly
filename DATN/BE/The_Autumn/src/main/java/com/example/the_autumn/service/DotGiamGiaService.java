package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.DotGiamGia;
import com.example.the_autumn.entity.DotGiamGiaChiTiet;
import com.example.the_autumn.expection.ApiException;
import com.example.the_autumn.model.request.DotGiamGiaRequest;
import com.example.the_autumn.model.response.DotGiamGiaResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import com.example.the_autumn.repository.DotGiamGiaChiTietRepository;
import com.example.the_autumn.repository.DotGiamGiaRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Predicate;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DotGiamGiaService {

    @Autowired
   private DotGiamGiaRepository dotGiamGiaRepository;

    @Autowired
    private DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(PhieuGiamGiaService.class);

    public List<DotGiamGiaResponse> getAllDotGiamGia() {
        return dotGiamGiaRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(DotGiamGiaResponse::new)
                .collect(Collectors.toList());
    }

    public PageableObject<DotGiamGiaResponse> phanTrang(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<DotGiamGia> page = dotGiamGiaRepository.findAll(pageable);
        Page<DotGiamGiaResponse> dotGiamGiaResponses = page.map(DotGiamGiaResponse::new);
        return new PageableObject<>(dotGiamGiaResponses);
    }

    public DotGiamGiaResponse getDotGiamGiaById(Integer id) {
        DotGiamGia d = dotGiamGiaRepository.findById(id).orElseThrow();
        return new DotGiamGiaResponse(d);
    }

    public void delete(Integer id) {
        dotGiamGiaRepository.findById(id).orElseThrow(
                () -> new ApiException("Khong tim thay Phieu Giam Gia", "404")
        );
        dotGiamGiaRepository.deleteById(id);
    }

    @Transactional
    public void add(DotGiamGiaRequest req) {
        DotGiamGia dot = new DotGiamGia();
        dot.setMaGiamGia(req.getMaGiamGia());
        dot.setTenDot(req.getTenDot());
        dot.setLoaiGiamGia(req.getLoaiGiamGia());
        dot.setGiaTriGiam(req.getGiaTriGiam());
        dot.setGiaTriToiThieu(req.getGiaTriToiThieu());
        dot.setNgayBatDau(req.getNgayBatDau());
        dot.setNgayKetThuc(req.getNgayKetThuc());

        // ✅ Xác định trạng thái theo ngày
        LocalDate now = LocalDate.now();
        if (now.isBefore(dot.getNgayBatDau())) {
            dot.setTrangThai(0); // sắp diễn ra
        } else if (!now.isAfter(dot.getNgayKetThuc())) {
            dot.setTrangThai(1); // đang diễn ra
        } else {
            dot.setTrangThai(2); // kết thúc
        }

        DotGiamGia savedDot = dotGiamGiaRepository.saveAndFlush(dot);
        em.refresh(savedDot);

        if (req.getCtspIds() == null || req.getCtspIds().isEmpty()) {
            throw new ApiException("Danh sách sản phẩm chi tiết không được để trống!", "400");
        }

        List<DotGiamGiaChiTiet> chiTietList = new ArrayList<>();
        int doUuTien = 1;

        for (Integer idCtsp : req.getCtspIds()) {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idCtsp)
                    .orElseThrow(() -> new ApiException("Không tìm thấy chi tiết sản phẩm ID: " + idCtsp, "404"));

            DotGiamGiaChiTiet chiTiet = new DotGiamGiaChiTiet();
            chiTiet.setDotGiamGia(savedDot);
            chiTiet.setChiTietSanPham(ctsp);
            chiTiet.setDoUuTien(doUuTien++);
            chiTietList.add(chiTiet);
        }

        dotGiamGiaChiTietRepository.saveAll(chiTietList);
        logger.info("✅ Thêm đợt giảm giá thành công ID={} với {} sản phẩm", savedDot.getId(), chiTietList.size());
    }


    @Transactional
    public void update(Integer id, DotGiamGiaRequest req) {
        DotGiamGia dot = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Không tìm thấy đợt giảm giá với ID: " + id, "404"));

        dot.setMaGiamGia(req.getMaGiamGia());
        dot.setTenDot(req.getTenDot());
        dot.setLoaiGiamGia(req.getLoaiGiamGia());
        dot.setGiaTriGiam(req.getGiaTriGiam());
        dot.setGiaTriToiThieu(req.getGiaTriToiThieu());
        dot.setNgayBatDau(req.getNgayBatDau());
        dot.setNgayKetThuc(req.getNgayKetThuc());

        LocalDate now = LocalDate.now();
        if (now.isBefore(dot.getNgayBatDau())) {
            dot.setTrangThai(0);
        } else if (!now.isAfter(dot.getNgayKetThuc())) {
            dot.setTrangThai(1);
        } else {
            dot.setTrangThai(2);
        }

        dot = dotGiamGiaRepository.save(dot);

        List<Integer> newCtspIds = req.getCtspIds();
        if (newCtspIds == null || newCtspIds.isEmpty()) {
            throw new ApiException("Danh sách sản phẩm chi tiết không được để trống!", "400");
        }

        List<DotGiamGiaChiTiet> oldChiTietList = dotGiamGiaChiTietRepository.findByDotGiamGia(dot);
        Set<Integer> oldIds = oldChiTietList.stream()
                .map(c -> c.getChiTietSanPham().getId())
                .collect(Collectors.toSet());

        for (DotGiamGiaChiTiet old : oldChiTietList) {
            if (!newCtspIds.contains(old.getChiTietSanPham().getId())) {
                dotGiamGiaChiTietRepository.delete(old);
            }
        }

        int doUuTien = 1;
        for (Integer idCtsp : newCtspIds) {
            if (!oldIds.contains(idCtsp)) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idCtsp)
                        .orElseThrow(() -> new ApiException("Không tìm thấy sản phẩm chi tiết ID: " + idCtsp, "404"));
                DotGiamGiaChiTiet chiTiet = new DotGiamGiaChiTiet();
                chiTiet.setDotGiamGia(dot);
                chiTiet.setChiTietSanPham(ctsp);
                chiTiet.setDoUuTien(doUuTien++);
                dotGiamGiaChiTietRepository.save(chiTiet);
            }
        }
    }

    public void updateTrangThai(Integer id, Integer trangThai) {
        DotGiamGia dot = dotGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Không tìm thấy đợt giảm giá", "404"));

        LocalDate now = LocalDate.now();
        if (now.isBefore(dot.getNgayBatDau())) {
            dot.setTrangThai(0);
        } else if (!now.isAfter(dot.getNgayKetThuc())) {
            dot.setTrangThai(1);
        } else {
            dot.setTrangThai(2);
        }

        if (trangThai != null) {
            dot.setTrangThai(trangThai);
        }

        dotGiamGiaRepository.save(dot);
    }


    public List<DotGiamGiaResponse> searchDotGiamGia(
            String keyword,
            LocalDate tuNgay,
            LocalDate denNgay,
            Boolean loaiGiamGia,
            Integer trangThai
    ) {
        Specification<DotGiamGia> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("maGiamGia")), kw),
                        cb.like(cb.lower(root.get("tenDot")), kw)
                ));
            }

            if (loaiGiamGia != null) {
                predicates.add(cb.equal(root.get("loaiGiamGia"), loaiGiamGia));
            }

            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            if (tuNgay != null && denNgay != null) {
                predicates.add(cb.between(root.get("ngayBatDau"), tuNgay, denNgay));
            } else if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayBatDau"), tuNgay));
            } else if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayKetThuc"), denNgay));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return dotGiamGiaRepository.findAll(spec)
                .stream()
                .map(DotGiamGiaResponse::new)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSanPhamByDot(Integer idDot) {
        DotGiamGia dot = dotGiamGiaRepository.findById(idDot)
                .orElseThrow(() -> new ApiException("Không tìm thấy đợt giảm giá", "404"));

        List<DotGiamGiaChiTiet> chiTietList = dotGiamGiaChiTietRepository.findByDotGiamGia(dot);

        Map<Integer, List<ChiTietSanPham>> grouped = chiTietList.stream()
                .map(DotGiamGiaChiTiet::getChiTietSanPham)
                .collect(Collectors.groupingBy(ct -> ct.getSanPham().getId()));

        List<Map<String, Object>> result = new ArrayList<>();
        for (Map.Entry<Integer, List<ChiTietSanPham>> entry : grouped.entrySet()) {
            Map<String, Object> sanPhamData = new HashMap<>();
            sanPhamData.put("sanPhamId", entry.getKey());
            sanPhamData.put("tenSanPham", entry.getValue().get(0).getSanPham().getTenSanPham());
            sanPhamData.put("chiTietIds", entry.getValue().stream()
                    .map(ChiTietSanPham::getId)
                    .collect(Collectors.toList()));
            result.add(sanPhamData);
        }

        return result;
    }
}
