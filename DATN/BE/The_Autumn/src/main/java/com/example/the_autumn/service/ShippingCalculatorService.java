package com.example.the_autumn.service;

import com.example.the_autumn.model.request.CalculateShippingFeeRequest;
import com.example.the_autumn.model.response.ShippingFeeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCalculatorService {

    private final RestTemplate restTemplate;
    private final AddressMappingService addressMappingService;
    private final GHNServiceCache ghnServiceCache;

    @Value("${shipping.ghn.token:}")
    private String ghnToken;

    @Value("${shipping.ghn.shop-id:}")
    private String ghnShopId;

    @Value("${shipping.ghtk.token:}")
    private String ghtkToken;

    @Value("${shipping.fallback.enabled:true}")
    private boolean fallbackEnabled;

    // Cache cho service availability
    @Component
    @Slf4j
    public static class GHNServiceCache {

        private final Map<Integer, List<Integer>> districtServiceCache = new ConcurrentHashMap<>();
        private final Map<Integer, Long> cacheTimestamps = new ConcurrentHashMap<>();
        private static final long CACHE_DURATION = 30 * 60 * 1000; // 30 phút

        public List<Integer> getAvailableServices(Integer districtId, String ghnToken, String ghnShopId) {
            // Kiểm tra cache
            if (isCacheValid(districtId)) {
                return districtServiceCache.get(districtId);
            }

            // Gọi API để lấy services
            List<Integer> services = fetchServicesFromAPI(districtId, ghnToken, ghnShopId);
            districtServiceCache.put(districtId, services);
            cacheTimestamps.put(districtId, System.currentTimeMillis());

            return services;
        }

        private List<Integer> fetchServicesFromAPI(Integer districtId, String ghnToken, String ghnShopId) {
            try {
                String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";

                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("shop_id", Integer.parseInt(ghnShopId));
                requestBody.put("from_district", 1442);
                requestBody.put("to_district", districtId);

                HttpHeaders headers = new HttpHeaders();
                headers.set("Token", ghnToken);
                headers.setContentType(MediaType.APPLICATION_JSON);

                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

                Map<String, Object> resBody = response.getBody();

                if (resBody != null && Integer.valueOf(resBody.get("code").toString()) == 200) {
                    List<Map<String, Object>> services = (List<Map<String, Object>>) resBody.get("data");
                    if (services != null && !services.isEmpty()) {
                        List<Integer> availableServices = services.stream()
                                .map(service -> (Integer) service.get("service_id"))
                                .collect(Collectors.toList());
                        log.info("✅ Found {} available services for district {}: {}",
                                availableServices.size(), districtId, availableServices);
                        return availableServices;
                    }
                } else {
                    log.warn("⚠️ GHN services API returned error: {}", resBody);
                }
            } catch (Exception e) {
                log.error("❌ Error fetching GHN services: {}", e.getMessage());
            }

            // Return default services nếu không lấy được
            List<Integer> defaultServices = Arrays.asList(53320, 53321, 53322);
            log.info("🔄 Using default services for district {}: {}", districtId, defaultServices);
            return defaultServices;
        }

        private boolean isCacheValid(Integer districtId) {
            Long timestamp = cacheTimestamps.get(districtId);
            return timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_DURATION;
        }

        public void clearCache() {
            districtServiceCache.clear();
            cacheTimestamps.clear();
        }
    }

    public ShippingFeeResponse calculateShippingFee(CalculateShippingFeeRequest request) {
        try {
            log.info("🚚 Calculating shipping fee for provider: {}", request.getDonViVanChuyen());
            log.info("📍 From: Tinh={}, Quan={}", request.getIdTinhGui(), request.getIdQuanGui());
            log.info("📍 To: Tinh={}, Quan={}", request.getIdTinhNhan(), request.getIdQuanNhan());

            // Chuyển đổi địa chỉ sang định dạng shipping provider
            AddressMappingService.ShippingMappedAddress mappedAddress =
                    addressMappingService.convertToShippingAddress(
                            request.getIdTinhNhan(),
                            request.getIdQuanNhan(),
                            request.getDonViVanChuyen()
                    );

            switch (request.getDonViVanChuyen().toUpperCase()) {
                case "GHN":
                    return calculateGHNFee(request, mappedAddress);
                case "GHTK":
                    return calculateGHTKFee(request, mappedAddress);
                default:
                    log.warn("⚠️ Unknown shipping provider: {}, using fallback", request.getDonViVanChuyen());
                    return calculateFallbackFee(request);
            }
        } catch (Exception e) {
            log.error("❌ Lỗi tính phí vận chuyển: {}", e.getMessage(), e);
            return calculateFallbackFee(request);
        }
    }

    private ShippingFeeResponse calculateGHNFee(CalculateShippingFeeRequest request,
                                                AddressMappingService.ShippingMappedAddress mappedAddress) {

        Integer ghnDistrictId = mappedAddress.getGhnDistrictId() != null ?
                mappedAddress.getGhnDistrictId() : request.getIdQuanNhan();

        if (ghnDistrictId == null) {
            log.error("❌ Không có GHN District ID");
            return calculateFallbackFee(request);
        }

        // Lấy danh sách services từ cache
        List<Integer> availableServices = ghnServiceCache.getAvailableServices(
                ghnDistrictId, ghnToken, ghnShopId
        );

        if (availableServices.isEmpty()) {
            log.warn("⚠️ No available GHN services for district: {}", ghnDistrictId);
            return calculateFallbackFee(request);
        }

        log.info("🔄 Trying {} GHN services for district {}: {}",
                availableServices.size(), ghnDistrictId, availableServices);

        // Thử từng service
        for (Integer serviceId : availableServices) {
            try {
                ShippingFeeResponse result = calculateGHNFeeWithService(
                        request, mappedAddress, serviceId
                );
                if (result != null && result.getSuccess()) {
                    log.info("✅ GHN service {} succeeded for district {}", serviceId, ghnDistrictId);
                    return result;
                }
            } catch (Exception e) {
                log.warn("⚠️ GHN service {} failed for district {}: {}",
                        serviceId, ghnDistrictId, e.getMessage());
            }
        }

        log.error("❌ All {} GHN services failed for district: {}",
                availableServices.size(), ghnDistrictId);
        return calculateFallbackFee(request);
    }

    private ShippingFeeResponse calculateGHNFeeWithService(CalculateShippingFeeRequest request,
                                                           AddressMappingService.ShippingMappedAddress mappedAddress,
                                                           Integer serviceId) {
        try {
            log.debug("🔄 Calculating GHN fee with service: {} for district: {}",
                    serviceId, mappedAddress.getGhnDistrictId());

            Integer ghnDistrictId = mappedAddress.getGhnDistrictId() != null ?
                    mappedAddress.getGhnDistrictId() : request.getIdQuanNhan();

            if (ghnDistrictId == null) {
                return null;
            }

            String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";

            Map<String, Object> requestBody = buildGHNFeeRequestBody(request, mappedAddress, serviceId, ghnDistrictId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", ghnToken);
            headers.set("ShopId", ghnShopId);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("📤 GHN Fee Request - District: {}, Ward: {}, Service: {}",
                    ghnDistrictId, requestBody.get("to_ward_code"), serviceId);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> resBody = response.getBody();

            if (resBody != null && Integer.valueOf(resBody.get("code").toString()) == 200) {
                Map<String, Object> data = (Map<String, Object>) resBody.get("data");
                BigDecimal fee = new BigDecimal(data.get("total").toString());

                ShippingFeeResponse result = new ShippingFeeResponse();
                result.setSuccess(true);
                result.setPhiVanChuyen(fee);
                result.setPhiVanChuyenGoc(fee);
                result.setDonViVanChuyen("GHN");
                result.setMessage("Tính phí GHN thành công");

                if (data.get("expected_delivery_time") != null) {
                    result.setThoiGianDuKien(data.get("expected_delivery_time").toString());
                } else {
                    result.setThoiGianDuKien("2-4 ngày");
                }

                log.info("✅ GHN fee calculated successfully: {} VND (Service: {})", fee, serviceId);
                return result;
            } else {
                log.warn("⚠️ GHN API returned error for service {}: {}", serviceId, resBody);
                // Nếu lỗi "route not found", return null để thử service khác
                if (resBody != null && resBody.get("message") != null &&
                        resBody.get("message").toString().contains("route not found")) {
                    return null;
                }
            }

        } catch (Exception e) {
            log.error("❌ GHN Fee Exception for service {}: {}", serviceId, e.getMessage());
            // Nếu lỗi "route not found", return null để thử service khác
            if (e.getMessage() != null && e.getMessage().contains("route not found")) {
                return null;
            }
            throw e; // Re-throw để retry mechanism hoạt động
        }

        return null;
    }

    private Map<String, Object> buildGHNFeeRequestBody(CalculateShippingFeeRequest request,
                                                       AddressMappingService.ShippingMappedAddress mappedAddress,
                                                       Integer serviceId, Integer ghnDistrictId) {
        Map<String, Object> requestBody = new HashMap<>();

        // Thông tin địa chỉ
        requestBody.put("from_district_id", 1442); // Quận Cầu Giấy - GHN ID
        requestBody.put("from_ward_code", "21008");
        requestBody.put("service_id", serviceId);
        requestBody.put("to_district_id", ghnDistrictId);

        String toWardCode = mappedAddress.getGhnWardCode() != null ?
                mappedAddress.getGhnWardCode() : "21010";
        requestBody.put("to_ward_code", toWardCode);

        // Kích thước và trọng lượng
        int totalWeight = calculateTotalWeight(request.getItems());
        requestBody.put("weight", Math.max(totalWeight, 100)); // Tối thiểu 100g

        // Tính kích thước dựa trên items
        int maxLength = request.getItems().stream()
                .mapToInt(item -> Math.max(item.getParsedChieuDai(), 1))
                .max().orElse(20);
        int maxWidth = request.getItems().stream()
                .mapToInt(item -> Math.max(item.getParsedChieuRong(), 1))
                .max().orElse(15);
        int totalHeight = request.getItems().stream()
                .mapToInt(item -> Math.max(item.getParsedChieuCao(), 1) * item.getSoLuong())
                .sum();

        requestBody.put("length", Math.max(maxLength, 1));
        requestBody.put("width", Math.max(maxWidth, 1));
        requestBody.put("height", Math.max(Math.min(totalHeight, 200), 1));

        // Giá trị bảo hiểm
        BigDecimal totalValue = calculateTotalValue(request.getItems());
        requestBody.put("insurance_value", totalValue.intValue());
        requestBody.put("cod_failed_amount", 0);
        requestBody.put("coupon", null);

        // Danh sách items - GHN YÊU CẦU
        List<Map<String, Object>> itemList = new ArrayList<>();
        for (CalculateShippingFeeRequest.ShippingItem item : request.getItems()) {
            Map<String, Object> itemMap = new HashMap<>();
            itemMap.put("name", "SP-" + item.getIdChiTietSanPham());
            itemMap.put("quantity", item.getSoLuong());
            itemMap.put("height", Math.max(item.getParsedChieuCao(), 1));
            itemMap.put("weight", Math.max(item.getParsedKhoiLuong(), 100));
            itemMap.put("length", Math.max(item.getParsedChieuDai(), 1));
            itemMap.put("width", Math.max(item.getParsedChieuRong(), 1));
            itemMap.put("category", Map.of(
                    "level1", "Đồ điện tử"
            ));
            itemList.add(itemMap);
        }
        requestBody.put("items", itemList);

        return requestBody;
    }

    private ShippingFeeResponse calculateGHTKFee(CalculateShippingFeeRequest request,
                                                 AddressMappingService.ShippingMappedAddress mappedAddress) {
        try {
            // Sử dụng mapped province và district name
            String pickProvince = "Hà Nội";
            String pickDistrict = "Quận Cầu Giấy";
            String toProvince = mappedAddress.getGhtkProvinceName();
            String toDistrict = mappedAddress.getGhtkDistrictName();

            int totalWeight = calculateTotalWeight(request.getItems());
            BigDecimal totalValue = calculateTotalValue(request.getItems());

            String url = String.format(
                    "https://services.giaohangtietkiem.vn/services/shipment/fee?pick_province=%s&pick_district=%s&province=%s&district=%s&weight=%d&value=%s",
                    pickProvince, pickDistrict, toProvince, toDistrict, totalWeight, totalValue.intValue()
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", ghtkToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<?> entity = new HttpEntity<>(headers);

            log.info("🚚 GHTK from: {}-{} to: {}-{}",
                    pickProvince, pickDistrict, toProvince, toDistrict);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            Map<String, Object> resBody = response.getBody();

            if (resBody != null && Boolean.TRUE.equals(resBody.get("success"))) {
                Map<String, Object> feeData = (Map<String, Object>) resBody.get("fee");
                BigDecimal fee = BigDecimal.valueOf(((Number) feeData.get("fee")).longValue());

                ShippingFeeResponse result = new ShippingFeeResponse();
                result.setSuccess(true);
                result.setPhiVanChuyen(fee);
                result.setPhiVanChuyenGoc(fee);
                result.setDonViVanChuyen("GHTK");
                result.setMessage("Tính phí GHTK thành công");
                result.setThoiGianDuKien("2-4 ngày");

                log.info("✅ Tính phí GHTK thành công: {} VND", fee);
                return result;
            }
        } catch (Exception e) {
            log.error("❌ GHTK API Exception: {}", e.getMessage(), e);
        }

        return calculateFallbackFee(request);
    }

    private ShippingFeeResponse calculateFallbackFee(CalculateShippingFeeRequest request) {
        BigDecimal totalValue = calculateTotalValue(request.getItems());
        int totalQuantity = request.getItems().stream()
                .mapToInt(CalculateShippingFeeRequest.ShippingItem::getSoLuong)
                .sum();

        BigDecimal fee;

        // Logic tính phí fallback
        if (totalValue.compareTo(BigDecimal.valueOf(1000000)) >= 0 || totalQuantity >= 10) {
            fee = BigDecimal.ZERO;
            log.info("🎉 Miễn phí ship: Đơn >= 1tr hoặc >= 10 sản phẩm");
        }
        // 15k cho đơn >= 500k hoặc >= 5 sản phẩm
        else if (totalValue.compareTo(BigDecimal.valueOf(500000)) >= 0 || totalQuantity >= 5) {
            fee = BigDecimal.valueOf(15000);
            log.info("💰 Phí ship 15k: Đơn >= 500k hoặc >= 5 sản phẩm");
        }
        // 30k cho các đơn còn lại
        else {
            fee = BigDecimal.valueOf(30000);
            log.info("💰 Phí ship 30k: Đơn thường");
        }

        ShippingFeeResponse result = new ShippingFeeResponse();
        result.setSuccess(true);
        result.setPhiVanChuyen(fee);
        result.setPhiVanChuyenGoc(fee);
        result.setDonViVanChuyen(request.getDonViVanChuyen());
        result.setThoiGianDuKien("3-5 ngày");
        result.setMessage("Sử dụng phí vận chuyển mặc định");

        log.info("ℹ️ Sử dụng phí fallback: {} VND (Tổng giá trị: {}, Số lượng: {})",
                fee, totalValue, totalQuantity);

        return result;
    }

    private int calculateTotalWeight(List<CalculateShippingFeeRequest.ShippingItem> items) {
        int total = items.stream()
                .mapToInt(item -> Math.max(item.getParsedKhoiLuong(), 100) * item.getSoLuong())
                .sum();
        return Math.max(total, 100); // Tối thiểu 100g
    }

    private BigDecimal calculateTotalValue(List<CalculateShippingFeeRequest.ShippingItem> items) {
        return items.stream()
                .map(item -> item.getGiaBan().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void clearGHNServiceCache() {
        ghnServiceCache.clearCache();
        log.info("🧹 Đã xóa cache GHN services");
    }

    public List<Integer> testGHNServices(Integer districtId) {
        return ghnServiceCache.getAvailableServices(districtId, ghnToken, ghnShopId);
    }
}