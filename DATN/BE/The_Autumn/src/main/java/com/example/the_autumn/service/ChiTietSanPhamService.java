package com.example.the_autumn.service;

import com.example.the_autumn.entity.*;
import com.example.the_autumn.model.request.TaoBienTheRequest;
import com.example.the_autumn.model.request.UpdateChiTietSanPhamRequest;
import com.example.the_autumn.model.response.ChiTietSanPhamResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChiTietSanPhamService {

    @Autowired
    private SanPhamRepository spRepo;

    @Autowired
    private ChiTietSanPhamRepository ctspRepo;

    @Autowired
    private AnhRepository anhRepo;

    @Autowired
    private MauSacRepository msRepo;

    @Autowired
    private KichThuocRepository ktRepo;

    @Autowired
    private CoAoRepository caRepo;

    @Autowired
    private TayAoRepository taRepo;

    @Autowired
    private NhaSanXuatRepository nsxRepo;

    @Autowired
    private XuatXuRepository xxRepo;

    @Autowired
    private ChatLieuRepository clRepo;

    @Autowired
    private KieuDangRepository kdRepo;

    public List<ChiTietSanPhamResponse> findAll() {
        return ctspRepo.findAll().stream().map(ChiTietSanPhamResponse::new).toList();
    }

    public PageableObject<ChiTietSanPhamResponse> phanTrang(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<ChiTietSanPham> pageCtsp = ctspRepo.findAll(pageable);
        Page<ChiTietSanPhamResponse> ctspRes = pageCtsp.map(ChiTietSanPhamResponse::new);
        return new PageableObject<>(ctspRes);
    }

    @Transactional
    public List<ChiTietSanPhamResponse> taoBienThe(TaoBienTheRequest request) {
        System.out.println("🔄 Service.taoBienThe() - Tạo sản phẩm hoàn chỉnh + biến thể");

        List<ChiTietSanPhamResponse> result = new ArrayList<>();

        try {
            SanPham sanPham = taoSanPhamHoanChinh(request);
            System.out.println("✅ Đã tạo sản phẩm với ID: " + sanPham.getId());

            Map<Integer, MauSac> mauSacMap = msRepo.findAllById(request.getIdMauSacs())
                    .stream().collect(Collectors.toMap(MauSac::getId, m -> m));

            KichThuoc kichThuoc = ktRepo.findById(request.getIdKichThuoc())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước"));

            List<ChiTietSanPham> danhSachBienThe = new ArrayList<>();

            for (Integer idMauSac : request.getIdMauSacs()) {
                if (kiemTraBienTheTrung(sanPham.getId(), idMauSac, request.getIdKichThuoc())) {
                    System.out.println("⚠️ Biến thể đã tồn tại, bỏ qua");
                    continue;
                }

                ChiTietSanPham ctsp = taoBienTheOptimized(
                        sanPham,
                        mauSacMap.get(idMauSac),
                        kichThuoc
                );

                danhSachBienThe.add(ctsp);
            }

            List<ChiTietSanPham> savedList = ctspRepo.saveAll(danhSachBienThe);

            result = savedList.stream()
                    .map(ChiTietSanPhamResponse::new)
                    .collect(Collectors.toList());

            System.out.println("✅ Tổng cộng tạo được " + result.size() + " biến thể");
            return result;

        } catch (Exception e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi tạo biến thể: " + e.getMessage());
        }
    }

    private SanPham taoSanPhamHoanChinh(TaoBienTheRequest request) {
        String maSanPham = generateMaSanPham(request.getTenSanPham());

        int attempt = 0;
        while (spRepo.existsByMaSanPham(maSanPham) && attempt < 5) {
            maSanPham = generateMaSanPham(request.getTenSanPham() + attempt);
            attempt++;
        }

        if (spRepo.existsByMaSanPham(maSanPham)) {
            throw new RuntimeException("Không thể tạo mã sản phẩm duy nhất sau " + attempt + " lần thử");
        }

        SanPham sanPham = new SanPham();

        sanPham.setTenSanPham(request.getTenSanPham());

        sanPham.setTrongLuong(request.getTrongLuong());

        sanPham.setNhaSanXuat(nsxRepo.findById(request.getIdNhaSanXuat())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà sản xuất ID: " + request.getIdNhaSanXuat())));

        sanPham.setXuatXu(xxRepo.findById(request.getIdXuatXu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xuất xứ ID: " + request.getIdXuatXu())));

        sanPham.setChatLieu(clRepo.findById(request.getIdChatLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chất liệu ID: " + request.getIdChatLieu())));

        sanPham.setKieuDang(kdRepo.findById(request.getIdKieuDang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kiểu dáng ID: " + request.getIdKieuDang())));

        sanPham.setCoAo(caRepo.findById(request.getIdCoAo())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cổ áo")));

        sanPham.setTayAo(taRepo.findById(request.getIdTayAo())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tay áo")));

        sanPham.setNgayTao(new Date());
        sanPham.setTrangThai(true);

        return spRepo.save(sanPham);
    }

    private ChiTietSanPham taoBienTheOptimized(SanPham sanPham, MauSac mauSac,
                                               KichThuoc kichThuoc) {
        if (mauSac == null || kichThuoc == null ) {
            throw new RuntimeException("Một trong các thuộc tính không tồn tại");
        }

        ChiTietSanPham ctsp = new ChiTietSanPham();

        ctsp.setSanPham(sanPham);
        ctsp.setMauSac(mauSac);
        ctsp.setKichThuoc(kichThuoc);
        ctsp.setGiaBan(BigDecimal.ZERO);
        ctsp.setSoLuongTon(0);

        ctsp.setMaVach(generateMaVach());
        ctsp.setNgayTao(LocalDate.now());
        ctsp.setTrangThai(true);

        return ctsp;
    }

    private boolean kiemTraBienTheTrung(Integer idSanPham, Integer idMauSac,
                                        Integer idKichThuoc) {
        return ctspRepo.existsBySanPham_IdAndMauSac_IdAndKichThuoc_Id(
                idSanPham, idMauSac, idKichThuoc
        );
    }

    @Transactional
    public void xoaBienThe(Integer idChiTietSanPham) {
        System.out.println("🗑️ Service.xoaBienThe() - Xóa biến thể: " + idChiTietSanPham);

        ChiTietSanPham ctsp = ctspRepo.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idChiTietSanPham));

        ctsp.setTrangThai(false);
        ctsp.setNgaySua(LocalDate.now());
        ctspRepo.save(ctsp);

        System.out.println("✅ Đã xóa (soft delete) biến thể: " + idChiTietSanPham);
    }

    @Transactional
    public void capNhatGiaBienThe(Integer idChiTietSanPham, BigDecimal donGia) {
        if (donGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá không được âm");
        }

        ChiTietSanPham ctsp = ctspRepo.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idChiTietSanPham));

        ctsp.setGiaBan(donGia);
        ctsp.setNgaySua(LocalDate.now());
        ctspRepo.save(ctsp);

        System.out.println("✅ Đã cập nhật giá biến thể: " + idChiTietSanPham);
    }

    @Transactional
    public void capNhatSoLuongBienThe(Integer idChiTietSanPham, Integer soLuong) {
        if (soLuong < 0) {
            throw new RuntimeException("Số lượng không được âm");
        }

        ChiTietSanPham ctsp = ctspRepo.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idChiTietSanPham));

        ctsp.setSoLuongTon(soLuong);
        ctsp.setNgaySua(LocalDate.now());
        ctspRepo.save(ctsp);

        System.out.println("✅ Đã cập nhật số lượng biến thể: " + idChiTietSanPham);
    }

    private String generateMaVach() {
        return UUID.randomUUID().toString().substring(0, 13);
    }

    private String generateMaSanPham(String tenSanPham) {
        long nextSequence = spRepo.count() + 1;

        String prefix = tenSanPham.length() >= 3
                ? tenSanPham.substring(0, 3).toUpperCase().replaceAll("[^A-Z]", "A")
                : "SPR";

        if (prefix.length() < 3) {
            prefix = String.format("%-3s", prefix).replace(' ', 'A');
        }

        String sequence = String.format("%06d", nextSequence);
        return prefix + sequence;
    }

    public Map<String, Object> previewBienThe(TaoBienTheRequest request) {
        System.out.println("🔍 Service.previewBienThe() - Tính toán thông tin biến thể");

        try {
            String tenNhaSanXuat = nsxRepo.findById(request.getIdNhaSanXuat())
                    .map(NhaSanXuat::getTenNhaSanXuat).orElse("Không xác định");
            String tenXuatXu = xxRepo.findById(request.getIdXuatXu())
                    .map(XuatXu::getTenXuatXu).orElse("Không xác định");
            String tenChatLieu = clRepo.findById(request.getIdChatLieu())
                    .map(ChatLieu::getTenChatLieu).orElse("Không xác định");
            String tenKieuDang = kdRepo.findById(request.getIdKieuDang())
                    .map(KieuDang::getTenKieuDang).orElse("Không xác định");
            String tenCoAo = caRepo.findById(request.getIdCoAo())
                    .map(CoAo::getTenCoAo).orElse("Không xác định");
            String tenTayAo = taRepo.findById(request.getIdTayAo())
                    .map(TayAo::getTenTayAo).orElse("Không xác định");
            String tenKichThuoc = ktRepo.findById(request.getIdKichThuoc())
                    .map(KichThuoc::getTenKichThuoc).orElse("Không xác định");

            List<String> tenMauSacs = msRepo.findAllById(request.getIdMauSacs())
                    .stream()
                    .map(MauSac::getTenMauSac)
                    .collect(Collectors.toList());

            int totalVariants = request.getIdMauSacs().size();

            Map<String, Object> previewInfo = new HashMap<>();
            previewInfo.put("maSanPham", "SP" + System.currentTimeMillis()); // Tạm thời
            previewInfo.put("tenSanPham", request.getTenSanPham());
            previewInfo.put("totalVariants", totalVariants);
            previewInfo.put("tenNhaSanXuat", tenNhaSanXuat);
            previewInfo.put("tenXuatXu", tenXuatXu);
            previewInfo.put("tenChatLieu", tenChatLieu);
            previewInfo.put("tenKieuDang", tenKieuDang);
            previewInfo.put("tenCoAo", tenCoAo);
            previewInfo.put("tenTayAo", tenTayAo);
            previewInfo.put("tenKichThuoc", tenKichThuoc);
            previewInfo.put("tenMauSacs", tenMauSacs);
            previewInfo.put("timestamp", new Date());

            System.out.println("✅ Preview tính toán: " + totalVariants + " biến thể");
            return previewInfo;

        } catch (Exception e) {
            System.err.println("❌ Lỗi preview: " + e.getMessage());
            throw new RuntimeException("Lỗi khi preview biến thể: " + e.getMessage());
        }
    }

    @Transactional
    public ChiTietSanPhamResponse updateChiTietSanPham(Integer id, UpdateChiTietSanPhamRequest request) {
        System.out.println("🔄 Service: Update chi tiết sản phẩm ID=" + id);

        ChiTietSanPham chiTiet = ctspRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + id));

        KichThuoc kichThuoc = ktRepo.findById(request.getIdKichThuoc())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kích thước với ID: " + request.getIdKichThuoc()));

        MauSac mauSac = msRepo.findById(request.getIdMauSac())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + request.getIdMauSac()));

        boolean isChangedSizeOrColor = !chiTiet.getKichThuoc().getId().equals(request.getIdKichThuoc())
                || !chiTiet.getMauSac().getId().equals(request.getIdMauSac());

        if (isChangedSizeOrColor) {
            boolean exists = ctspRepo.existsBySanPham_IdAndMauSac_IdAndKichThuoc_Id(
                    chiTiet.getSanPham().getId(),
                    request.getIdMauSac(),
                    request.getIdKichThuoc()
            );

            if (exists) {
                throw new RuntimeException("Biến thể với kích thước '" + kichThuoc.getTenKichThuoc()
                        + "' và màu sắc '" + mauSac.getTenMauSac() + "' đã tồn tại");
            }
        }

        if (request.getGiaBan().compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Giá bán không được âm");
        }

        if (request.getSoLuongTon() < 0) {
            throw new RuntimeException("Số lượng tồn không được âm");
        }

        chiTiet.setKichThuoc(kichThuoc);
        chiTiet.setMauSac(mauSac);
        chiTiet.setGiaBan(request.getGiaBan());
        chiTiet.setSoLuongTon(request.getSoLuongTon());

        if (request.getMaVach() != null && !request.getMaVach().trim().isEmpty()) {
            chiTiet.setMaVach(request.getMaVach());
        }

        chiTiet.setMoTa(request.getMoTa());

        if (request.getTrangThai() != null) {
            chiTiet.setTrangThai(request.getTrangThai());
        }

        chiTiet.setNgaySua(LocalDate.now());

        ChiTietSanPham saved = ctspRepo.save(chiTiet);

        System.out.println("✅ Service: Đã cập nhật biến thể ID=" + id);

        return new ChiTietSanPhamResponse(saved);
    }

    public List<ChiTietSanPhamResponse> findBySanPhamId(Integer idSanPham) {
        List<ChiTietSanPham> list = ctspRepo.findBySanPhamId(idSanPham);

        if (list == null || list.isEmpty()) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(ChiTietSanPhamResponse::new)
                .collect(Collectors.toList());
    }
}