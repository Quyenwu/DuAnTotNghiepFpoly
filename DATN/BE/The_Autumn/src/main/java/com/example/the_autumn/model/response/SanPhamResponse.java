package com.example.the_autumn.model.response;

import com.example.the_autumn.entity.Anh;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.HoaDonChiTiet;
import com.example.the_autumn.entity.SanPham;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Calendar;

@Getter
@Setter
public class SanPhamResponse {

    private Integer id;
    private String maSanPham;
    private String tenSanPham;
    private Date ngayTao;
    private Date ngaySua;
    private Integer nguoiTao;
    private Integer nguoiSua;
    private Boolean trangThai;
    private String maNhaSanXuat;
    private String tenNhaSanXuat;
    private String maXuatXu;
    private String tenXuatXu;
    private String maChatLieu;
    private String tenChatLieu;
    private String maKieuDang;
    private String tenKieuDang;
    private String trongLuong;
    private String maCoAo;
    private String tenCoAo;
    private String maTayAo;
    private String tenTayAo;
    private List<ChiTietSanPhamResponse> chiTietSanPhams;
    private Integer tongSoLuong;
    private BigDecimal giaThapNhat;
    private BigDecimal giaCaoNhat;
    private Integer tongSoLuongDaMua;
    private List<String> hinhAnhSanPham;

    // Constructor cho tất cả thời gian
    public SanPhamResponse(SanPham sp) {
        this(sp, "all"); // Mặc định lấy tất cả
    }

    // Constructor với timeRange
    public SanPhamResponse(SanPham sp, String timeRange) {
        this.id = sp.getId();
        this.maSanPham = sp.getMaSanPham();
        this.tenSanPham = sp.getTenSanPham();
        this.trongLuong = sp.getTrongLuong();
        this.ngayTao = sp.getNgayTao();
        this.ngaySua = sp.getNgaySua();
        this.nguoiTao = sp.getNguoiTao();
        this.nguoiSua = sp.getNguoiSua();
        this.trangThai = sp.getTrangThai();
        this.maNhaSanXuat = sp.getNhaSanXuat().getMaNhaSanXuat();
        this.tenNhaSanXuat = sp.getNhaSanXuat().getTenNhaSanXuat();
        this.maXuatXu = sp.getXuatXu().getMaXuatXu();
        this.tenXuatXu = sp.getXuatXu().getTenXuatXu();
        this.maChatLieu = sp.getChatLieu().getMaChatLieu();
        this.tenChatLieu = sp.getChatLieu().getTenChatLieu();
        this.maKieuDang = sp.getKieuDang().getMaKieuDang();
        this.tenKieuDang = sp.getKieuDang().getTenKieuDang();
        this.maCoAo = sp.getCoAo().getMaCoAo();
        this.tenCoAo = sp.getCoAo().getTenCoAo();
        this.maTayAo = sp.getTayAo().getMaTayAo();
        this.tenTayAo = sp.getTayAo().getTenTayAo();

        if (sp.getChiTietSanPham() != null && !sp.getChiTietSanPham().isEmpty()) {
            // Lấy danh sách hình ảnh
            this.hinhAnhSanPham = sp.getChiTietSanPham().stream()
                    .flatMap(ctsp -> ctsp.getAnhs().stream())
                    .map(Anh::getDuongDanAnh)
                    .filter(url -> url != null && !url.isEmpty())
                    .distinct()
                    .toList();

            // Lấy chi tiết sản phẩm
            this.chiTietSanPhams = sp.getChiTietSanPham().stream()
                    .map(ChiTietSanPhamResponse::new)
                    .toList();

            // Tính tổng số lượng tồn
            this.tongSoLuong = sp.getChiTietSanPham().stream()
                    .mapToInt(chiTiet -> chiTiet.getSoLuongTon() != null ? chiTiet.getSoLuongTon() : 0)
                    .sum();

            // Tính giá thấp nhất và cao nhất
            List<BigDecimal> giaList = sp.getChiTietSanPham().stream()
                    .filter(ct -> ct.getGiaBan() != null)
                    .map(ChiTietSanPham::getGiaBan)
                    .toList();

            if (!giaList.isEmpty()) {
                this.giaThapNhat = giaList.stream().min(BigDecimal::compareTo).get();
                this.giaCaoNhat = giaList.stream().max(BigDecimal::compareTo).get();
            } else {
                this.giaThapNhat = BigDecimal.ZERO;
                this.giaCaoNhat = BigDecimal.ZERO;
            }

            // Tính tổng số lượng đã mua theo timeRange
            this.tongSoLuongDaMua = calculateTongSoLuongDaMua(sp, timeRange);

        } else {
            this.chiTietSanPhams = List.of();
            this.tongSoLuong = 0;
            this.giaThapNhat = BigDecimal.ZERO;
            this.giaCaoNhat = BigDecimal.ZERO;
            this.hinhAnhSanPham = List.of();
            this.tongSoLuongDaMua = 0;
        }
    }

    // Hàm tính tổng số lượng đã mua theo timeRange
    private Integer calculateTongSoLuongDaMua(SanPham sp, String timeRange) {
        if (sp.getChiTietSanPham() == null || sp.getChiTietSanPham().isEmpty()) {
            return 0;
        }

        return sp.getChiTietSanPham().stream()
                .flatMap(ct -> ct.getHoaDonChiTiets().stream())
                .filter(hdct -> {
                    // Kiểm tra hóa đơn đã thanh toán
                    if (hdct.getHoaDon() == null ||
                            hdct.getHoaDon().getTrangThai() == null ||
                            hdct.getHoaDon().getTrangThai() != 3 || // Trạng thái 3 = đã thanh toán
                            hdct.getHoaDon().getNgayThanhToan() == null) {
                        return false;
                    }

                    Date ngayThanhToan = hdct.getHoaDon().getNgayThanhToan();
                    Calendar calNgayThanhToan = Calendar.getInstance();
                    calNgayThanhToan.setTime(ngayThanhToan);

                    Calendar calNow = Calendar.getInstance();

                    switch (timeRange) {
                        case "day": // Hôm nay
                            return calNgayThanhToan.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                                    calNgayThanhToan.get(Calendar.MONTH) == calNow.get(Calendar.MONTH) &&
                                    calNgayThanhToan.get(Calendar.DAY_OF_MONTH) == calNow.get(Calendar.DAY_OF_MONTH);
                        case "week": // Tuần này
                            return calNgayThanhToan.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                                    calNgayThanhToan.get(Calendar.WEEK_OF_YEAR) == calNow.get(Calendar.WEEK_OF_YEAR);
                        case "month": // Tháng này
                            return calNgayThanhToan.get(Calendar.YEAR) == calNow.get(Calendar.YEAR) &&
                                    calNgayThanhToan.get(Calendar.MONTH) == calNow.get(Calendar.MONTH);
                        case "year": // Năm nay
                            return calNgayThanhToan.get(Calendar.YEAR) == calNow.get(Calendar.YEAR);
                        default: // "all" - Tất cả thời gian
                            return true;
                    }
                })
                .mapToInt(hdct -> hdct.getSoLuong() != null ? hdct.getSoLuong() : 0)
                .sum();
    }
}