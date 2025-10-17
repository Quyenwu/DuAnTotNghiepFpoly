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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class PhieuGiamGiaService {

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private GiamGiaKhachHangRepository giamGiaKhachHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private EmailService emailService;

    @PersistenceContext
    private EntityManager em;

    private static final Logger logger = LoggerFactory.getLogger(PhieuGiamGiaService.class);

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

    @Transactional
    public void add(PhieuGiamGiaRequesst req) {
        PhieuGiamGia p = MapperUtils.map(req, PhieuGiamGia.class);
        p.setTrangThai(req.getTrangThai() != null ? req.getTrangThai() : true);

        if (req.getKieu() == 1 && req.getIdKhachHangs() != null) {
            p.setSoLuongDung(req.getIdKhachHangs().size());
        }

        PhieuGiamGia savedPGG = phieuGiamGiaRepository.saveAndFlush(p);
        em.refresh(savedPGG);
        logger.info("✅ Saved discount ID={} & code={}", savedPGG.getId(), savedPGG.getMaGiamGia());
        if (req.getKieu() == 1 && req.getIdKhachHangs() != null && !req.getIdKhachHangs().isEmpty()) {
            List<GiamGiaKhachHang> list = new ArrayList<>();
            for (Integer khachHangId : req.getIdKhachHangs()) {
                khachHangRepository.findById(khachHangId).ifPresent(kh -> {
                    GiamGiaKhachHang link = new GiamGiaKhachHang();
                    link.setPhieuGiamGia(savedPGG);
                    link.setKhachHang(kh);
                    link.setTrangThai(true);
                    list.add(link);
                    if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                        emailService.sendDiscountEmail(kh.getEmail(), savedPGG);
                    }
                });
            }
            if (!list.isEmpty()) {
                giamGiaKhachHangRepository.saveAll(list);
                logger.info("Saved {} customer-discount relations", list.size());
            }
        } else {
            logger.info("Public discount - no emails required");
        }
    }


    @Transactional
    public void update(Integer id, PhieuGiamGiaRequesst phieuGiamGiaRequesst) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá với ID: " + id));
        List<Integer> existingCustomerIds = giamGiaKhachHangRepository
                .findByPhieuGiamGia_Id(id)
                .stream()
                .map(ggkh -> ggkh.getKhachHang().getId())
                .collect(Collectors.toList());
        MapperUtils.mapToExisting(phieuGiamGiaRequesst, p);
        p.setId(id);
        PhieuGiamGia savedPGG = phieuGiamGiaRepository.save(p);
        giamGiaKhachHangRepository.deleteByPhieuGiamGiaId(id);
        AtomicInteger emailCount = new AtomicInteger(0);
        AtomicInteger errorCount = new AtomicInteger(0);
        if (phieuGiamGiaRequesst.getKieu() == 1 &&
                phieuGiamGiaRequesst.getIdKhachHangs() != null &&
                !phieuGiamGiaRequesst.getIdKhachHangs().isEmpty()) {
            List<GiamGiaKhachHang> list = new ArrayList<>();
            for (Integer khachHangId : phieuGiamGiaRequesst.getIdKhachHangs()) {
                Optional<KhachHang> khachHangOpt = khachHangRepository.findById(khachHangId);
                if (khachHangOpt.isPresent()) {
                    KhachHang kh = khachHangOpt.get();
                    GiamGiaKhachHang pgkh = new GiamGiaKhachHang();
                    pgkh.setPhieuGiamGia(savedPGG);
                    pgkh.setKhachHang(kh);
                    list.add(pgkh);
                    pgkh.setTrangThai(true);
                    if (!existingCustomerIds.contains(khachHangId)) {
                        if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                            logger.info("Processing update email for new customer {}: {}", kh.getId(), kh.getEmail());

                            try {
                                emailService.sendDiscountEmail(kh.getEmail(), savedPGG);
                                emailCount.incrementAndGet();
                                logger.info("Update email sent successfully to: {}", kh.getEmail());
                            } catch (Exception e) {
                                errorCount.incrementAndGet();
                                logger.error("Failed to send update email to {}: {}", kh.getEmail(), e.getMessage());
                            }
                        }
                    }
                }
            }
            if (!list.isEmpty()) {
                giamGiaKhachHangRepository.saveAll(list);
                logger.info("Updated {} customer-discount relationships", list.size());
            }
            logger.info("Update email sending summary: {} sent, {} failed",
                    emailCount.get(), errorCount.get());
            List<Integer> newCustomerIds = phieuGiamGiaRequesst.getIdKhachHangs()
                    .stream()
                    .filter(customerId -> !existingCustomerIds.contains(customerId))
                    .collect(Collectors.toList());
            logger.info("Customer update summary: {} total, {} new customers",
                    phieuGiamGiaRequesst.getIdKhachHangs().size(), newCustomerIds.size());
        }
    }

    public List<Integer> getKhachHangTheoPhieu(Integer phieuId) {
        return giamGiaKhachHangRepository.findByPhieuGiamGia_Id(phieuId)
                .stream()
                .map(g -> g.getKhachHang().getId())
                .collect(Collectors.toList());
    }


    public void updateTrangThai(Integer id, Boolean trangThai) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Không tìm thấy Phiếu Giảm Giá", "404"));
        LocalDate now = LocalDate.now();
        if (Boolean.TRUE.equals(trangThai) && p.getNgayKetThuc().isBefore(now)) {
            throw new ApiException("Phiếu này đã hết hạn, không thể kích hoạt lại!", "400");
        }
        if (p.getNgayBatDau().isAfter(now) && Boolean.TRUE.equals(trangThai)) {
            p.setTrangThai(true);
        }
        else if (p.getNgayKetThuc().isBefore(now)) {
            p.setTrangThai(false);
        } else {
            p.setTrangThai(trangThai);
        }
        phieuGiamGiaRepository.save(p);
    }

    public List<PhieuGiamGiaRespone> searchPhieuGiamGia(
            String keyword,
            LocalDate tuNgay,
            LocalDate denNgay,
            Integer kieu,
            Boolean loaiGiamGia,
            Boolean trangThai
    ) {
        Specification<PhieuGiamGia> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                List<Predicate> keywordPredicates = new ArrayList<>();
                keywordPredicates.add(cb.like(cb.lower(root.get("maGiamGia")), kw));
                keywordPredicates.add(cb.like(cb.lower(root.get("tenChuongTrinh")), kw));

                predicates.add(cb.or(keywordPredicates.toArray(new Predicate[0])));
            }
            if (kieu != null) {
                predicates.add(cb.equal(root.get("kieu"), kieu));
            }
            if (loaiGiamGia != null) {
                predicates.add(cb.equal(root.get("loaiGiamGia"), loaiGiamGia));
            }
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }
            if (tuNgay != null && denNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayBatDau"), tuNgay));
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayKetThuc"), denNgay));
            } else if (tuNgay != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("ngayBatDau"), tuNgay));
            } else if (denNgay != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("ngayKetThuc"), denNgay));
            }
            return predicates.isEmpty()
                    ? cb.conjunction()
                    : cb.and(predicates.toArray(new Predicate[0]));
        };
        return phieuGiamGiaRepository.findAll(spec)
                .stream()
                .map(PhieuGiamGiaRespone::new)
                .collect(Collectors.toList());
    }
}
