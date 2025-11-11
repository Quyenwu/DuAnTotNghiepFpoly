package com.example.the_autumn.service;

import com.example.the_autumn.model.response.*;
import com.example.the_autumn.repository.*;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private final SanPhamRepository sanPhamRepo;
    private final ChiTietSanPhamRepository chiTietRepo;
    private final MauSacRepository mauSacRepo;
    private final KichThuocRepository kichThuocRepo;
    private final ChatLieuRepository chatLieuRepo;
    private final CoAoRepository coAoRepo;
    private final TayAoRepository tayAoRepo;
    private final KieuDangRepository kieuDangRepo;
    private final PhieuGiamGiaRepository phieuGiamRepo;
    private final DotGiamGiaRepository dotGiamRepo;
    private final KhachHangRepository khachHangRepo;
    private final DiaChiRepository diaChiRepo;

    public KnowledgeBaseService(
            SanPhamRepository sanPhamRepo,
            ChiTietSanPhamRepository chiTietRepo,
            MauSacRepository mauSacRepo,
            KichThuocRepository kichThuocRepo,
            ChatLieuRepository chatLieuRepo,
            CoAoRepository coAoRepo,
            TayAoRepository tayAoRepo,
            KieuDangRepository kieuDangRepo,
            PhieuGiamGiaRepository phieuGiamRepo,
            DotGiamGiaRepository dotGiamRepo,
            KhachHangRepository khachHangRepo,
            DiaChiRepository diaChiRepo
    ) {
        this.sanPhamRepo = sanPhamRepo;
        this.chiTietRepo = chiTietRepo;
        this.mauSacRepo = mauSacRepo;
        this.kichThuocRepo = kichThuocRepo;
        this.chatLieuRepo = chatLieuRepo;
        this.coAoRepo = coAoRepo;
        this.tayAoRepo = tayAoRepo;
        this.kieuDangRepo = kieuDangRepo;
        this.phieuGiamRepo = phieuGiamRepo;
        this.dotGiamRepo = dotGiamRepo;
        this.khachHangRepo = khachHangRepo;
        this.diaChiRepo = diaChiRepo;
    }

    // Xác định topic
    public String detectTopic(String question) {
        String q = question.toLowerCase();
        if (q.contains("sản phẩm") || q.contains("áo") || q.contains("quần")) return "sanpham_text";
        if (q.contains("giảm giá") || q.contains("khuyến mãi")) return "khuyenmai";
        if (q.contains("địa chỉ") || q.contains("giao hàng") || q.contains("khách hàng")) return "khachhang";
        return "chung";
    }

    // Trả dữ liệu dạng map
    public Map<String, Object> getKnowledgeBase(String topic) {
        Map<String, Object> kb = new HashMap<>();
        LocalDate today = LocalDate.now();

        switch (topic) {
            case "sanpham_text" -> {
                kb.put("sanPhamText", getSanPhamText());
            }
            case "khuyenmai" -> {
                kb.put("phieuGiamGia", phieuGiamRepo.findActive(today)
                        .stream().map(PhieuGiamGiaRespone::new).toList());
                kb.put("dotGiamGia", dotGiamRepo.findActive(today)
                        .stream().map(DotGiamGiaResponse::new).toList());
            }
            case "khachhang" -> {
                kb.put("khachHang", khachHangRepo.findByTrangThai(true)
                        .stream().map(KhachHangResponse::new).toList());
                kb.put("diaChi", diaChiRepo.findByTrangThai(true)
                        .stream().map(DiaChiResponse::new).toList());
            }
            default -> kb.put("note", "Dữ liệu chung về cửa hàng thời trang The Autumn");
        }

        return kb;
    }

    // Hàm format sản phẩm dạng text "cha-con"
    private String getSanPhamText() {
        List<SanPhamResponse> sanPhams = sanPhamRepo.findByTrangThai(true)
                .stream()
                .map(SanPhamResponse::new)
                .toList();

        StringBuilder sb = new StringBuilder();

        int stt = 1;
        for (SanPhamResponse sp : sanPhams) {
            sb.append(stt)
                    .append(". **")
                    .append(sp.getTenSanPham())
                    .append("**");

            // ✅ Lấy giá sau giảm thấp nhất trong chi tiết sản phẩm
            BigDecimal giaSauGiam = sp.getChiTietSanPhams().stream()
                    .map(ChiTietSanPhamResponse::getGiaSauGiam)
                    .filter(g -> g != null)
                    .min(BigDecimal::compareTo)
                    .orElse(sp.getGiaThapNhat());
            sb.append(" - ").append(giaSauGiam != null ? giaSauGiam : "Liên hệ").append("đ\n");

            // ✅ Kiểu dáng & chất liệu
            sb.append("   - Kiểu dáng: ")
                    .append(sp.getTenKieuDang() != null ? sp.getTenKieuDang() : "Đang cập nhật")
                    .append(", chất liệu: ")
                    .append(sp.getTenChatLieu() != null ? sp.getTenChatLieu() : "Đang cập nhật")
                    .append("\n");

            // ✅ Màu sắc
            String mauSacList = sp.getChiTietSanPhams().stream()
                    .map(ChiTietSanPhamResponse::getTenMauSac)
                    .filter(m -> m != null && !m.isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));
            sb.append("   - Màu sắc: ").append(mauSacList.isEmpty() ? "Đang cập nhật" : mauSacList).append("\n");

            // ✅ Size
            String sizeList = sp.getChiTietSanPhams().stream()
                    .map(ChiTietSanPhamResponse::getTenKichThuoc)
                    .filter(s -> s != null && !s.isEmpty())
                    .distinct()
                    .sorted()
                    .collect(Collectors.joining(", "));
            sb.append("   - Size: ").append(sizeList.isEmpty() ? "Đang cập nhật" : sizeList).append("\n");

            // ✅ Lấy ảnh đại diện từ chi tiết sản phẩm
            String anhDaiDien = sp.getChiTietSanPhams().stream()
                    .flatMap(ct -> ct.getAnhs() != null ? ct.getAnhs().stream() : java.util.stream.Stream.empty())
                    .map(AnhResponse::getDuongDanAnh)
                    .filter(a -> a != null && !a.isEmpty())
                    .findFirst()
                    .orElse("https://cdn.theautumn.vn/no-image.jpg");
            sb.append("   - Ảnh: ").append(anhDaiDien).append("\n\n");

            stt++;
        }

        return sb.toString();
    }
}
