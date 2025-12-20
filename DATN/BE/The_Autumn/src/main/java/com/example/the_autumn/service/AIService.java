package com.example.the_autumn.service;

import com.example.the_autumn.entity.Anh;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.SanPham;
import com.example.the_autumn.repository.SanPhamRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final ObjectMapper objectMapper;

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
        this.objectMapper = new ObjectMapper();
    }

    // 🚨 THAY ĐỔI: Luôn trả về String
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
            Map<String, Object> directResult = searchProductDirectly(message);
            if (directResult != null) {
                try {
                    // 🚨 Convert Map thành JSON string
                    String jsonResponse = objectMapper.writeValueAsString(directResult);
                    System.out.println("✅ Direct search response: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())) + "...");
                    return jsonResponse;
                } catch (Exception e) {
                    System.err.println("❌ Lỗi convert Map to JSON: " + e.getMessage());
                    return "Có lỗi xảy ra khi tìm sản phẩm. Vui lòng thử lại.";
                }
            }
        }

        // Detect topic
        String topic = knowledgeBaseService.detectTopic(message);

        // Lấy KB summary
        String kbSummary = knowledgeBaseService.getKBSummary(topic);

        // Gọi AI trả lời
        String aiResponse = deepSeekService.getAnswer(message, kbSummary);

        // Kiểm tra nếu AI trả về JSON sản phẩm
        if (isProductResponse(aiResponse)) {
            try {
                // Parse JSON và bổ sung thông tin
                Map<String, Object> enhancedResponse = enhanceProductResponse(aiResponse);
                // 🚨 Convert lại thành JSON string
                String jsonResponse = objectMapper.writeValueAsString(enhancedResponse);
                System.out.println("✅ Enhanced AI response: " + jsonResponse.substring(0, Math.min(100, jsonResponse.length())) + "...");
                return jsonResponse;
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi xử lý response AI: " + e.getMessage());
                return aiResponse; // Trả về nguyên bản nếu có lỗi
            }
        }

        System.out.println("✅ Plain AI response: " + (aiResponse.length() > 100 ? aiResponse.substring(0, 100) + "..." : aiResponse));
        return aiResponse; // Trả về text thông thường
    }

    // Kiểm tra nếu response có chứa dữ liệu sản phẩm
    private boolean isProductResponse(String response) {
        try {
            if (response == null || response.trim().isEmpty()) {
                return false;
            }
            String trimmed = response.trim();
            return trimmed.startsWith("{") && trimmed.endsWith("}");
        } catch (Exception e) {
            return false;
        }
    }

    // Bổ sung thông tin sản phẩm cho response từ AI
    private Map<String, Object> enhanceProductResponse(String aiJsonResponse) {
        try {
            Map<String, Object> responseMap = objectMapper.readValue(aiJsonResponse, Map.class);

            List<Map<String, Object>> products = (List<Map<String, Object>>) responseMap.get("products");
            if (products != null && !products.isEmpty()) {
                List<Map<String, Object>> enhancedProducts = new ArrayList<>();

                for (Map<String, Object> product : products) {
                    Object idObj = product.get("id");
                    Integer productId = null;

                    if (idObj != null) {
                        try {
                            // Xử lý nhiều kiểu dữ liệu cho ID
                            if (idObj instanceof Integer) {
                                productId = (Integer) idObj;
                            } else if (idObj instanceof Long) {
                                productId = ((Long) idObj).intValue();
                            } else if (idObj instanceof Number) {
                                // Xử lý các kiểu Number khác
                                productId = ((Number) idObj).intValue();
                            } else if (idObj instanceof String) {
                                String idStr = ((String) idObj).trim();
                                if (!idStr.isEmpty() && idStr.matches("\\d+")) {
                                    productId = Integer.parseInt(idStr);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("⚠️ Không parse được ID: " + idObj + " - " + e.getMessage());
                        }
                    }

                    if (productId != null) {
                        // Tìm sản phẩm trong DB để lấy thông tin chi tiết
                        Optional<SanPham> sanPhamOpt = sanPhamRepo.findById(productId);
                        if (sanPhamOpt.isPresent()) {
                            SanPham sanPham = sanPhamOpt.get();
                            Map<String, Object> enhancedProduct = new LinkedHashMap<>(product);

                            // ✅ THÊM: đúng tên trường FE mong đợi
                            enhancedProduct.put("tenSanPham", sanPham.getTenSanPham());

                            // ✅ THÊM: lấy hình ảnh sản phẩm - QUAN TRỌNG!
                            List<String> images = new ArrayList<>();
                            if (sanPham.getChiTietSanPham() != null) {
                                for (ChiTietSanPham ctsp : sanPham.getChiTietSanPham()) {
                                    if (ctsp.getAnhs() != null) {
                                        for (Anh anh : ctsp.getAnhs()) {
                                            if (anh.getDuongDanAnh() != null && !anh.getDuongDanAnh().isEmpty()) {
                                                images.add(anh.getDuongDanAnh());
                                            }
                                        }
                                    }
                                }
                            }

                            // Nếu không có ảnh, dùng placeholder
                            if (images.isEmpty()) {
                                images.add("/placeholder.png");
                            }

                            // ✅ QUAN TRỌNG: trường này phải trùng với FE
                            enhancedProduct.put("hinhAnhSanPham", images);

                            // Giữ trường image cho tương thích
                            if (!enhancedProduct.containsKey("image") ||
                                    enhancedProduct.get("image") == null ||
                                    enhancedProduct.get("image").toString().isEmpty()) {
                                enhancedProduct.put("image", images.get(0));
                            }

                            // Lấy màu sắc nếu chưa có
                            if (!enhancedProduct.containsKey("color") ||
                                    enhancedProduct.get("color") == null ||
                                    enhancedProduct.get("color").toString().isEmpty()) {

                                String colors = "";
                                if (sanPham.getChiTietSanPham() != null) {
                                    colors = sanPham.getChiTietSanPham().stream()
                                            .filter(ctsp -> ctsp.getMauSac() != null)
                                            .map(ctsp -> ctsp.getMauSac().getTenMauSac())
                                            .distinct()
                                            .collect(Collectors.joining(", "));
                                }
                                enhancedProduct.put("color", colors);
                            }

                            // Lấy giá nếu chưa có hoặc bằng 0
                            if (!enhancedProduct.containsKey("price") ||
                                    enhancedProduct.get("price") == null) {

                                BigDecimal minPrice = BigDecimal.ZERO;
                                if (sanPham.getChiTietSanPham() != null && !sanPham.getChiTietSanPham().isEmpty()) {
                                    minPrice = sanPham.getChiTietSanPham().stream()
                                            .map(ChiTietSanPham::getGiaBan)
                                            .min(BigDecimal::compareTo)
                                            .orElse(BigDecimal.ZERO);
                                }
                                enhancedProduct.put("price", minPrice);
                            }

                            // Sửa link cho đúng format FE
                            if (!enhancedProduct.containsKey("link") ||
                                    enhancedProduct.get("link") == null ||
                                    !enhancedProduct.get("link").toString().contains("productDetail")) {
                                enhancedProduct.put("link", "/productDetail/" + productId);
                            }

                            enhancedProducts.add(enhancedProduct);

                            // Log để debug
                            System.out.println("✅ Enhanced product ID: " + productId + " - " + sanPham.getTenSanPham());
                            System.out.println("   Images count: " + images.size());

                        } else {
                            // Nếu không tìm thấy sản phẩm, giữ nguyên
                            enhancedProducts.add(product);
                            System.out.println("⚠️ Không tìm thấy sản phẩm ID: " + productId);
                        }
                    } else {
                        // Nếu không parse được ID, giữ nguyên product
                        enhancedProducts.add(product);
                        System.out.println("⚠️ Không parse được ID từ: " + idObj);
                    }
                }

                responseMap.put("products", enhancedProducts);
                System.out.println("✅ Enhanced " + enhancedProducts.size() + " products");
            }

            return responseMap;

        } catch (Exception e) {
            System.err.println("❌ Error enhancing product response: " + e.getMessage());
            e.printStackTrace();

            // Trả về response gốc nếu có lỗi
            try {
                return objectMapper.readValue(aiJsonResponse, Map.class);
            } catch (Exception ex) {
                Map<String, Object> fallback = new LinkedHashMap<>();
                fallback.put("message", aiJsonResponse);
                fallback.put("products", List.of());
                fallback.put("follow_up_question", "");
                fallback.put("need_human_support", true);
                return fallback;
            }
        }
    }

    // Kiểm tra câu hỏi tìm sản phẩm
    private boolean isProductSearchQuestion(String question) {
        String q = question.toLowerCase();
        return q.contains("có sản phẩm") ||
                q.contains("có bán") ||
                q.contains("có mẫu") ||
                q.contains("tìm sản phẩm") ||
                q.contains("kiếm sản phẩm") ||
                q.contains("tìm kiếm") ||
                q.contains("có không") ||
                q.contains("sản phẩm nào") ||
                (q.contains("áo") && q.contains("cotton") && q.contains("20%"));
    }

    // Tìm sản phẩm trực tiếp từ DB
    private Map<String, Object> searchProductDirectly(String question) {
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
            return buildProductResponseMap(foundProducts, question);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tìm sản phẩm trực tiếp: " + e.getMessage());
            return null;
        }
    }

    // Trích xuất keyword
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
                .replace("nào", "")
                .replace("gì", "")
                .replaceAll("\\s+", " ")
                .trim();

        return cleaned.split(" ");
    }

    // Build response map (không phải JSON string)
    private Map<String, Object> buildProductResponseMap(List<SanPham> products, String originalQuestion) {
        Map<String, Object> response = new LinkedHashMap<>();
        List<Map<String, Object>> productList = new ArrayList<>();

        for (SanPham sp : products) {
            Map<String, Object> product = new LinkedHashMap<>();

            // ✅ ĐÚNG CẤU TRÚC FE CẦN
            product.put("id", sp.getId());
            product.put("tenSanPham", sp.getTenSanPham());  // TRÙNG VỚI FE
            product.put("name", sp.getTenSanPham());        // Giữ cho tương thích

            // Lấy giá nhỏ nhất từ chi tiết sản phẩm
            BigDecimal minPrice = BigDecimal.ZERO;
            if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {
                minPrice = sp.getChiTietSanPham().stream()
                        .map(ChiTietSanPham::getGiaBan)
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
            }
            product.put("price", minPrice);

            // ✅ Lấy hình ảnh - TRÙNG TÊN TRƯỜNG VỚI FE
            List<String> images = new ArrayList<>();
            if (sp.getChiTietSanPham() != null) {
                for (ChiTietSanPham ctsp : sp.getChiTietSanPham()) {
                    if (ctsp.getAnhs() != null && !ctsp.getAnhs().isEmpty()) {
                        for (Anh anh : ctsp.getAnhs()) {
                            if (anh.getDuongDanAnh() != null && !anh.getDuongDanAnh().isEmpty()) {
                                images.add(anh.getDuongDanAnh());
                            }
                        }
                    }
                }
            }

            // Nếu không có ảnh, thêm placeholder
            if (images.isEmpty()) {
                images.add("/placeholder.png");
            }

            product.put("hinhAnhSanPham", images);  // QUAN TRỌNG: đúng tên FE dùng
            product.put("image", images.get(0));    // Giữ cho tương thích

            // Thêm thông tin khác
            product.put("link", "/productDetail/" + sp.getId());

            // Lấy màu sắc từ chi tiết sản phẩm
            String colors = "";
            if (sp.getChiTietSanPham() != null) {
                colors = sp.getChiTietSanPham().stream()
                        .filter(ctsp -> ctsp.getMauSac() != null)
                        .map(ctsp -> ctsp.getMauSac().getTenMauSac())
                        .distinct()
                        .collect(Collectors.joining(", "));
            }
            product.put("color", colors.isEmpty() ? "" : colors);

            product.put("size_suggestion", ""); // Có thể tính toán sau

            productList.add(product);

            // Log để debug
            System.out.println("✅ Product: " + sp.getTenSanPham());
            System.out.println("   Images count: " + images.size());
            System.out.println("   Price: " + minPrice);
        }

        // Set message và các trường khác
        if (products.size() == 1) {
            response.put("message", "Dạ có ạ! Bên em có mẫu " + products.get(0).getTenSanPham() +
                    ". Chất liệu cao cấp, mặc rất thoải mái. Anh/chị muốn em tư vấn thêm về size hay màu sắc không ạ?");
        } else {
            response.put("message", "Dạ có ạ! Em tìm thấy " + products.size() +
                    " sản phẩm phù hợp. Anh/chị muốn xem chi tiết sản phẩm nào ạ?");
        }

        response.put("products", productList);
        response.put("follow_up_question", "Anh/chị quan tâm đến sản phẩm nào nhất ạ?");
        response.put("need_human_support", false);

        System.out.println("✅ Built response with " + productList.size() + " products");

        return response;
    }
}