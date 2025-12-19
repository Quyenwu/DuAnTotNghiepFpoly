package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.SanPham;
import com.example.the_autumn.repository.SanPhamRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class AIService {

    private final DeepSeekService deepSeekService;
    private final KnowledgeBaseService knowledgeBaseService;
    private final SanPhamRepository sanPhamRepo;

    // Lưu thời gian hỏi và số lần hỏi mỗi room
    private final Map<Integer, Long> lastAskTime = new ConcurrentHashMap<>();
    private final Map<Integer, Integer> askCount = new ConcurrentHashMap<>();
    private final long MIN_INTERVAL_MS = 3000; // 3 giây giữa 2 câu hỏi
    private final int MAX_ASK_PER_MIN = 20; // tối đa 20 câu / phút

    public AIService(DeepSeekService deepSeekService,
                     KnowledgeBaseService knowledgeBaseService,
                     SanPhamRepository sanPhamRepo) {
        this.deepSeekService = deepSeekService;
        this.knowledgeBaseService = knowledgeBaseService;
        this.sanPhamRepo = sanPhamRepo;
    }

    // 🚨 XÓA @Cacheable - KHÔNG cache câu trả lời AI
    public String ask(String message, Integer roomId) {
        if (message == null || message.isEmpty()) {
            return "Xin lỗi, tôi không hiểu câu hỏi của bạn.";
        }

        long now = System.currentTimeMillis();

        // Kiểm tra spam theo khoảng cách
        if (lastAskTime.containsKey(roomId) && now - lastAskTime.get(roomId) < MIN_INTERVAL_MS) {
            return "Bạn đang hỏi quá nhanh, vui lòng đợi 3 giây giữa 2 câu.";
        }

        // Kiểm tra số câu hỏi / phút
        askCount.putIfAbsent(roomId, 0);
        if (askCount.get(roomId) >= MAX_ASK_PER_MIN) {
            return "Bạn đã hỏi quá nhiều trong 1 phút, vui lòng đợi một chút.";
        }

        lastAskTime.put(roomId, now);
        askCount.put(roomId, askCount.get(roomId) + 1);

        // 🚨 TRƯỚC TIÊN: Kiểm tra nếu là câu hỏi tìm sản phẩm
        if (isProductSearchQuestion(message)) {
            System.out.println("🔍 AIService: Đây là câu hỏi tìm sản phẩm: " + message);
            String directResult = searchProductDirectly(message);
            if (directResult != null) {
                return directResult;
            }
        }

        // Detect topic
        String topic = knowledgeBaseService.detectTopic(message);

        // Lấy KB summary - CÁI NÀY vẫn cache được
        String kbSummary = knowledgeBaseService.getKBSummary(topic);

        // 🚨 THÊM LOG để debug
        System.out.println("📋 AIService: KB Summary length = " + kbSummary.length());
        System.out.println("📋 AIService: KB Summary contains sản phẩm mới? " +
                kbSummary.contains("Áo thun basic trắng Cotton 20%"));

        // Gọi AI trả lời
        return deepSeekService.getAnswer(message, kbSummary);
    }

    // 🚨 THÊM: Kiểm tra câu hỏi tìm sản phẩm
    private boolean isProductSearchQuestion(String question) {
        String q = question.toLowerCase();
        return q.contains("có sản phẩm") ||
                q.contains("có bán") ||
                q.contains("có mẫu") ||
                q.contains("tìm sản phẩm") ||
                q.contains("kiếm sản phẩm") ||
                q.contains("tìm kiếm") ||
                q.contains("có không") ||
                (q.contains("áo") && q.contains("cotton") && q.contains("20%"));
    }

    // 🚨 THÊM: Tìm sản phẩm trực tiếp từ DB
    private String searchProductDirectly(String question) {
        try {
            System.out.println("🔍 AIService: Tìm kiếm trực tiếp sản phẩm với: " + question);

            // Tách từ khóa
            String[] keywords = extractKeywords(question);
            if (keywords.length == 0) {
                return null;
            }

            // Tìm tất cả sản phẩm active
            List<SanPham> allProducts = sanPhamRepo.findByTrangThai(true);
            System.out.println("📊 Tổng sản phẩm active trong DB: " + allProducts.size());

            // Filter sản phẩm phù hợp
            List<SanPham> foundProducts = allProducts.stream()
                    .filter(sp -> {
                        String tenSP = sp.getTenSanPham().toLowerCase();
                        // Fuzzy search: chỉ cần một từ khóa khớp
                        for (String kw : keywords) {
                            if (kw.length() > 2 && tenSP.contains(kw.toLowerCase())) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao())) // Mới nhất trước
                    .limit(3)
                    .collect(Collectors.toList());

            System.out.println("✅ Tìm thấy " + foundProducts.size() + " sản phẩm phù hợp");

            if (foundProducts.isEmpty()) {
                return null; // Để DeepSeek xử lý
            }

            // Build response
            return buildProductResponse(foundProducts, question);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm sản phẩm trực tiếp: " + e.getMessage());
            return null;
        }
    }

    // 🚨 THÊM: Trích xuất keyword
    private String[] extractKeywords(String question) {
        // Loại bỏ stop words
        String cleaned = question.toLowerCase()
                .replace("có", "")
                .replace("bán", "")
                .replace("mẫu", "")
                .replace("sản phẩm", "")
                .replace("tìm", "")
                .replace("kiếm", "")
                .replace("?", "")
                .replace("không", "")
                .replace("ạ", "")
                .replace("em", "")
                .replace("anh", "")
                .replace("chị", "")
                .replaceAll("\\s+", " ")
                .trim();

        return cleaned.split(" ");
    }

    // 🚨 THÊM: Build response
    private String buildProductResponse(List<SanPham> products, String originalQuestion) {
        StringBuilder sb = new StringBuilder();

        if (products.size() == 1) {
            SanPham sp = products.get(0);
            sb.append("{\n");
            sb.append("  \"message\": \"Dạ có ạ! Bên em có mẫu ").append(sp.getTenSanPham()).append(" đấy ạ. Chất liệu cao cấp, mặc rất thoải mái. Anh/chị muốn em tư vấn thêm về size hay màu sắc không ạ?\",\n");
            sb.append("  \"products\": [\n");
            sb.append("    {\n");
            sb.append("      \"id\": ").append(sp.getId()).append(",\n");
            sb.append("      \"name\": \"").append(sp.getTenSanPham()).append("\",\n");

            // Lấy giá
            if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {
                BigDecimal minPrice = sp.getChiTietSanPham().stream()
                        .map(ChiTietSanPham::getGiaBan)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
                sb.append("      \"price\": ").append(minPrice).append(",\n");
            } else {
                sb.append("      \"price\": 0,\n");
            }

            sb.append("      \"image\": \"\",\n");
            sb.append("      \"link\": \"/product/").append(sp.getId()).append("\",\n");
            sb.append("      \"color\": \"\",\n");
            sb.append("      \"size_suggestion\": \"\"\n");
            sb.append("    }\n");
            sb.append("  ],\n");
            sb.append("  \"follow_up_question\": \"Anh/chị cần tư vấn size nào ạ?\",\n");
            sb.append("  \"need_human_support\": false\n");
            sb.append("}");
        } else {
            sb.append("{\n");
            sb.append("  \"message\": \"Dạ có ạ! Em tìm thấy ").append(products.size()).append(" sản phẩm phù hợp. Anh/chị muốn xem chi tiết sản phẩm nào ạ?\",\n");
            sb.append("  \"products\": [\n");

            for (int i = 0; i < products.size(); i++) {
                SanPham sp = products.get(i);
                sb.append("    {\n");
                sb.append("      \"id\": ").append(sp.getId()).append(",\n");
                sb.append("      \"name\": \"").append(sp.getTenSanPham()).append("\",\n");

                if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {
                    BigDecimal minPrice = sp.getChiTietSanPham().stream()
                            .map(ChiTietSanPham::getGiaBan)
                            .min(BigDecimal::compareTo)
                            .orElse(BigDecimal.ZERO);
                    sb.append("      \"price\": ").append(minPrice).append(",\n");
                } else {
                    sb.append("      \"price\": 0,\n");
                }

                sb.append("      \"image\": \"\",\n");
                sb.append("      \"link\": \"/product/").append(sp.getId()).append("\",\n");
                sb.append("      \"color\": \"\",\n");
                sb.append("      \"size_suggestion\": \"\"\n");
                sb.append("    }");
                if (i < products.size() - 1) sb.append(",");
                sb.append("\n");
            }

            sb.append("  ],\n");
            sb.append("  \"follow_up_question\": \"Anh/chị quan tâm đến sản phẩm nào nhất ạ?\",\n");
            sb.append("  \"need_human_support\": false\n");
            sb.append("}");
        }

        return sb.toString();
    }
}
