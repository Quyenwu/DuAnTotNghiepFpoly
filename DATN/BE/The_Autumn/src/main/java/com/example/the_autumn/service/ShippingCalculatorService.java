package com.example.the_autumn.service;

import com.example.the_autumn.model.request.CalculateShippingFeeRequest;
import com.example.the_autumn.model.response.ShippingFeeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShippingCalculatorService {

    private final RestTemplate restTemplate;

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
            switch (request.getDonViVanChuyen().toUpperCase()) {
                case "GHN":
                    return calculateGHNFee(request);
                case "GHTK":
                    return calculateGHTKFee(request);
                default:
                    return calculateFallbackFee(request);
            }
        } catch (Exception e) {
            log.error("Lỗi tính phí vận chuyển: {}", e.getMessage());
            return calculateFallbackFee(request);
        }
    }

    private ShippingFeeResponse calculateGHNFee(CalculateShippingFeeRequest request) {
        try {
            String url = "https://dev-online-gateway.ghn.vn/shiip/public-api/v2/shipping-order/fee";

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("from_district_id", request.getIdQuanGui());
            requestBody.put("from_ward_code", request.getIdPhuongGui().toString());
            requestBody.put("to_district_id", request.getIdQuanNhan());
            requestBody.put("to_ward_code", request.getIdPhuongNhan().toString());
            requestBody.put("weight", calculateTotalWeight(request.getItems()));
            requestBody.put("length", 20);
            requestBody.put("width", 15);
            requestBody.put("height", 10);
            requestBody.put("service_type_id", 2);
            requestBody.put("insurance_value", calculateTotalValue(request.getItems()));

            Map<String, String> headers = new HashMap<>();
            headers.put("Token", ghnToken);
            headers.put("ShopId", ghnShopId);
            headers.put("Content-Type", "application/json");

            Map<String, Object> response = restTemplate.postForObject(url, requestBody, Map.class);

            if (response != null && "200".equals(response.get("code"))) {
                Map<String, Object> data = (Map<String, Object>) response.get("data");
                BigDecimal fee = BigDecimal.valueOf((Integer) data.get("total"));

                ShippingFeeResponse result = new ShippingFeeResponse();
                result.setPhiVanChuyen(fee);
                result.setPhiVanChuyenGoc(fee);
                result.setDonViVanChuyen("GHN");
                result.setThoiGianDuKien((String) data.get("leadtime"));
                result.setSuccess(true);
                result.setMessage("Tính phí thành công");

                return result;
            }
        } catch (Exception e) {
            log.error("Lỗi GHN API: {}", e.getMessage());
        }

        return calculateFallbackFee(request);
    }

    private ShippingFeeResponse calculateGHTKFee(CalculateShippingFeeRequest request) {
        try {
            String url = "https://services.giaohangtietkiem.vn/services/shipment/fee?" +
                    "pick_province=Hà+Nội" +
                    "&pick_district=Quận+Cầu+Giấy" +
                    "&province=" + getProvinceName(request.getIdTinhNhan()) +
                    "&district=" + getDistrictName(request.getIdQuanNhan()) +
                    "&weight=" + calculateTotalWeight(request.getItems()) +
                    "&value=" + calculateTotalValue(request.getItems());

            Map<String, String> headers = new HashMap<>();
            headers.put("Token", ghtkToken);

            Map<String, Object> response = restTemplate.getForObject(url, Map.class, headers);

            if (response != null && response.get("success") != null) {
                Map<String, Object> feeData = (Map<String, Object>) response.get("fee");
                BigDecimal fee = BigDecimal.valueOf((Integer) feeData.get("fee"));

                ShippingFeeResponse result = new ShippingFeeResponse();
                result.setPhiVanChuyen(fee);
                result.setPhiVanChuyenGoc(fee);
                result.setDonViVanChuyen("GHTK");
                result.setThoiGianDuKien("2-4 ngày");
                result.setSuccess(true);
                result.setMessage("Tính phí thành công");

                return result;
            }
        } catch (Exception e) {
            log.error("Lỗi GHTK API: {}", e.getMessage());
        }

        return calculateFallbackFee(request);
    }

    private ShippingFeeResponse calculateFallbackFee(CalculateShippingFeeRequest request) {
        BigDecimal totalValue = calculateTotalValue(request.getItems());
        int totalQuantity = request.getItems().stream()
                .mapToInt(CalculateShippingFeeRequest.ShippingItem::getSoLuong)
                .sum();

        BigDecimal fee = BigDecimal.ZERO;

        if (totalValue.compareTo(BigDecimal.valueOf(1000000)) >= 0 || totalQuantity >= 10) {
            fee = BigDecimal.ZERO;
        } else if (totalValue.compareTo(BigDecimal.valueOf(500000)) >= 0 || totalQuantity >= 5) {
            fee = BigDecimal.valueOf(15000);
        } else {
            fee = BigDecimal.valueOf(30000);
        }

        ShippingFeeResponse result = new ShippingFeeResponse();
        result.setPhiVanChuyen(fee);
        result.setPhiVanChuyenGoc(fee);
        result.setDonViVanChuyen(request.getDonViVanChuyen());
        result.setThoiGianDuKien("3-5 ngày");
        result.setSuccess(true);
        result.setMessage("Sử dụng phí mặc định");

        return result;
    }

    private int calculateTotalWeight(java.util.List<CalculateShippingFeeRequest.ShippingItem> items) {
        return items.stream()
                .mapToInt(item -> (item.getKhoiLuong() != null ? item.getKhoiLuong() : 200) * item.getSoLuong())
                .sum();
    }

    private BigDecimal calculateTotalValue(java.util.List<CalculateShippingFeeRequest.ShippingItem> items) {
        return items.stream()
                .map(item -> item.getGiaBan().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String getProvinceName(Integer idTinh) {
        return "Hà Nội";
    }

    private String getDistrictName(Integer idQuan) {
        return "Quận Ba Đình";
    }
}