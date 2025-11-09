package com.example.the_autumn.model.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ThongKeRequest {
    private SummaryData summary;

    // Biểu đồ doanh thu
    private List<ChartData> revenueChart;

    // Top sản phẩm
    private List<ProductData> topProducts;

    // Trạng thái đơn hàng
    private List<StatusData> orderStatus;

    // Kênh phân phối
    private List<ChannelData> channels;

    // Thống kê brand
    private List<BrandData> brands;

    // Bảng chi tiết
    private List<DetailData> detailTable;

    // ==================== INNER CLASSES ====================

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class SummaryData {
        private PeriodStats today;
        private PeriodStats week;
        private PeriodStats month;
        private PeriodStats year;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PeriodStats {
        private Double revenue;
        private Integer orders;
        private Integer products;
        private String growth;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChartData {
        private String period; // "Tuần 1", "Tháng 1"
        private Double value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ProductData {
        private Integer rank;
        private Integer productId;
        private String name;
        private Integer sold;
        private Double revenue;
        private String price;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StatusData {
        private String name;
        private Integer value;
        private String color;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ChannelData {
        private String name;
        private Integer value;
        private String color;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class BrandData {
        private String name;
        private Double value;
        private String color;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class DetailData {
        private String period;
        private String revenue;
        private Integer orders;
        private String avgValue;
        private String growth;
        private String status;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TopProductResponse {
        private Long id;
        private String tenSanPham;
        private String anh;
        private Double giaBan;
        private Integer tongSoLuongBan;
        private Integer tongDoanhThu;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LowStockProductResponse {
        private Long id;
        private String tenSanPham;
        private Integer soLuongTon;
    }

}
