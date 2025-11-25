package com.example.the_autumn.service;

import com.example.the_autumn.model.request.CalculateShippingFeeRequest;
import com.example.the_autumn.model.response.ShippingFeeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCalculatorService {

    private final RestTemplate restTemplate;
    private final AddressMappingService addressMappingService;

    @Value("${shipping.ghn.token:}")
    private String ghnToken;

    @Value("${shipping.ghn.shop-id:}")
    private String ghnShopId;

    @Value("${shipping.ghtk.token:}")
    private String ghtkToken;

    @Value("${shipping.fallback.enabled:true}")
    private boolean fallbackEnabled;

    public ShippingFeeResponse calculateShippingFee(CalculateShippingFeeRequest request) {
        try {
            log.info("Calculating shipping fee for provider: {}", request.getDonViVanChuyen());
            log.info("From: Tinh={}, Quan={}", request.getIdTinhGui(), request.getIdQuanGui());
            log.info("To: Tinh={}, Quan={}", request.getIdTinhNhan(), request.getIdQuanNhan());

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
                    return calculateFallbackFee(request);
            }
        } catch (Exception e) {
            log.error("Lỗi tính phí vận chuyển: {}", e.getMessage(), e);
            return calculateFallbackFee(request);
        }
    }

    private ShippingFeeResponse calculateGHNFee(CalculateShippingFeeRequest request,
                                                AddressMappingService.ShippingMappedAddress mappedAddress) {
        try {
            log.info("🔄 Calculating GHN fee for: {}", mappedAddress.getQuanHuyenName());

            // Sử dụng mapped district ID hoặc fallback
            Integer ghnDistrictId = mappedAddress.getGhnDistrictId() != null ?
                    mappedAddress.getGhnDistrictId() : request.getIdQuanNhan();

            if (ghnDistrictId == null) {
                log.error("❌ Không có GHN District ID");
                return calculateFallbackFee(request);
            }

            Integer serviceId = getGHNServiceId(ghnDistrictId);

            if (serviceId == null) {
                log.error("❌ Cannot get GHN service_id");
                return calculateFallbackFee(request);
            }

            String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";

            Map<String, Object> requestBody = new HashMap<>();

            requestBody.put("from_district_id", 1442); // Quận Cầu Giấy - GHN ID
            requestBody.put("from_ward_code", "21008");

            requestBody.put("service_id", serviceId);
            requestBody.put("service_type_id", null);

            requestBody.put("to_district_id", ghnDistrictId);

            String toWardCode = mappedAddress.getGhnWardCode() != null ?
                    mappedAddress.getGhnWardCode() : "21010";
            requestBody.put("to_ward_code", toWardCode);

            // Kích thước và trọng lượng
            int totalWeight = calculateTotalWeight(request.getItems());
            requestBody.put("weight", totalWeight);

            // Tính kích thước dựa trên items
            int maxLength = request.getItems().stream()
                    .mapToInt(item -> item.getParsedChieuDai())
                    .max().orElse(20);
            int maxWidth = request.getItems().stream()
                    .mapToInt(item -> item.getParsedChieuRong())
                    .max().orElse(20);
            int totalHeight = request.getItems().stream()
                    .mapToInt(item -> item.getParsedChieuCao() * item.getSoLuong())
                    .sum();

            requestBody.put("length", maxLength);
            requestBody.put("width", maxWidth);
            requestBody.put("height", Math.min(totalHeight, 200));

            // Giá trị bảo hiểm
            BigDecimal totalValue = calculateTotalValue(request.getItems());
            requestBody.put("insurance_value", totalValue.intValue());
            requestBody.put("cod_failed_amount", 0);
            requestBody.put("coupon", null);

            // Danh sách items - GHN YÊU CẦU
            List<Map<String, Object>> itemList = new ArrayList<>();
            for (CalculateShippingFeeRequest.ShippingItem item : request.getItems()) {
                Map<String, Object> itemMap = new HashMap<>();
                itemMap.put("name", "ITEM-" + item.getIdChiTietSanPham());
                itemMap.put("quantity", item.getSoLuong());
                itemMap.put("height", item.getParsedChieuCao());
                itemMap.put("weight", item.getParsedKhoiLuong());
                itemMap.put("length", item.getParsedChieuDai());
                itemMap.put("width", item.getParsedChieuRong());
                itemList.add(itemMap);
            }
            requestBody.put("items", itemList);

            // Headers
            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", ghnToken);
            headers.set("ShopId", ghnShopId);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            log.info("📤 GHN Fee Request - District: {}, Ward: {}", ghnDistrictId, toWardCode);

            // Gọi API
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

                // Thời gian dự kiến
                if (data.get("expected_delivery_time") != null) {
                    result.setThoiGianDuKien(data.get("expected_delivery_time").toString());
                }

                log.info("✅ Tính phí GHN thành công: {} VND", fee);
                return result;
            } else {
                log.warn("⚠️ GHN API trả về code không thành công: {}", resBody);
            }

        } catch (Exception e) {
            log.error("❌ GHN Fee Exception: {}", e.getMessage(), e);
        }

        return calculateFallbackFee(request);
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

        // Miễn phí ship cho đơn >= 1 triệu hoặc >= 10 sản phẩm
        if (totalValue.compareTo(BigDecimal.valueOf(1000000)) >= 0 || totalQuantity >= 10) {
            fee = BigDecimal.ZERO;
        }
        // 15k cho đơn >= 500k hoặc >= 5 sản phẩm
        else if (totalValue.compareTo(BigDecimal.valueOf(500000)) >= 0 || totalQuantity >= 5) {
            fee = BigDecimal.valueOf(15000);
        }
        // 30k cho các đơn còn lại
        else {
            fee = BigDecimal.valueOf(30000);
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

    // Helper methods
    private int calculateTotalWeight(List<CalculateShippingFeeRequest.ShippingItem> items) {
        return items.stream()
                .mapToInt(item -> item.getParsedKhoiLuong() * item.getSoLuong())
                .sum();
    }

    private BigDecimal calculateTotalValue(List<CalculateShippingFeeRequest.ShippingItem> items) {
        return items.stream()
                .map(item -> item.getGiaBan().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Integer getGHNServiceId(Integer toDistrictId) {
        try {
            String url = "https://online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/available-services";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("shop_id", Integer.parseInt(ghnShopId));
            requestBody.put("from_district", 1442); // Quận Cầu Giấy - GHN ID
            requestBody.put("to_district", toDistrictId);

            HttpHeaders headers = new HttpHeaders();
            headers.set("Token", ghnToken);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> resBody = response.getBody();

            if (resBody != null && Integer.valueOf(resBody.get("code").toString()) == 200) {
                List<Map<String, Object>> services = (List<Map<String, Object>>) resBody.get("data");
                if (services != null && !services.isEmpty()) {
                    return (Integer) services.get(0).get("service_id");
                }
            }
        } catch (Exception e) {
            log.error("Lỗi khi lấy GHN service_id: {}", e.getMessage());
        }

        return 53320; // Standard Express
    }
}