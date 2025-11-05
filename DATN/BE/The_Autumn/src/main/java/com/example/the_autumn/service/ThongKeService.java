package com.example.the_autumn.service;


import com.example.the_autumn.model.request.ThongKeRequest;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import com.example.the_autumn.repository.HoaDonChiTietRepository;
import com.example.the_autumn.repository.SanPhamRepository;
import com.example.the_autumn.repository.ThongKeRepository;
import com.example.the_autumn.model.request.ThongKeRequest.*;
import com.itextpdf.text.Chunk;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.ByteArrayOutputStream;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ThongKeService {




    private final ThongKeRepository thongKeRepository;
    private final NumberFormat currencyFormat = NumberFormat.getInstance(new Locale("vi", "VN"));

    // ========== METHOD MỚI: Lấy tất cả thống kê một lần ==========
    public ThongKeRequest getAllStatistics(String period) {
       ThongKeRequest dto = new ThongKeRequest();


        dto.setSummary(getSummary());
        dto.setRevenueChart(getRevenueChart(period));
        dto.setTopProducts(getTopProducts(period, 5));
        dto.setOrderStatus(getOrderStatus(period));
        dto.setChannels(getChannelDistribution(period));
        dto.setBrands(getBrandStatistics(period));
        dto.setDetailTable(getDetailTable());

        return dto;
    }

    // ========== CÁC METHOD CŨ - CHỈ ĐỔI RETURN TYPE ==========

    // 1. Lấy thống kê tổng quan
    public SummaryData getSummary() {
        LocalDate now = LocalDate.now();

        // Hôm nay
        Map<String, Object> todayStats = thongKeRepository.getSummaryStatistics(
                now, now.plusDays(1)
        );

        // Tuần này
        LocalDate startOfWeek = now.minusDays(now.getDayOfWeek().getValue() - 1);
        Map<String, Object> weekStats = thongKeRepository.getSummaryStatistics(
                startOfWeek, now.plusDays(1)
        );

        // Tháng này
        LocalDate startOfMonth = now.with(TemporalAdjusters.firstDayOfMonth());
        Map<String, Object> monthStats = thongKeRepository.getSummaryStatistics(
                startOfMonth, now.plusDays(1)
        );

        // Năm này
        LocalDate startOfYear = now.with(TemporalAdjusters.firstDayOfYear());
        Map<String, Object> yearStats = thongKeRepository.getSummaryStatistics(
                startOfYear, now.plusDays(1)
        );

        return new SummaryData(
                mapToPeriodStats(todayStats),
                mapToPeriodStats(weekStats),
                mapToPeriodStats(monthStats),
                mapToPeriodStats(yearStats)
        );
    }

    private PeriodStats mapToPeriodStats(Map<String, Object> stats) {
        return new PeriodStats(
                ((Number) stats.getOrDefault("totalRevenue", 0)).doubleValue(),
                ((Number) stats.getOrDefault("totalOrders", 0)).intValue(),
                ((Number) stats.getOrDefault("totalProducts", 0)).intValue(),
                "+0%"
        );
    }

    // 2. Lấy dữ liệu biểu đồ doanh thu
    public List<ChartData> getRevenueChart(String type) {
        LocalDate now = LocalDate.now();
        List<Map<String, Object>> data;

        switch (type) {
            case "day" -> data = thongKeRepository.getDailyRevenue(now.getMonthValue(), now.getYear());
            case "week" -> data = thongKeRepository.getWeeklyRevenue(now.getMonthValue(), now.getYear());
            case "year" -> data = thongKeRepository.getMonthlyRevenue(now.getYear());
            default -> data = thongKeRepository.getWeeklyRevenue(now.getMonthValue(), now.getYear());
        }

        return data.stream()
                .map(row -> new ChartData(
                        row.containsKey("day") ? "Ngày " + row.get("day").toString()
                                : row.containsKey("week") ? row.get("week").toString()
                                : row.get("month").toString(),
                        ((Number) row.get("revenue")).doubleValue()
                ))
                .collect(Collectors.toList());
    }



    // 3. Top sản phẩm bán chạy
    public List<ProductData> getTopProducts(String period, int limit) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(period, now);

        List<Map<String, Object>> data = thongKeRepository.getTopProducts(
                startDate,
                now.plusDays(1),
                limit
        );

        List<ProductData> products = new ArrayList<>();
        int rank = 1;

        for (Map<String, Object> row : data) {
            products.add(new ProductData(
                    rank++,
                    ((Number) row.get("productId")).intValue(),
                    row.get("name").toString(),
                    ((Number) row.get("sold")).intValue(),
                    ((Number) row.get("revenue")).doubleValue(),
                    currencyFormat.format(((Number) row.get("price")).doubleValue()) + " đ"
            ));
        }

        return products;
    }

    // 4. Phân bổ trạng thái đơn hàng
    public List<StatusData> getOrderStatus(String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(period, now);

        List<Map<String, Object>> data = thongKeRepository.getOrderStatusDistribution(
                startDate,
                now.plusDays(1)
        );

        Map<String, String> colorMap = Map.of(
                "Đã thanh toán", "#7c3aed",
                "Chờ xác nhận", "#ef4444",
                "Đang vận chuyển", "#fbbf24",
                "Chờ giao hàng", "#10b981",
                "Đã hủy", "#f97316"
        );

        return data.stream()
                .map(row -> new StatusData(
                        row.get("statusName").toString(),
                        ((Number) row.get("total")).intValue(),
                        colorMap.getOrDefault(row.get("statusName").toString(), "#6b7280")
                ))
                .collect(Collectors.toList());
    }

    // 5. Phân phối kênh
    public List<ChannelData> getChannelDistribution(String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(period, now);

        List<Map<String, Object>> data = thongKeRepository.getChannelDistribution(
                startDate,
                now.plusDays(1)
        );

        Map<String, String> colorMap = Map.of(
                "Online", "#ec4899",
                "Tại quầy", "#3b82f6"
        );

        return data.stream()
                .map(row -> new ChannelData(
                        row.get("channelName").toString(),
                        ((Number) row.get("total")).intValue(),
                        colorMap.getOrDefault(row.get("channelName").toString(), "#6b7280")
                ))
                .collect(Collectors.toList());
    }

    // 6. Thống kê theo brand
    public List<BrandData> getBrandStatistics(String period) {
        LocalDate now = LocalDate.now();
        LocalDate startDate = getStartDate(period, now);

        List<Map<String, Object>> data = thongKeRepository.getBrandStatistics(
                startDate,
                now.plusDays(1)
        );

        String[] colors = {"#ec4899", "#3b82f6", "#fbbf24", "#10b981", "#f97316"};

        List<BrandData> brandData = new ArrayList<>();
        int index = 0;

        for (Map<String, Object> row : data) {
            brandData.add(new BrandData(
                    row.get("brandName").toString(),
                    ((Number) row.get("totalRevenue")).doubleValue(),
                    colors[index % colors.length]
            ));
            index++;
        }

        return brandData;
    }

    // 7. Bảng thống kê chi tiết
    public List<DetailData> getDetailTable() {
        LocalDate now = LocalDate.now();

        return Arrays.asList(
                createDetailRow("Hôm nay", now, now.plusDays(1)),
                createDetailRow("Tuần này", now.minusDays(now.getDayOfWeek().getValue() - 1), now.plusDays(1)),
                createDetailRow("Tháng này", now.with(TemporalAdjusters.firstDayOfMonth()), now.plusDays(1)),
                createDetailRow("Năm này", now.with(TemporalAdjusters.firstDayOfYear()), now.plusDays(1))
        );
    }

    private DetailData createDetailRow(String period, LocalDate start, LocalDate end) {
        Map<String, Object> stats = thongKeRepository.getSummaryStatistics(start, end);

        double revenue = ((Number) stats.getOrDefault("totalRevenue", 0)).doubleValue();
        int orders = ((Number) stats.getOrDefault("totalOrders", 0)).intValue();
        double avgValue = orders > 0 ? revenue / orders : 0;

        return new DetailData(
                period,
                currencyFormat.format(revenue) + " đ",
                orders,
                currencyFormat.format(avgValue) + " đ",
                "+0%",
                "Xuất sắc"
        );
    }

    // Helper method
    private LocalDate getStartDate(String period, LocalDate now) {
        return switch (period) {
            case "day" -> now;
            case "week" -> now.minusDays(now.getDayOfWeek().getValue() - 1);
            case "year" -> now.with(TemporalAdjusters.firstDayOfYear());
            default -> now.with(TemporalAdjusters.firstDayOfMonth());
        };
    }

    public byte[] generateThongKePDF(String period) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, out);
            document.open();

            // 🧾 Tiêu đề
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("BÁO CÁO THỐNG KÊ DOANH THU (" + period.toUpperCase() + ")", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15);
            document.add(title);

            // 📅 Thời gian
            Paragraph time = new Paragraph("Ngày tạo: " + java.time.LocalDate.now());
            time.setAlignment(Element.ALIGN_RIGHT);
            time.setSpacingAfter(10);
            document.add(time);

            // 📈 Lấy dữ liệu thống kê
            ThongKeRequest data = getAllStatistics(period);
            ThongKeRequest.SummaryData summary = data.getSummary();

            // 🧮 Tổng quan
            document.add(new Paragraph("TỔNG QUAN:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            PdfPTable summaryTable = new PdfPTable(4);
            summaryTable.setWidthPercentage(100);
            summaryTable.setSpacingBefore(10);

            summaryTable.addCell("Kỳ");
            summaryTable.addCell("Doanh thu");
            summaryTable.addCell("Số đơn");
            summaryTable.addCell("Số sản phẩm");

            summaryTable.addCell("Hôm nay");
            summaryTable.addCell(currencyFormat.format(summary.getToday().getRevenue()) + " đ");
            summaryTable.addCell(String.valueOf(summary.getToday().getOrders()));
            summaryTable.addCell(String.valueOf(summary.getToday().getProducts()));

            summaryTable.addCell("Tuần này");
            summaryTable.addCell(currencyFormat.format(summary.getWeek().getRevenue()) + " đ");
            summaryTable.addCell(String.valueOf(summary.getWeek().getOrders()));
            summaryTable.addCell(String.valueOf(summary.getWeek().getProducts()));

            summaryTable.addCell("Tháng này");
            summaryTable.addCell(currencyFormat.format(summary.getMonth().getRevenue()) + " đ");
            summaryTable.addCell(String.valueOf(summary.getMonth().getOrders()));
            summaryTable.addCell(String.valueOf(summary.getMonth().getProducts()));

            summaryTable.addCell("Năm này");
            summaryTable.addCell(currencyFormat.format(summary.getYear().getRevenue()) + " đ");
            summaryTable.addCell(String.valueOf(summary.getYear().getOrders()));
            summaryTable.addCell(String.valueOf(summary.getYear().getProducts()));

            document.add(summaryTable);
            document.add(Chunk.NEWLINE);

            // 🏆 Top sản phẩm
            document.add(new Paragraph("TOP SẢN PHẨM BÁN CHẠY:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            PdfPTable productTable = new PdfPTable(4);
            productTable.setWidthPercentage(100);
            productTable.setSpacingBefore(10);
            productTable.addCell("STT");
            productTable.addCell("Tên sản phẩm");
            productTable.addCell("Số lượng bán");
            productTable.addCell("Doanh thu");

            int index = 1;
            for (ThongKeRequest.ProductData p : data.getTopProducts()) {
                productTable.addCell(String.valueOf(index++));
                productTable.addCell(p.getName());
                productTable.addCell(String.valueOf(p.getSold()));
                productTable.addCell(currencyFormat.format(p.getRevenue()) + " đ");
            }
            document.add(productTable);

            // 🧭 Phân bổ trạng thái đơn hàng
            document.add(Chunk.NEWLINE);
            document.add(new Paragraph("PHÂN BỔ TRẠNG THÁI ĐƠN HÀNG:", new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD)));
            PdfPTable statusTable = new PdfPTable(2);
            statusTable.setWidthPercentage(60);
            statusTable.setSpacingBefore(10);
            statusTable.addCell("Trạng thái");
            statusTable.addCell("Số lượng");

            for (ThongKeRequest.StatusData s : data.getOrderStatus()) {
                statusTable.addCell(s.getName());
                statusTable.addCell(String.valueOf(s.getValue()));
            }
            document.add(statusTable);

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo báo cáo PDF: " + e.getMessage(), e);
        }
    }


    public List<ThongKeRequest.TopProductResponse> getTopSellingProducts() {
        List<Object[]> result = thongKeRepository.findTopSellingProducts();
        return result.stream()
                .map(obj -> new ThongKeRequest.TopProductResponse(
                        ((Number) obj[0]).longValue(),        // id
                        (String) obj[1],                      // tenSanPham
                        (String) obj[2],                      // anh
                        obj[3] != null ? ((Number) obj[3]).doubleValue() : 0.0,  // giaBan
                        obj[4] != null ? ((Number) obj[4]).intValue() : 0,        // tongSoLuongBan
                        obj[5] != null ? ((Number) obj[5]).intValue() : 0    // tongDoanhThu
                ))
                .toList();
    }

    public List<ThongKeRequest.LowStockProductResponse> getLowStockProducts() {
        List<Object[]> result = thongKeRepository.findLowStockProducts(); // ✅ Bỏ PageRequest
        return result.stream()
                .map(obj -> new ThongKeRequest.LowStockProductResponse(
                        ((Number) obj[0]).longValue(),
                        (String) obj[1],
                        ((Number) obj[2]).intValue()
                ))
                .toList();
    }

}
