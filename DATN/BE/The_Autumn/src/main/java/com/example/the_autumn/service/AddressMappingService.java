package com.example.the_autumn.service;

import com.example.the_autumn.entity.QuanHuyen;
import com.example.the_autumn.entity.ShippingAddressMapping;
import com.example.the_autumn.entity.TinhThanh;
import com.example.the_autumn.repository.QuanHuyenRepository;
import com.example.the_autumn.repository.ShippingAddressMappingRepository;
import com.example.the_autumn.repository.TinhThanhRepository;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressMappingService {

    private final ShippingAddressMappingRepository mappingRepository;
    private final TinhThanhRepository tinhThanhRepository;
    private final QuanHuyenRepository quanHuyenRepository;

    public ShippingMappedAddress convertToShippingAddress(
            Integer tinhThanhId,
            Integer quanHuyenId,
            String provider) {

        ShippingMappedAddress result = new ShippingMappedAddress();

        try {
            // Lấy thông tin tỉnh, quận từ database
            TinhThanh tinhThanh = tinhThanhRepository.findById(tinhThanhId)
                    .orElseThrow(() -> new RuntimeException("Tỉnh/thành không tồn tại"));

            QuanHuyen quanHuyen = quanHuyenRepository.findById(quanHuyenId)
                    .orElseThrow(() -> new RuntimeException("Quận/huyện không tồn tại"));

            result.setTinhThanhName(tinhThanh.getTenTinh());
            result.setQuanHuyenName(quanHuyen.getTenQuan());

            // Tìm mapping trong database
            Optional<ShippingAddressMapping> mapping = mappingRepository
                    .findByTinhThanhIdAndQuanHuyenId(tinhThanhId, quanHuyenId);

            if (mapping.isPresent()) {
                ShippingAddressMapping map = mapping.get();

                if ("GHN".equalsIgnoreCase(provider) && map.getGhnDistrictId() != null) {
                    result.setGhnDistrictId(map.getGhnDistrictId());
                    result.setGhnWardCode(map.getGhnWardCode());
                    log.info("✅ Sử dụng GHN mapping: districtId={}, wardCode={}",
                            map.getGhnDistrictId(), map.getGhnWardCode());
                }

                if ("GHTK".equalsIgnoreCase(provider)) {
                    result.setGhtkProvinceName(map.getGhtkProvinceName() != null ?
                            map.getGhtkProvinceName() : tinhThanh.getTenTinh());
                    result.setGhtkDistrictName(map.getGhtkDistrictName() != null ?
                            map.getGhtkDistrictName() : quanHuyen.getTenQuan());
                    log.info("✅ Sử dụng GHTK mapping: province={}, district={}",
                            result.getGhtkProvinceName(), result.getGhtkDistrictName());
                }
            } else {
                // Fallback: Sử dụng mapping mặc định dựa trên tên
                result.setGhtkProvinceName(tinhThanh.getTenTinh());
                result.setGhtkDistrictName(quanHuyen.getTenQuan());

                log.warn("⚠️ Không tìm thấy mapping cho: {}-{}, sử dụng fallback",
                        tinhThanh.getTenTinh(), quanHuyen.getTenQuan());
            }

        } catch (Exception e) {
            log.error("❌ Lỗi mapping địa chỉ: {}", e.getMessage());
            // Vẫn trả về thông tin cơ bản để fallback có thể hoạt động
            result.setGhtkProvinceName("Hà Nội");
            result.setGhtkDistrictName("Quận Ba Đình");
        }

        return result;
    }

    @Getter
    @Setter
    public static class ShippingMappedAddress {
        private String tinhThanhName;
        private String quanHuyenName;
        private Integer ghnDistrictId;
        private String ghnWardCode;
        private String ghtkProvinceName;
        private String ghtkDistrictName;
    }
}