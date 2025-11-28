package com.example.the_autumn.repository;

import com.example.the_autumn.dto.SanPhamTrangChuProjection;
import com.example.the_autumn.entity.SanPham;

import com.example.the_autumn.model.response.SanPhamResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SanPhamRepository extends JpaRepository<SanPham, Integer> {

    List<SanPham> findByTenSanPhamAndTrangThai(String tenSanPham, Boolean trangThai);
    List<SanPham> findByTrangThai(Boolean trangThai);
    @Query("select sp from SanPham sp where (:q is null or lower(sp.tenSanPham) like lower(concat('%', :q, '%')) or lower(sp.maSanPham) like lower(concat('%', :q, '%')))")
    Page<SanPham> search(String q, Pageable pageable);
  
    Optional<SanPham> findByMaSanPham(String maSanPham);

    boolean existsByTenSanPham(String tenSanPham);

    boolean existsByMaSanPham(String maSanPham);

    @EntityGraph(attributePaths = {
            "nhaSanXuat", "xuatXu", "chatLieu", "kieuDang", "coAo", "tayAo",
            "chiTietSanPham", "chiTietSanPham.mauSac", "chiTietSanPham.kichThuoc",
            "chiTietSanPham.trongLuong", "chiTietSanPham.anhs"
    })
    @Query("SELECT sp FROM SanPham sp WHERE sp.id = :id")
    Optional<SanPham> findByIdWithDetails(@Param("id") Integer id);

    @Query(value = """
    WITH ProductDetails AS (
        SELECT 
            sp.id AS idSanPham,
            sp.ten_san_pham AS tenSanPham,
            ctsp.gia_ban AS giaBan,
            a.duong_dan_anh AS anhDaiDien,
            ROW_NUMBER() OVER(PARTITION BY sp.id ORDER BY ctsp.id, a.id) as rn
        FROM 
            san_pham sp
        JOIN 
            chi_tiet_san_pham ctsp ON sp.id = ctsp.id_san_pham
        LEFT JOIN 
            anh a ON ctsp.id = a.id_ctsp
        WHERE 
            sp.trang_thai = 1 AND ctsp.trang_thai = 1 AND ctsp.so_luong_ton > 0
    )
    SELECT 
        pd.idSanPham,
        pd.tenSanPham,
        MIN(pd.giaBan) AS giaMin, 
        MAX(pd.giaBan) AS giaMax,
        MAX(CASE WHEN pd.rn = 1 THEN pd.anhDaiDien ELSE NULL END) AS anhDaiDien
    FROM 
        ProductDetails pd
    GROUP BY 
        pd.idSanPham, pd.tenSanPham
""", nativeQuery = true)
    List<SanPhamTrangChuProjection> findSanPhamTrangChu();

    @Query(value = """
        WITH RankedProducts AS (
            SELECT 
                sp.id AS idSanPham,
                sp.ten_san_pham AS tenSanPham,
                a.duong_dan_anh AS anhDaiDien,
                ctsp.gia_ban AS giaGoc,
                dggct.gia_sau_giam AS giaSauGiam,
                dgg.id AS idDotGiamGia,
                dgg.ten_dot AS tenDotGiamGia,
                dggct.do_uu_tien,
                ROW_NUMBER() OVER (PARTITION BY sp.id ORDER BY dggct.do_uu_tien DESC, a.id) AS rn
            FROM san_pham sp
            INNER JOIN chi_tiet_san_pham ctsp ON sp.id = ctsp.id_san_pham
            INNER JOIN dot_giam_gia_chi_tiet dggct ON ctsp.id = dggct.id_ctsp
            INNER JOIN dot_giam_gia dgg ON dggct.id_dot_giam_gia = dgg.id
            LEFT JOIN anh a ON ctsp.id = a.id_ctsp
            WHERE sp.trang_thai = 1
              AND ctsp.trang_thai = 1
              AND dgg.trang_thai = 1
              AND GETDATE() BETWEEN dgg.ngay_bat_dau AND dgg.ngay_ket_thuc
              AND dggct.gia_sau_giam IS NOT NULL
              AND dggct.gia_sau_giam < ctsp.gia_ban
        )
        SELECT 
            idSanPham,
            tenSanPham,
            anhDaiDien,
            giaGoc,
            giaSauGiam,
            idDotGiamGia,
            tenDotGiamGia
        FROM RankedProducts
        WHERE rn = 1
        ORDER BY do_uu_tien DESC, idSanPham
    """, nativeQuery = true)
    List<Object[]> findSanPhamDangGiamGiaNative();

    @Query(value = """
        WITH RankedProducts AS (
            SELECT 
                sp.id AS idSanPham,
                sp.ten_san_pham AS tenSanPham,
                a.duong_dan_anh AS anhDaiDien,
                ctsp.gia_ban AS giaGoc,
                dggct.gia_sau_giam AS giaSauGiam,
                dgg.id AS idDotGiamGia,
                dgg.ten_dot AS tenDotGiamGia,
                CAST(((ctsp.gia_ban - dggct.gia_sau_giam) * 100.0 / ctsp.gia_ban) AS DECIMAL(5,2)) AS phanTramGiam,
                ROW_NUMBER() OVER (PARTITION BY sp.id ORDER BY dggct.do_uu_tien DESC, a.id) AS rn
            FROM san_pham sp
            INNER JOIN chi_tiet_san_pham ctsp ON sp.id = ctsp.id_san_pham
            INNER JOIN dot_giam_gia_chi_tiet dggct ON ctsp.id = dggct.id_ctsp
            INNER JOIN dot_giam_gia dgg ON dggct.id_dot_giam_gia = dgg.id
            LEFT JOIN anh a ON ctsp.id = a.id_ctsp
            WHERE sp.trang_thai = 1
              AND ctsp.trang_thai = 1
              AND dgg.trang_thai = 1
              AND GETDATE() BETWEEN dgg.ngay_bat_dau AND dgg.ngay_ket_thuc
              AND dggct.gia_sau_giam IS NOT NULL
              AND dggct.gia_sau_giam < ctsp.gia_ban
              AND ((ctsp.gia_ban - dggct.gia_sau_giam) * 100.0 / ctsp.gia_ban) >= :minPercent
        )
        SELECT 
            idSanPham,
            tenSanPham,
            anhDaiDien,
            giaGoc,
            giaSauGiam,
            idDotGiamGia,
            tenDotGiamGia
        FROM RankedProducts
        WHERE rn = 1
        ORDER BY phanTramGiam DESC, idSanPham
    """, nativeQuery = true)
    List<Object[]> findSanPhamGiamGiaTheoPercentNative(double minPercent);

    @Query("""
    SELECT sp FROM SanPham sp
    WHERE sp.trangThai = true
""")
    List<SanPham> findAllSanPhamBanChay();

}

