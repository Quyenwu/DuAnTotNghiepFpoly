package com.example.the_autumn.service;

import com.example.the_autumn.dto.ListSanPhamResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private final KnowledgeBaseQueryService queryService;

    public KnowledgeBaseService(KnowledgeBaseQueryService queryService) {
        this.queryService = queryService;
    }

    // Topic detection nâng cao
    public String detectTopic(String question) {
        String q = question.toLowerCase();
        Map<String, List<String>> topics = Map.of(
                "sanpham_text", List.of("sản phẩm", "áo", "quần", "váy", "giày"),
                "khuyenmai", List.of("giảm giá", "khuyến mãi", "sale", "voucher"),
                "khachhang", List.of("địa chỉ", "giao hàng", "khách hàng", "tài khoản")
        );

        for (var entry : topics.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (q.contains(keyword)) return entry.getKey();
            }
        }
        return "chung";
    }

    // Cache KB summary (top 3 sản phẩm + khuyến mãi)
    @Cacheable(value = "knowledge_base_summary", key = "#topic")
    public String getKBSummary(String topic) {
        Map<String, Object> kb = getKnowledgeBase(topic);
        return summarizeKB(kb);
    }

    // Lấy KB đầy đủ theo topic (dùng cho summary)
    @Cacheable(value = "knowledge_base", key = "#topic")
    public Map<String, Object> getKnowledgeBase(String topic) {
        Map<String, Object> kb = new HashMap<>();
        LocalDate today = LocalDate.now();

        switch (topic) {
            case "sanpham_text" -> {
                var allProducts = queryService.getSanPhamData();
                var topProducts = allProducts.stream()
                        .sorted(Comparator.comparingInt(ListSanPhamResponse::getTongSoLuongDaMua).reversed())
                        .limit(5) // top 3
                        .collect(Collectors.toList());
                kb.put("sanPhamList", topProducts);
            }
            case "khuyenmai" -> {
                kb.put("phieuGiamGia", queryService.getActivePhieuGiam(today));
                kb.put("dotGiamGia", queryService.getActiveDotGiam(today));
            }
            case "khachhang" -> {
                kb.put("khachHang", queryService.getActiveKhachHang());
                kb.put("diaChi", queryService.getActiveDiaChi());
            }
            default -> kb.put("note", "Dữ liệu chung cửa hàng The Autumn");
        }
        return kb;
    }

    // Tóm tắt KB để gửi AI
    public String summarizeKB(Map<String, Object> kb) {
        StringBuilder sb = new StringBuilder();
        if (kb.containsKey("sanPhamList")) {
            List<ListSanPhamResponse> spList = (List<ListSanPhamResponse>) kb.get("sanPhamList");
            sb.append("Top sản phẩm:\n");
            for (var sp : spList) {
                sb.append("- ").append(sp.getTenSanPham())
                        .append(" | Giá: ").append(sp.getGiaThapNhat()).append(" - ").append(sp.getGiaCaoNhat())
                        .append(" | Ảnh: ").append(sp.getHinhAnhSanPham().stream().findFirst().orElse(""))
                        .append(" | Link: /product/").append(sp.getId())
                        .append("\n");
            }
        }
        if (kb.containsKey("phieuGiamGia")) {
            sb.append("Khuyến mãi hiện có: ").append(kb.get("phieuGiamGia")).append("\n");
        }
        if (kb.containsKey("dotGiamGia")) {
            sb.append("Đợt giảm giá: ").append(kb.get("dotGiamGia")).append("\n");
        }
        return sb.toString();
    }
}
