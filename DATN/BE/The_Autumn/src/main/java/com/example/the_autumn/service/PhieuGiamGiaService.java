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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
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
        return phieuGiamGiaRepository.findAll()
                .stream()
                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(PhieuGiamGiaRespone::new)
                .collect(Collectors.toList());
    }

    public PageableObject<PhieuGiamGiaRespone> phanTrang(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<PhieuGiamGia> page = phieuGiamGiaRepository.findAll(pageable);
        Page<PhieuGiamGiaRespone> phieuGiamGiaRespones = page.map(PhieuGiamGiaRespone::new);
        return new PageableObject<>(phieuGiamGiaRespones);
    }

    public PhieuGiamGiaRespone getPhieuGiamGiaById(Integer id) {
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
        p.setTrangThai(req.getTrangThai() != null ? req.getTrangThai() : 1);

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
    public void update(Integer id, PhieuGiamGiaRequesst req) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá ID: " + id));

        BigDecimal oldGiaTri = p.getGiaTriGiamGia();
        Boolean oldLoaiGiam = p.getLoaiGiamGia();
        LocalDate oldNgayBatDau = p.getNgayBatDau();
        LocalDate oldNgayKetThuc = p.getNgayKetThuc();

        // Quyết định dựa trên giá trị trong request (chưa map vào entity)
        boolean isGiaTriChanged = oldGiaTri.compareTo(req.getGiaTriGiamGia()) != 0;
        boolean isLoaiChanged = !oldLoaiGiam.equals(req.getLoaiGiamGia());

        // Kiểm tra thay đổi ngày (LocalDate)
        boolean isNgayBatDauChanged = !oldNgayBatDau.equals(req.getNgayBatDau());
        boolean isNgayKetThucChanged = !oldNgayKetThuc.equals(req.getNgayKetThuc());
        boolean isNgayChanged = isNgayBatDauChanged || isNgayKetThucChanged;

        // Load existing customer links trước
        List<GiamGiaKhachHang> existingList = giamGiaKhachHangRepository.findByPhieuGiamGia_Id(id);
        Map<Integer, GiamGiaKhachHang> existingMap = existingList.stream()
                .collect(Collectors.toMap(gg -> gg.getKhachHang().getId(), gg -> gg));

        // ----------------------------
        // TRƯỜNG HỢP 1: giá trị thay đổi -> tạo bản ghi mới
        // ----------------------------
        if (isGiaTriChanged) {
            // Tạo entity mới từ request
            PhieuGiamGia newPGG = MapperUtils.map(req, PhieuGiamGia.class);
            newPGG.setId(null);
            newPGG.setNgayTao(new Date());

            PhieuGiamGia savedNew = phieuGiamGiaRepository.saveAndFlush(newPGG);
            em.refresh(savedNew);
            logger.info("🆕 Tạo phiếu mới ID={} do thay đổi giá trị giảm", savedNew.getId());

            List<GiamGiaKhachHang> newLinks = new ArrayList<>();

            // Xử lý khách hàng theo request
            if (req.getKieu() == 1 && req.getIdKhachHangs() != null && !req.getIdKhachHangs().isEmpty()) {
                Set<Integer> requestedIds = new HashSet<>(req.getIdKhachHangs());

                // 1. Khách hàng có trong cả cũ và mới: Gửi email UPDATE
                for (Integer khId : requestedIds) {
                    if (existingMap.containsKey(khId)) {
                        KhachHang kh = existingMap.get(khId).getKhachHang();

                        GiamGiaKhachHang clone = new GiamGiaKhachHang();
                        clone.setKhachHang(kh);
                        clone.setPhieuGiamGia(savedNew);
                        clone.setTrangThai(true);
                        newLinks.add(clone);

                        if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                            // Thay đổi giá trị: gửi email UPDATE
                            emailService.sendDiscountUpdateEmail(kh.getEmail(), savedNew);
                            logger.info("📧 Gửi email CẬP NHẬT cho {} (thay đổi giá trị)", kh.getEmail());
                        }
                    }
                }

                // 2. Khách hàng mới (chỉ có trong request): Gửi email MỚI
                for (Integer khId : requestedIds) {
                    if (!existingMap.containsKey(khId)) {
                        KhachHang kh = khachHangRepository.findById(khId)
                                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại: " + khId));

                        GiamGiaKhachHang gg = new GiamGiaKhachHang();
                        gg.setPhieuGiamGia(savedNew);
                        gg.setKhachHang(kh);
                        gg.setTrangThai(true);
                        newLinks.add(gg);

                        if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                            emailService.sendDiscountEmail(kh.getEmail(), savedNew);
                            logger.info("📧 Gửi email MỚI cho {} (khách hàng mới)", kh.getEmail());
                        }
                    }
                }

                // 3. Khách hàng bị xóa (có trong cũ, không có trong request): Gửi email HỦY
                existingList.stream()
                        .filter(gg -> !requestedIds.contains(gg.getKhachHang().getId()))
                        .forEach(gg -> {
                            if (gg.getKhachHang().getEmail() != null && !gg.getKhachHang().getEmail().isBlank()) {
                                emailService.sendDiscountCancelEmail(gg.getKhachHang().getEmail(), p);
                                logger.info("📧 Gửi email HỦY cho {} (bị xóa khỏi phiếu)", gg.getKhachHang().getEmail());
                            }
                        });
            }

            if (!newLinks.isEmpty()) {
                giamGiaKhachHangRepository.saveAll(newLinks);
            }

            // Kết thúc: không chạm vào bản cũ
            return;
        }

        // ----------------------------
        // TRƯỜNG HỢP 2: giá trị KHÔNG đổi -> chỉ update bản cũ
        // ----------------------------
        // Map các field từ req vào entity cũ
        MapperUtils.mapToExisting(req, p);
        p.setId(id);
        PhieuGiamGia saved = phieuGiamGiaRepository.saveAndFlush(p);
        em.refresh(saved);
        logger.info("✔ Update phiếu ID={} (không tạo bản ghi mới)", id);

        // Xóa liên kết cũ
        giamGiaKhachHangRepository.deleteByPhieuGiamGiaId(id);

        if (req.getKieu() == 1 && req.getIdKhachHangs() != null && !req.getIdKhachHangs().isEmpty()) {
            List<GiamGiaKhachHang> toSave = new ArrayList<>();
            Set<Integer> requestedIds = new HashSet<>(req.getIdKhachHangs());

            // Xử lý logic email cho từng khách hàng
            for (Integer khId : requestedIds) {
                KhachHang kh = khachHangRepository.findById(khId)
                        .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại: " + khId));

                GiamGiaKhachHang gg = new GiamGiaKhachHang();
                gg.setPhieuGiamGia(saved);
                gg.setKhachHang(kh);
                gg.setTrangThai(true);
                toSave.add(gg);

                if (kh.getEmail() != null && !kh.getEmail().isBlank()) {
                    if (existingMap.containsKey(khId)) {
                        // Khách hàng đã có: kiểm tra xem có thay đổi gì quan trọng không
                        boolean hasImportantChange = isLoaiChanged || isNgayChanged;

                        if (hasImportantChange) {
                            // Có thay đổi quan trọng: gửi email UPDATE
                            emailService.sendDiscountUpdateEmail(kh.getEmail(), saved);
                            if (isLoaiChanged && isNgayChanged) {
                                logger.info("📧 Gửi email CẬP NHẬT cho {} (thay đổi loại giảm và ngày)", kh.getEmail());
                            } else if (isLoaiChanged) {
                                logger.info("📧 Gửi email CẬP NHẬT cho {} (thay đổi loại giảm)", kh.getEmail());
                            } else if (isNgayChanged) {
                                logger.info("📧 Gửi email CẬP NHẬT cho {} (thay đổi ngày)", kh.getEmail());
                            }
                        } else {
                            // Chỉ thay đổi thông tin khác (tên, mô tả...), không gửi email
                            logger.info("ℹ️ Không gửi email cho {} (chỉ thay đổi thông tin khác)", kh.getEmail());
                        }
                    } else {
                        // Khách hàng mới: gửi email MỚI
                        emailService.sendDiscountEmail(kh.getEmail(), saved);
                        logger.info("📧 Gửi email MỚI cho {} (khách hàng mới)", kh.getEmail());
                    }
                }
            }

            // Khách hàng bị xóa: gửi email HỦY
            existingList.stream()
                    .filter(gg -> !requestedIds.contains(gg.getKhachHang().getId()))
                    .forEach(gg -> {
                        if (gg.getKhachHang().getEmail() != null && !gg.getKhachHang().getEmail().isBlank()) {
                            emailService.sendDiscountCancelEmail(gg.getKhachHang().getEmail(), saved);
                            logger.info("📧 Gửi email HỦY cho {} (bị xóa khỏi phiếu)", gg.getKhachHang().getEmail());
                        }
                    });

            if (!toSave.isEmpty()) {
                giamGiaKhachHangRepository.saveAll(toSave);
            }
        } else {
            logger.info("Phiếu công khai — không gắn khách hàng cụ thể.");
        }
    }


    public List<Integer> getKhachHangTheoPhieu(Integer phieuId) {
        return giamGiaKhachHangRepository.findByPhieuGiamGia_Id(phieuId)
                .stream()
                .map(g -> g.getKhachHang().getId())
                .collect(Collectors.toList());
    }


    public void updateTrangThai(Integer id, Integer trangThai) {
        PhieuGiamGia p = phieuGiamGiaRepository.findById(id)
                .orElseThrow(() -> new ApiException("Không tìm thấy Phiếu Giảm Giá", "404"));
        LocalDate now = LocalDate.now();

        if (trangThai == null) {
            if (p.getTrangThai() != null && p.getTrangThai() == 2) {
                return;
            }

            if (p.getNgayBatDau().isAfter(now)) {
                p.setTrangThai(0);
            } else if ((p.getNgayBatDau().isBefore(now) || p.getNgayBatDau().isEqual(now))
                    && (p.getNgayKetThuc().isAfter(now) || p.getNgayKetThuc().isEqual(now))) {
                p.setTrangThai(1);
            } else if (p.getNgayKetThuc().isBefore(now)) {
                p.setTrangThai(2);
            }
        } else {
            if (trangThai == 1 && p.getNgayKetThuc().isBefore(now)) {
                throw new ApiException("Phiếu này đã hết hạn, không thể kích hoạt lại!", "400");
            }
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
            Integer trangThai
    ) {
        Specification<PhieuGiamGia> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isBlank()) {
                String kw = "%" + keyword.toLowerCase() + "%";
                List<Predicate> keywordPredicates = new ArrayList<>();

                keywordPredicates.add(cb.like(cb.lower(root.get("maGiamGia")), kw));
                keywordPredicates.add(cb.like(cb.lower(root.get("tenChuongTrinh")), kw));
                keywordPredicates.add(cb.like(cb.lower(root.get("moTa")), kw));

                if (keyword.endsWith("%")) {
                    try {
                        String numberPart = keyword.substring(0, keyword.length() - 1).trim();
                        BigDecimal value = new BigDecimal(numberPart);

                        Predicate giaTriPredicate = cb.equal(root.get("giaTriGiamGia"), value);
                        Predicate loaiGiamGiaPredicate = cb.equal(root.get("loaiGiamGia"), false);
                        keywordPredicates.add(cb.and(giaTriPredicate, loaiGiamGiaPredicate));

                    } catch (NumberFormatException e) {
                    }
                }
                else if (keyword.toLowerCase().contains("vnd")) {
                    try {
                        String numberPart = keyword.replaceAll("[^0-9]", "").trim();
                        if (!numberPart.isEmpty()) {
                            BigDecimal value = new BigDecimal(numberPart);

                            Predicate giaTriPredicate = cb.equal(root.get("giaTriGiamGia"), value);
                            Predicate loaiGiamGiaPredicate = cb.equal(root.get("loaiGiamGia"), true);
                            keywordPredicates.add(cb.and(giaTriPredicate, loaiGiamGiaPredicate));
                        }
                    } catch (NumberFormatException e) {
                    }
                }
                else {
                    try {
                        BigDecimal value = new BigDecimal(keyword);

                        keywordPredicates.add(cb.equal(root.get("giaTriGiamGia"), value));
                        keywordPredicates.add(cb.equal(root.get("mucGiaGiamToiDa"), value));
                        keywordPredicates.add(cb.equal(root.get("giaTriDonHangToiThieu"), value));

                    } catch (NumberFormatException e) {
                    }

                    try {
                        Integer soLuong = Integer.parseInt(keyword);
                        keywordPredicates.add(cb.equal(root.get("soLuongDung"), soLuong));
                    } catch (NumberFormatException e) {
                    }
                }

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
