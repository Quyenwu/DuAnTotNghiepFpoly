package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ThongKeRequest;
import com.example.the_autumn.service.ThongKeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/thong-ke")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174","http://172.20.10.2:5173"})
public class ThongKeController {

    private final ThongKeService thongKeService;

    /**
     * Lấy tất cả thống kê trong một request
     * @param period: "day", "week", "month", "year"
     */
    @GetMapping("/all")
    public ResponseEntity<ThongKeRequest> getAllStatistics(
            @RequestParam(defaultValue = "month") String period
    ) {
        ThongKeRequest result = thongKeService.getAllStatistics(period);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy thống kê tổng quan (hôm nay, tuần, tháng, năm)
     */
    @GetMapping("/summary")
    public ResponseEntity<ThongKeRequest.SummaryData> getSummary() {
        ThongKeRequest.SummaryData result = thongKeService.getSummary();
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy dữ liệu biểu đồ doanh thu
     * @param type: "week" (tuần trong tháng), "year" (tháng trong năm)
     */
    @GetMapping("/revenue-chart")
    public ResponseEntity<List<ThongKeRequest.ChartData>> getRevenueChart(
            @RequestParam(defaultValue = "week") String type
    ) {
        List<ThongKeRequest.ChartData> result = thongKeService.getRevenueChart(type);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy top sản phẩm bán chạy
     * @param period: "day", "week", "month", "year"
     * @param limit: số lượng sản phẩm (mặc định 5)
     */
    @GetMapping("/top-products")
    public ResponseEntity<List<ThongKeRequest.ProductData>> getTopProducts(
            @RequestParam(defaultValue = "month") String period,
            @RequestParam(defaultValue = "5") int limit
    ) {
        List<ThongKeRequest.ProductData> result = thongKeService.getTopProducts(period, limit);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy phân bổ trạng thái đơn hàng
     * @param period: "day", "week", "month", "year"
     */
    @GetMapping("/order-status")
    public ResponseEntity<List<ThongKeRequest.StatusData>> getOrderStatus(
            @RequestParam(defaultValue = "month") String period
    ) {
        List<ThongKeRequest.StatusData> result = thongKeService.getOrderStatus(period);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy phân phối theo kênh (Online/Tại quầy)
     * @param period: "day", "week", "month", "year"
     */
    @GetMapping("/channels")
    public ResponseEntity<List<ThongKeRequest.ChannelData>> getChannelDistribution(
            @RequestParam(defaultValue = "month") String period
    ) {
        List<ThongKeRequest.ChannelData> result = thongKeService.getChannelDistribution(period);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy thống kê theo brand/nhà sản xuất
     * @param period: "day", "week", "month", "year"
     */
    @GetMapping("/brands")
    public ResponseEntity<List<ThongKeRequest.BrandData>> getBrandStatistics(
            @RequestParam(defaultValue = "month") String period
    ) {
        List<ThongKeRequest.BrandData> result = thongKeService.getBrandStatistics(period);
        return ResponseEntity.ok(result);
    }

    /**
     * Lấy bảng thống kê chi tiết (hôm nay, tuần, tháng, năm)
     */
    @GetMapping("/detail-table")
    public ResponseEntity<List<ThongKeRequest.DetailData>> getDetailTable() {
        List<ThongKeRequest.DetailData> result = thongKeService.getDetailTable();
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bao-cao/pdf")
    public ResponseEntity<byte[]> exportBaoCaoPDF(
            @RequestParam(defaultValue = "month") String period
    ) {
        byte[] pdfData = thongKeService.generateThongKePDF(period);

        return ResponseEntity.ok()
                .header("Content-Type", "application/pdf")
                .header("Content-Disposition", "attachment; filename=bao_cao_thong_ke.pdf")
                .body(pdfData);
    }

    // ✅ Top sản phẩm bán chạy nhất
    @GetMapping("/top-selling-products")
    public ResponseEntity<List<ThongKeRequest.TopProductResponse>> getTopSellingProducts() {
        return ResponseEntity.ok(thongKeService.getTopSellingProducts());
    }

    @GetMapping("/low-stock-products")
    public ResponseEntity<List<ThongKeRequest.LowStockProductResponse>> getLowStockProducts() {
        return ResponseEntity.ok(thongKeService.getLowStockProducts());
    }


}