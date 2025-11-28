package com.example.the_autumn.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
public class KnowledgeBaseService {

    private final KnowledgeBaseQueryService queryService;

    public KnowledgeBaseService(KnowledgeBaseQueryService queryService) {
        this.queryService = queryService;
    }

    public String detectTopic(String question) {
        String q = question.toLowerCase();
        if (q.contains("sản phẩm") || q.contains("áo") || q.contains("quần")) return "sanpham_text";
        if (q.contains("giảm giá") || q.contains("khuyến mãi")) return "khuyenmai";
        if (q.contains("địa chỉ") || q.contains("giao hàng") || q.contains("khách hàng")) return "khachhang";
        return "chung";
    }

    @Cacheable(value = "knowledge_base", key = "#topic")
    public Map<String, Object> getKnowledgeBase(String topic) {
        Map<String, Object> kb = new HashMap<>();
        LocalDate today = LocalDate.now();

        switch (topic) {
            case "sanpham_text" -> kb.put("sanPhamText", queryService.getSanPhamData());
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
}
