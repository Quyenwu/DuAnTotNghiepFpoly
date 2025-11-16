package com.example.the_autumn.repository;

import com.example.the_autumn.entity.HoaDon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Repository
public interface ThongKeRepository extends JpaRepository<HoaDon, Integer> {

    // ✅ 1. Thống kê tổng quan
    @Query(value = """
        SELECT 
            COUNT(DISTINCT hd.id) as totalOrders,
            ISNULL(SUM(hd.tong_tien_sau_giam), 0) as totalRevenue,
            ISNULL(SUM(hdct.so_luong), 0) as totalProducts
        FROM hoa_don hd
        LEFT JOIN hoa_don_chi_tiet hdct ON hd.id = hdct.id_hoa_don
        WHERE hd.trang_thai NOT IN (0, 4)
        AND hd.ngay_tao >= :startDate 
        AND hd.ngay_tao < :endDate
        """, nativeQuery = true)
    Map<String, Object> getSummaryStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ✅ 2. Doanh thu theo tuần trong tháng
    @Query(value = """
        SELECT 
            CONCAT(N'Tuần ', 
                DATEPART(WEEK, hd.ngay_tao) - 
                DATEPART(WEEK, DATEADD(DAY, 1-DATEPART(DAY, hd.ngay_tao), hd.ngay_tao)) + 1
            ) as week,
            ISNULL(SUM(hd.tong_tien_sau_giam), 0) as revenue
        FROM hoa_don hd
        WHERE MONTH(hd.ngay_tao) = :month
        AND YEAR(hd.ngay_tao) = :year
        AND hd.trang_thai NOT IN (0, 4)
        GROUP BY DATEPART(WEEK, hd.ngay_tao), 
                 DATEPART(WEEK, DATEADD(DAY, 1-DATEPART(DAY, hd.ngay_tao), hd.ngay_tao))
        ORDER BY DATEPART(WEEK, hd.ngay_tao)
        """, nativeQuery = true)
    List<Map<String, Object>> getWeeklyRevenue(
            @Param("month") int month,
            @Param("year") int year
    );

    // ✅ 3. Doanh thu theo tháng trong năm
    @Query(value = """
        SELECT 
            CONCAT(N'Tháng ', MONTH(hd.ngay_tao)) as month,
            ISNULL(SUM(hd.tong_tien_sau_giam), 0) as revenue
        FROM hoa_don hd
        WHERE YEAR(hd.ngay_tao) = :year
        AND hd.trang_thai NOT IN (0, 4)
        GROUP BY MONTH(hd.ngay_tao)
        ORDER BY MONTH(hd.ngay_tao)
        """, nativeQuery = true)
    List<Map<String, Object>> getMonthlyRevenue(@Param("year") int year);

    // ✅ 4. Top sản phẩm bán chạy
    @Query(value = """
        SELECT TOP (:limit)
            sp.id as productId,
            sp.ten_san_pham as name,
            SUM(hdct.so_luong) as sold,
            SUM(hdct.thanh_tien) as revenue,
            hdct.gia_ban as price
        FROM hoa_don_chi_tiet hdct
        JOIN hoa_don hd ON hdct.id_hoa_don = hd.id
        JOIN chi_tiet_san_pham ctsp ON hdct.id_ctsp = ctsp.id
        JOIN san_pham sp ON ctsp.id_san_pham = sp.id
        WHERE hd.ngay_tao >= :startDate 
        AND hd.ngay_tao < :endDate
        AND hd.trang_thai NOT IN (0, 4)
        GROUP BY sp.id, sp.ten_san_pham, hdct.gia_ban
        ORDER BY sold DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getTopProducts(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("limit") int limit
    );

    // ⚠️ 5. Phân bổ trạng thái đơn hàng
    @Query(value = """
        SELECT 
            CASE 
                WHEN hd.trang_thai = 0 THEN N'Chờ xác nhận'
                WHEN hd.trang_thai = 1 THEN N'Chờ giao hàng'
                WHEN hd.trang_thai = 2 THEN N'Đang vận chuyển'
                WHEN hd.trang_thai = 3 THEN N'Đã thanh toán'
                WHEN hd.trang_thai = 4 THEN N'Đã hủy'
                ELSE N'Khác'
            END as statusName,
            COUNT(*) as total
        FROM hoa_don hd
        WHERE hd.ngay_tao >= :startDate 
        AND hd.ngay_tao < :endDate
        GROUP BY hd.trang_thai
        """, nativeQuery = true)
    List<Map<String, Object>> getOrderStatusDistribution(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ✅ 6. Phân phối theo kênh
    @Query(value = """
        SELECT 
            CASE 
                WHEN hd.loai_hoa_don = 1 THEN N'Tại quầy'
                WHEN hd.loai_hoa_don = 0 THEN N'Online'
                ELSE N'Khác'
            END as channelName,
            COUNT(*) as total,
            SUM(hd.tong_tien_sau_giam) as revenue
        FROM hoa_don hd
        WHERE hd.ngay_tao >= :startDate 
        AND hd.ngay_tao < :endDate
        AND hd.trang_thai NOT IN (0, 4)
        GROUP BY hd.loai_hoa_don
        """, nativeQuery = true)
    List<Map<String, Object>> getChannelDistribution(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ✅ 7. Thống kê theo brand
    @Query(value = """
        SELECT 
            nsx.ten_nha_san_xuat as brandName,
            SUM(hdct.thanh_tien) as totalRevenue,
            SUM(hdct.so_luong) as totalSold
        FROM hoa_don_chi_tiet hdct
        JOIN hoa_don hd ON hdct.id_hoa_don = hd.id
        JOIN chi_tiet_san_pham ctsp ON hdct.id_ctsp = ctsp.id
        JOIN san_pham sp ON ctsp.id_san_pham = sp.id
        JOIN nha_san_xuat nsx ON sp.id_nha_san_xuat = nsx.id
        WHERE hd.ngay_tao >= :startDate 
        AND hd.ngay_tao < :endDate
        AND hd.trang_thai NOT IN (0, 4)
        GROUP BY nsx.id, nsx.ten_nha_san_xuat
        ORDER BY totalRevenue DESC
        """, nativeQuery = true)
    List<Map<String, Object>> getBrandStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // ✅ 8. Doanh thu theo ngày trong tháng
    @Query(value = """
        SELECT 
            DAY(hd.ngay_tao) AS day,
            ISNULL(SUM(hd.tong_tien_sau_giam), 0) AS revenue
        FROM hoa_don hd
        WHERE MONTH(hd.ngay_tao) = :month
        AND YEAR(hd.ngay_tao) = :year
        AND hd.trang_thai NOT IN (0, 4)
        GROUP BY DAY(hd.ngay_tao)
        ORDER BY DAY(hd.ngay_tao)
        """, nativeQuery = true)
    List<Map<String, Object>> getDailyRevenue(
            @Param("month") int month,
            @Param("year") int year
    );

    // ✅ 9. Top 10 sản phẩm bán chạy nhất
    @Query(value = """
        SELECT TOP 10
            sp.id,
            sp.ten_san_pham,
            (SELECT TOP 1 a.duong_dan_anh 
             FROM anh a 
             INNER JOIN chi_tiet_san_pham ctsp ON a.id_ctsp = ctsp.id 
             WHERE ctsp.id_san_pham = sp.id 
             ORDER BY a.id ASC) as anh,
            AVG(hdct.gia_ban) as gia_ban,
            SUM(hdct.so_luong) as tong_so_luong_ban,
            SUM(hdct.thanh_tien) as tong_doanh_thu
        FROM san_pham sp
        INNER JOIN chi_tiet_san_pham ctsp ON sp.id = ctsp.id_san_pham
        INNER JOIN hoa_don_chi_tiet hdct ON ctsp.id = hdct.id_ctsp
        INNER JOIN hoa_don hd ON hdct.id_hoa_don = hd.id
        WHERE hd.trang_thai NOT IN (0, 4)
        GROUP BY sp.id, sp.ten_san_pham
        HAVING SUM(hdct.so_luong) > 0
        ORDER BY tong_so_luong_ban DESC
        """, nativeQuery = true)
    List<Object[]> findTopSellingProducts();

    // ✅ 10. Sản phẩm sắp hết hàng
    @Query(value = """
        SELECT TOP 5
            sp.id, 
            sp.ten_san_pham, 
            SUM(ctsp.so_luong_ton) as tongTon
        FROM chi_tiet_san_pham ctsp
        JOIN san_pham sp ON ctsp.id_san_pham = sp.id
        WHERE ctsp.trang_thai = 1
        GROUP BY sp.id, sp.ten_san_pham
        HAVING SUM(ctsp.so_luong_ton) <= 50 AND SUM(ctsp.so_luong_ton) > 0
        ORDER BY tongTon ASC
        """, nativeQuery = true)
    List<Object[]> findLowStockProducts();

}