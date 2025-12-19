package com.example.the_autumn.service;

import com.example.the_autumn.dto.ListSanPhamResponse;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class KnowledgeBaseService {

    private final KnowledgeBaseQueryService queryService;

    @Autowired
    private org.springframework.cache.CacheManager cacheManager;

    public KnowledgeBaseService(KnowledgeBaseQueryService queryService) {
        this.queryService = queryService;
    }

    // THÊM METHOD MỚI: Xóa cache sản phẩm khi có thay đổi
    public void clearProductCache() {
        // Xóa tất cả cache liên quan đến sản phẩm
        if (cacheManager.getCache("knowledge_base") != null) {
            cacheManager.getCache("knowledge_base").clear(); // Xóa tất cả, không chỉ sanpham_text
        }

        if (cacheManager.getCache("knowledge_base_summary") != null) {
            cacheManager.getCache("knowledge_base_summary").clear();
        }

        if (cacheManager.getCache("ai_answer_cache") != null) {
            cacheManager.getCache("ai_answer_cache").clear();
        }

        System.out.println("✅ [" + new java.util.Date() + "] Đã xóa TẤT CẢ cache AI");
    }

    private Map<String, Object> fetchKnowledgeBaseFromDB(String topic) {
        Map<String, Object> kb = new HashMap<>();
        LocalDate today = LocalDate.now();

        switch (topic) {
            case "sanpham_text" -> {
                var allProducts = queryService.getSanPhamData();

                // 🚨 SỬA QUAN TRỌNG: Sắp xếp theo sản phẩm MỚI NHẤT
                var topProducts = allProducts.stream()
                        .sorted(Comparator
                                // Ưu tiên sản phẩm MỚI NHẤT (createdDate mới nhất)
                                .comparing((ListSanPhamResponse p) -> {
                                    if (p.getCreatedDate() == null) {
                                        return new Date(0L); // Nếu null, xem như cũ
                                    }
                                    return p.getCreatedDate();
                                }).reversed()
                                // Sau đó sắp xếp theo số lượng đã bán
                                .thenComparingInt(ListSanPhamResponse::getTongSoLuongDaMua).reversed()
                        )
                        .limit(20)
                        .collect(Collectors.toList());

                kb.put("sanPhamList", topProducts);
                kb.put("totalProducts", allProducts.size());
                kb.put("lastFetched", new Date());
// Debug log chi tiết
                System.out.println("==========================================");
                System.out.println("📦 KnowledgeBaseService: Lấy dữ liệu sản phẩm từ DB");
                System.out.println("📊 Tổng sản phẩm: " + allProducts.size());
                System.out.println("📊 Hiển thị: " + topProducts.size() + " sản phẩm (top 20)");
                // Debug log
                System.out.println("📦 Lấy dữ liệu sản phẩm từ DB:");
                for (var sp : topProducts) {
                    System.out.println("   - " + sp.getTenSanPham() +
                            " (Ngày tạo: " + sp.getCreatedDate() +
                            ", Đã bán: " + sp.getTongSoLuongDaMua() + ")");
                }
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

    // Cache KB đầy đủ
    @Cacheable(value = "knowledge_base", key = "#topic")
    public Map<String, Object> getKnowledgeBase(String topic) {
        System.out.println("🔄 [" + new java.util.Date() + "] Đang lấy dữ liệu từ DB cho topic: " + topic);
        return fetchKnowledgeBaseFromDB(topic);
    }

    // Topic detection nâng cao
    public String detectTopic(String question) {
        String q = question.toLowerCase();
        Map<String, List<String>> topics = Map.of(
                "sanpham_text", List.of("sản phẩm", "áo", "quần", "váy", "giày", "mới", "hàng mới", "sản phẩm mới"),
                "khuyenmai", List.of("giảm giá", "khuyến mãi", "sale", "voucher", "giảm"),
                "khachhang", List.of("địa chỉ", "giao hàng", "khách hàng", "tài khoản", "vận chuyển")
        );

        for (var entry : topics.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (q.contains(keyword)) return entry.getKey();
            }
        }
        return "chung";
    }

    // Cache KB summary
    @Cacheable(value = "knowledge_base_summary", key = "#topic")
    public String getKBSummary(String topic) {
        System.out.println("📝 [" + new java.util.Date() + "] Đang tạo summary cho topic: " + topic);
        Map<String, Object> kb = getKnowledgeBase(topic);
        return summarizeKB(kb);
    }

    // Tóm tắt KB để gửi AI
    public String summarizeKB(Map<String, Object> kb) {
        StringBuilder sb = new StringBuilder();
        if (kb.containsKey("sanPhamList")) {
            List<ListSanPhamResponse> spList = (List<ListSanPhamResponse>) kb.get("sanPhamList");
            sb.append("Top sản phẩm (cập nhật mới nhất):\n");
            for (var sp : spList) {
                sb.append("- ").append(sp.getTenSanPham())
                        .append(" | Giá: ").append(sp.getGiaThapNhat()).append(" - ").append(sp.getGiaCaoNhat())
                        .append(" | Đã bán: ").append(sp.getTongSoLuongDaMua())
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