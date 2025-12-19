package com.example.the_autumn.service;

import com.example.the_autumn.dto.KhachHangDTO;
import com.example.the_autumn.dto.NhanVienDTO;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.HinhThucThanhToan;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.HoaDonChiTiet;
import com.example.the_autumn.entity.LichSuHoaDon;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhuongThucThanhToan;
import com.example.the_autumn.entity.*;
import com.example.the_autumn.model.request.*;
import com.example.the_autumn.model.response.*;
import com.example.the_autumn.repository.HoaDonChiTietRepository;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhuongThucThanhToanRepository;
import com.example.the_autumn.repository.*;
import com.example.the_autumn.util.MapperUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service

public class HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private DotGiamGiaChiTietRepository dotGiamGiaChiTietRepository;

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepository;

    @Autowired
    private HinhThucThanhToanRepository hinhThucThanhToanRepository;

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    private QuanHuyenRepository quanHuyenRepository;

    @Autowired
    private TinhThanhRepository tinhThanhRepository;


    @Autowired
    private EmailService emailService;

    @Autowired
    private VnPayService vnPayService;

//    public PageHoaDonRequest<HoaDonRespone> getAll(Pageable pageable) {
//        Pageable sortedPageable = PageRequest.of(
//                pageable.getPageNumber(),
//                pageable.getPageSize(),
//                Sort.by(Sort.Direction.DESC, "ngayTao")
//        );
//
//        Specification<HoaDon> spec = (root, query, cb) ->
//                cb.notEqual(root.get("trangThai"), 5);
//
//        Page<HoaDon> page = hoaDonRepository.findAll(spec, sortedPageable);
//
//        List<HoaDonRespone> dtoList = page.getContent().stream()
//                .map(this::convertToDTO)
//                .collect(Collectors.toList());
//
//        return new PageHoaDonRequest<>(
//                dtoList,
//                page.getTotalPages(),
//                page.getTotalElements(),
//                page.getNumber(),
//                page.getSize(),
//                page.isFirst(),
//                page.isLast()
//        );
//    }

    private HoaDonRespone convertToDTO(HoaDon hd) {
        HoaDonRespone dto = new HoaDonRespone();
        dto.setId(hd.getId());
        dto.setMaHoaDon(hd.getMaHoaDon());
        dto.setLoaiHoaDon(hd.getLoaiHoaDon());
        dto.setPhiVanChuyen(hd.getPhiVanChuyen());
        dto.setTongTien(hd.getTongTien());
        dto.setTongTienSauGiam(hd.getTongTienSauGiam());
        dto.setGhiChu(hd.getGhiChu());
        dto.setDiaChiKhachHang(hd.getDiaChiKhachHang());
        dto.setNgayThanhToan(hd.getNgayThanhToan());
        dto.setNgayTao(hd.getNgayTao());
        dto.setNgaySua(hd.getNgaySua());
        dto.setTrangThai(hd.getTrangThai());

        if (hd.getKhachHang() != null) {
            KhachHangDTO khDTO = new KhachHangDTO();
            khDTO.setId(hd.getKhachHang().getId());
            khDTO.setHoTen(hd.getKhachHang().getHoTen());
            khDTO.setSdt(hd.getKhachHang().getSdt());
            khDTO.setEmail(hd.getKhachHang().getEmail());
            dto.setKhachHang(khDTO);
        }

        if (hd.getNhanVien() != null) {
            NhanVienDTO nvDTO = new NhanVienDTO();
            nvDTO.setId(hd.getNhanVien().getId());
            nvDTO.setHoTen(hd.getNhanVien().getHoTen());
            nvDTO.setSdt(hd.getNhanVien().getSdt());
            nvDTO.setEmail(hd.getNhanVien().getEmail());
            dto.setNhanVien(nvDTO);

            if (hd.getHinhThucThanhToans() != null && !hd.getHinhThucThanhToans().isEmpty()) {
                HinhThucThanhToan hinhThuc = hd.getHinhThucThanhToans().get(0);
                if (hinhThuc.getPhuongThucThanhToan() != null) {
                    dto.setHinhThucThanhToan(hinhThuc.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
                } else {
                    dto.setHinhThucThanhToan("Không xác định");
                }
            } else {
                dto.setHinhThucThanhToan("Chưa thanh toán");
            }
        }
        return dto;
    }


    public Optional<HoaDon> getById(Integer id) {
        return hoaDonRepository.findById(id);
    }




    public byte[] printInvoices(List<Integer> invoiceIds) throws Exception {
        List<HoaDon> hoaDons = hoaDonRepository.findAllById(invoiceIds);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // Font tiếng Việt
        BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headerFont = new Font(bf, 12, Font.BOLD);
        Font normalFont = new Font(bf, 10, Font.NORMAL);

        // Tiêu đề
        Paragraph title = new Paragraph("DANH SÁCH HÓA ĐƠN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(20);
        document.add(title);

        // Tạo bảng 9 cột
        PdfPTable table = new PdfPTable(9);
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        float[] columnWidths = {0.6f, 1.2f, 1.8f, 1.5f, 1.3f, 1f, 1.3f, 1.2f, 1.5f};
        table.setWidths(columnWidths);

        String[] headers = {"STT", "Mã HĐ", "Khách hàng", "Nhân viên", "Trạng thái",
                "Dịch vụ", "Hình thức TT", "Ngày tạo", "Tổng tiền"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Dữ liệu
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        int stt = 1;

        for (HoaDon hd : hoaDons) {
            // STT
            PdfPCell cellSTT = new PdfPCell(new Phrase(String.valueOf(stt++), normalFont));
            cellSTT.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellSTT.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellSTT.setPadding(8);
            table.addCell(cellSTT);

            // Mã hóa đơn
            PdfPCell cellMaHD = new PdfPCell(new Phrase(hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "", normalFont));
            cellMaHD.setPadding(8);
            cellMaHD.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellMaHD);

            // Khách hàng
            String tenKH = hd.getKhachHang() != null ? hd.getKhachHang().getHoTen() : "Khách lẻ";
            PdfPCell cellKH = new PdfPCell(new Phrase(tenKH, normalFont));
            cellKH.setPadding(8);
            cellKH.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellKH);

            // Nhân viên
            String tenNV = hd.getNhanVien() != null ? hd.getNhanVien().getHoTen() : "";
            PdfPCell cellNV = new PdfPCell(new Phrase(tenNV, normalFont));
            cellNV.setPadding(8);
            cellNV.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellNV);

            // Trạng thái
            String trangThaiText = TrangThaiHoaDonRespone.getText(hd.getTrangThai());
            PdfPCell cellTT = new PdfPCell(new Phrase(trangThaiText, normalFont));
            cellTT.setPadding(8);
            cellTT.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellTT);

            // Dịch vụ
            String dichVu = hd.getLoaiHoaDon() != null && hd.getLoaiHoaDon() ? "Tại quầy" : "Online";
            PdfPCell cellDV = new PdfPCell(new Phrase(dichVu, normalFont));
            cellDV.setPadding(8);
            cellDV.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellDV.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellDV);

            // ⭐ Hình thức thanh toán (QUAN TRỌNG - đã sửa)
            String hinhThuc = "";
            if (hd.getHinhThucThanhToans() != null && !hd.getHinhThucThanhToans().isEmpty()) {
                HinhThucThanhToan ht = hd.getHinhThucThanhToans().get(0);
                if (ht.getPhuongThucThanhToan() != null) {
                    hinhThuc = ht.getPhuongThucThanhToan().getTenPhuongThucThanhToan();
                }
            }
            PdfPCell cellHTTT = new PdfPCell(new Phrase(hinhThuc, normalFont));
            cellHTTT.setPadding(8);
            cellHTTT.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellHTTT);

            // Ngày tạo
            String ngayTao = hd.getNgayTao() != null ? dateFormat.format(hd.getNgayTao()) : "";
            PdfPCell cellNgay = new PdfPCell(new Phrase(ngayTao, normalFont));
            cellNgay.setPadding(8);
            cellNgay.setHorizontalAlignment(Element.ALIGN_CENTER);
            cellNgay.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cellNgay);

            // Tổng tiền
            String tongTienStr = hd.getTongTien() != null ? currencyFormat.format(hd.getTongTien()) : "0 ₫";
            PdfPCell cellTien = new PdfPCell(new Phrase(tongTienStr, normalFont));
            cellTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTien.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cellTien.setPadding(8);
            table.addCell(cellTien);
        }

        document.add(table);

        // Footer
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Paragraph footer = new Paragraph(
                "Ngày in: " + sdf.format(new Date()),
                new Font(bf, 9, Font.ITALIC)
        );
        footer.setAlignment(Element.ALIGN_RIGHT);
        footer.setSpacingBefore(20);
        document.add(footer);

        document.close();
        return baos.toByteArray();
    }


    public PageHoaDonRequest<HoaDonRespone> timkiemVaLoc(
            String searchText,
            List<Boolean> loaiHoaDon,
            Integer trangThai,
            LocalDate ngayTao,
            String hinhThucThanhToan,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "ngayTao"));

        Specification<HoaDon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ⭐ ĐIỀU KIỆN MẶC ĐỊNH: LOẠI BỎ TRẠNG THÁI 5
            // Chỉ hiển thị trạng thái 5 nếu người dùng CHỦ ĐỘNG lọc với trangThai = 5
            if (trangThai == null || trangThai != 5) {
                predicates.add(cb.notEqual(root.get("trangThai"), 5));
            }

            // Tìm kiếm theo searchText (tìm trong mã HD, tên KH, tên NV)
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText.toLowerCase().trim() + "%";

                Predicate maPredicate = cb.like(cb.lower(root.get("maHoaDon")), searchPattern);

                // Tìm theo tên khách hàng (nếu có)
                Join<HoaDon, KhachHang> khachHangJoin = root.join("khachHang", JoinType.LEFT);
                Predicate tenKhPredicate = cb.like(cb.lower(khachHangJoin.get("hoTen")), searchPattern);

                // Tìm theo tên nhân viên (nếu có)
                Join<HoaDon, NhanVien> nhanVienJoin = root.join("nhanVien", JoinType.LEFT);
                Predicate tenNvPredicate = cb.like(cb.lower(nhanVienJoin.get("hoTen")), searchPattern);

                predicates.add(cb.or(maPredicate, tenKhPredicate, tenNvPredicate));
            }

            // Lọc theo loại hóa đơn
            if (loaiHoaDon != null && !loaiHoaDon.isEmpty()) {
                predicates.add(root.get("loaiHoaDon").in(loaiHoaDon));
            }

            // ⭐ Lọc theo trạng thái (nếu có)
            // Nếu người dùng chủ động lọc trạng thái, thì hiển thị theo ý họ
            if (trangThai != null) {
                // Nếu là trạng thái 5, thay thế điều kiện loại bỏ ở trên
                if (trangThai == 5) {
                    // Tìm và xóa điều kiện loại bỏ trạng thái 5 đã thêm ở trên
                    predicates.removeIf(p -> {
                        try {
                            return p.toString().contains("trangThai != 5");
                        } catch (Exception e) {
                            return false;
                        }
                    });
                }
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            // Lọc theo ngày tạo
            if (ngayTao != null) {
                LocalDateTime startOfDay = ngayTao.atStartOfDay();
                LocalDateTime endOfDay = ngayTao.atTime(23, 59, 59);
                predicates.add(cb.between(root.get("ngayTao"), startOfDay, endOfDay));
            }

            // Lọc theo hình thức thanh toán
            if (hinhThucThanhToan != null && !hinhThucThanhToan.trim().isEmpty()) {
                Join<HoaDon, HinhThucThanhToan> hinhThucJoin = root.join("hinhThucThanhToans", JoinType.LEFT);
                Join<HinhThucThanhToan, PhuongThucThanhToan> phuongThucJoin = hinhThucJoin.join("phuongThucThanhToan", JoinType.LEFT);

                predicates.add(cb.like(
                        cb.lower(phuongThucJoin.get("tenPhuongThucThanhToan")),
                        "%" + hinhThucThanhToan.toLowerCase().trim() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<HoaDon> pageResult = hoaDonRepository.findAll(spec, pageable);

        List<HoaDonRespone> dtoList = pageResult.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageHoaDonRequest<>(
                dtoList,
                pageResult.getTotalPages(),
                pageResult.getTotalElements(),
                pageResult.getNumber(),
                pageResult.getSize(),
                pageResult.isFirst(),
                pageResult.isLast()
        );
    }

    public HoaDonDetailResponse getHoaDonDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + id));

        HoaDonDetailResponse dto = new HoaDonDetailResponse();

        // ==================== THÔNG TIN CƠ BẢN ====================
        dto.setId(hoaDon.getId());
        dto.setMaHoaDon(hoaDon.getMaHoaDon());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgayThanhToan(hoaDon.getNgayThanhToan());
        dto.setTenKhachHang(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getHoTen() : null);
        dto.setSdtKhachHang(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getSdt() : null);
        dto.setEmailKhachHang(hoaDon.getKhachHang() != null ? hoaDon.getKhachHang().getEmail() : null);
        dto.setDiaChiKhachHang(hoaDon.getDiaChiKhachHang());

        // ==================== THÔNG TIN PHỤ PHÍ ====================
        dto.setPhiPhu(hoaDon.getPhiPhu() != null ? hoaDon.getPhiPhu() : BigDecimal.ZERO);
        dto.setPhiPhuMoi(hoaDon.getPhiPhuMoi() != null ? hoaDon.getPhiPhuMoi() : BigDecimal.ZERO);

        // ==================== THÔNG TIN SỐ TIỀN THANH TOÁN ====================
        // Ưu tiên 1: Lấy từ trường soTienThanhToan của entity HoaDon
        BigDecimal soTienThanhToan = BigDecimal.ZERO;
        if (hoaDon.getSoTienThanhToan() != null) {
            soTienThanhToan = hoaDon.getSoTienThanhToan();
        }
        // Ưu tiên 2: Lấy từ lịch sử thanh toán
        else if (hoaDon.getLichSuThanhToans() != null && !hoaDon.getLichSuThanhToans().isEmpty()) {
            soTienThanhToan = hoaDon.getLichSuThanhToans().stream()
                    .filter(ls -> ls.getTrangThai() != null && ls.getTrangThai())
                    .map(ls -> ls.getSoTien() != null ? ls.getSoTien() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
        // Mặc định: Nếu không có thông tin, coi như chưa thanh toán
        dto.setSoTienThanhToan(soTienThanhToan);

        // ==================== TÍNH TOÁN SỐ TIỀN CẦN THANH TOÁN ====================
        BigDecimal tongTienSauGiam = hoaDon.getTongTienSauGiam() != null ?
                hoaDon.getTongTienSauGiam() : BigDecimal.ZERO;

        // Tính số tiền còn phải thanh toán
        BigDecimal soTienCanThanhToan = tongTienSauGiam.subtract(soTienThanhToan);
        if (soTienCanThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            soTienCanThanhToan = BigDecimal.ZERO;
        }

        dto.setSoTienCanThanhToan(soTienCanThanhToan);

        // Xử lý phiPhuDetails từ ghiChuPhiPhu (nếu lưu dạng JSON)
        if (hoaDon.getPhiPhuDetails() != null && !hoaDon.getPhiPhuDetails().trim().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                List<HoaDonDetailResponse.PhiPhuDetailDTO> phiPhuDetails =
                        mapper.readValue(hoaDon.getPhiPhuDetails(),
                                new TypeReference<List<HoaDonDetailResponse.PhiPhuDetailDTO>>() {});
                dto.setPhiPhuDetails(phiPhuDetails);
            } catch (Exception e) {
                System.err.println("Lỗi parse phiPhuDetails: " + e.getMessage());
                dto.setPhiPhuDetails(new ArrayList<>());
            }
        } else {
            dto.setPhiPhuDetails(new ArrayList<>());
        }

        // ==================== THÔNG TIN NHÂN VIÊN ====================
        if (hoaDon.getNhanVien() != null) {
            dto.setIdNhanVien(hoaDon.getNhanVien().getId());
            dto.setMaNhanVien(hoaDon.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(hoaDon.getNhanVien().getHoTen());
            dto.setSdtNhanVien(hoaDon.getNhanVien().getSdt());
        }

        // ==================== THÔNG TIN PHIẾU GIẢM GIÁ ====================
        if (hoaDon.getPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = hoaDon.getPhieuGiamGia();
            dto.setMaGiamGia(phieuGiamGia.getMaGiamGia());
            dto.setTenChuongTrinh(phieuGiamGia.getTenChuongTrinh());
            dto.setGiaTriGiamGia(phieuGiamGia.getGiaTriGiamGia());
            dto.setLoaiGiamGia(phieuGiamGia.getLoaiGiamGia());
            dto.setMucGiaGiamToiDa(phieuGiamGia.getMucGiaGiamToiDa());
            dto.setGiaTriDonHangToiThieu(phieuGiamGia.getGiaTriDonHangToiThieu());
            dto.setSoLuongDung(phieuGiamGia.getSoLuongDung());
            dto.setNgayBatDau(phieuGiamGia.getNgayBatDau());
            dto.setNgayKetThuc(phieuGiamGia.getNgayKetThuc());
            dto.setTrangThaiPhieuGiamGia(phieuGiamGia.getTrangThai());
        }

        // ==================== THÔNG TIN LỊCH SỬ THANH TOÁN ====================
        if (hoaDon.getLichSuThanhToans() != null && !hoaDon.getLichSuThanhToans().isEmpty()) {
            LichSuThanhToan lichSu = hoaDon.getLichSuThanhToans().get(0);
            dto.setMaGiaoDich(lichSu.getMaGiaoDich());
            dto.setSoTien(lichSu.getSoTien());
            dto.setGhiChuThanhToan(lichSu.getGhiChu());
        }

        // ==================== THÔNG TIN HÌNH THỨC THANH TOÁN ====================
        if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
            HinhThucThanhToan hinhThuc = hoaDon.getHinhThucThanhToans().get(0);
            if (hinhThuc.getPhuongThucThanhToan() != null) {
                dto.setIdPhuongThucThanhToan(hinhThuc.getPhuongThucThanhToan().getId());
                dto.setHinhThucThanhToan(hinhThuc.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
            } else {
                dto.setHinhThucThanhToan("Không xác định");
            }
        } else {
            dto.setHinhThucThanhToan("Chưa thanh toán");
        }

        // ==================== THÔNG TIN HÓA ĐƠN ====================
        dto.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        dto.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setTongTienSauGiam(tongTienSauGiam);
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setGhiChu(hoaDon.getGhiChu());

        // ==================== THÔNG TIN KHÁCH HÀNG CHI TIẾT ====================
        if (hoaDon.getKhachHang() != null) {
            KhachHangResponse kh = new KhachHangResponse(hoaDon.getKhachHang());
            dto.setKhachHang(kh);
        } else {
            KhachHangResponse kh = new KhachHangResponse();
            kh.setHoTen("Khách lẻ");
            kh.setSdt("N/A");
            kh.setEmail("N/A");
            dto.setKhachHang(kh);
        }

        // ==================== CHI TIẾT SẢN PHẨM ====================
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon_Id(id);
        List<HoaDonDetailResponse.ChiTietSanPhamResponse> chiTietDTOs = chiTietList.stream()
                .map(ct -> {
                    HoaDonDetailResponse.ChiTietSanPhamResponse ctDTO = new HoaDonDetailResponse.ChiTietSanPhamResponse();
                    ctDTO.setId(ct.getId());
                    ctDTO.setIdChiTietSanPham(ct.getChiTietSanPham().getId());
                    ctDTO.setSoLuong(ct.getSoLuong());
                    ctDTO.setThanhTien(ct.getThanhTien());
                    ctDTO.setGhiChu(ct.getGhiChu());

                    if (ct.getChiTietSanPham() != null) {
                        ChiTietSanPham ctsp = ct.getChiTietSanPham();
                        ctDTO.setMaVach(ctsp.getMaVach());
                        ctDTO.setGiaBan(ctsp.getGiaBan());
                        ctDTO.setGiaSauGiam(ct.getGiaBan());

                        if (ctsp.getSanPham() != null) {
                            ctDTO.setTenSanPham(ctsp.getSanPham().getTenSanPham());
                        }
                        if (ctsp.getMauSac() != null) {
                            ctDTO.setMauSac(ctsp.getMauSac().getTenMauSac());
                        }
                        if (ctsp.getKichThuoc() != null) {
                            ctDTO.setKichThuoc(ctsp.getKichThuoc().getTenKichThuoc());
                        }
                        if (ctsp.getAnhs() != null) {
                            List<String> anhUrls = ctsp.getAnhs()
                                    .stream()
                                    .map(anh -> anh.getDuongDanAnh())
                                    .collect(Collectors.toList());
                            ctDTO.setAnhUrls(anhUrls);
                        }
                    }

                    return ctDTO;
                })
                .collect(Collectors.toList());

        dto.setChiTietSanPhams(chiTietDTOs);

        return dto;
    }


    public boolean canEdit(Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHoaDon));

        Integer status = hoaDon.getTrangThai();
        return status != null && status != 3 && status != 4;
    }


    // Trong HoaDonService.java - sửa phần cập nhật sản phẩm
    @Transactional
    public UpdateHoaDonResponse updateHoaDon(Integer id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID " + id));

        // Biến theo dõi thay đổi
        Integer oldStatus = hoaDon.getTrangThai();
        Integer newStatus = request.getTrangThai();
        boolean hasAddressChange = false;
        boolean hasProductChanges = false;
        boolean hasStatusChange = false;

        // ==================== XỬ LÝ TRẠNG THÁI VÀ TỒN KHO ====================
        if (newStatus != null && !newStatus.equals(oldStatus)) {
            hasStatusChange = true;

            // Trừ tồn kho khi chuyển từ Chờ xác nhận (0) sang Chờ giao hàng (1)
            if (oldStatus == 0 && newStatus == 1) {
                subtractInventory(hoaDon);
            }

            // Trả tồn kho khi hủy đơn hàng (chuyển sang trạng thái 4)
            if (newStatus == 4 ) {
                handleOrderCancellation(hoaDon, false);
            }if (newStatus == 3) {
                handleOrderCompletion(hoaDon);
            }
        }

        // ==================== CẬP NHẬT THÔNG TIN CƠ BẢN ====================
        if (request.getGhiChu() != null) {
            hoaDon.setGhiChu(request.getGhiChu());
        }

        if (request.getTrangThai() != null) {
            hoaDon.setTrangThai(request.getTrangThai());
        }

        if (request.getLoaiHoaDon() != null) {
            hoaDon.setLoaiHoaDon(request.getLoaiHoaDon());
        }

        // Cập nhật thông tin tiền
        if (request.getPhiVanChuyen() != null) {
            hoaDon.setPhiVanChuyen(request.getPhiVanChuyen());
        }

        if (request.getPhiPhu() != null) {
            hoaDon.setPhiPhu(request.getPhiPhu());
        }

        if (request.getPhiPhuMoi() != null) {
            hoaDon.setPhiPhuMoi(request.getPhiPhuMoi());
        }

        if (request.getPhiPhuDetails() != null && !request.getPhiPhuDetails().isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                String phiPhuDetailsJson = mapper.writeValueAsString(request.getPhiPhuDetails());
                hoaDon.setPhiPhuDetails(phiPhuDetailsJson);
            } catch (Exception e) {
                System.err.println("Lỗi chuyển phiPhuDetails thành JSON: " + e.getMessage());
            }
        }

        // Cập nhật thông tin khách hàng
        if (request.getIdKhachHang() != null) {
            KhachHang khachHang = khachHangRepository.findById(request.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + request.getIdKhachHang()));
            hoaDon.setKhachHang(khachHang);
        }

        // ==================== CẬP NHẬT CHI TIẾT SẢN PHẨM ====================
        if (request.getChiTietSanPhams() != null && !request.getChiTietSanPhams().isEmpty()) {
            hasProductChanges = true;

            // Lấy danh sách sản phẩm cũ để so sánh
            List<HoaDonChiTiet> existingDetails = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());
            Map<Integer, Integer> oldProductQuantities = new HashMap<>();

            for (HoaDonChiTiet oldDetail : existingDetails) {
                oldProductQuantities.put(oldDetail.getChiTietSanPham().getId(), oldDetail.getSoLuong());
            }

            // Xóa tất cả chi tiết cũ
            List<Integer> existingDetailIds = existingDetails.stream()
                    .map(HoaDonChiTiet::getId)
                    .collect(Collectors.toList());

            if (!existingDetailIds.isEmpty()) {
                hoaDonChiTietRepository.deleteAllById(existingDetailIds);
            }

            // Thêm chi tiết mới
            List<HoaDonChiTiet> newDetails = new ArrayList<>();
            BigDecimal tongTienSanPham = BigDecimal.ZERO;

            for (UpdateHoaDonRequest.ChiTietSanPhamRequest ctRequest : request.getChiTietSanPhams()) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctRequest.getIdChiTietSanPham())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm ID: " + ctRequest.getIdChiTietSanPham()));

                HoaDonChiTiet hdct = new HoaDonChiTiet();
                hdct.setHoaDon(hoaDon);
                hdct.setChiTietSanPham(ctsp);
                hdct.setSoLuong(ctRequest.getSoLuong());

                // Sử dụng giá từ request hoặc từ sản phẩm (không dùng giaSauGiam)
                BigDecimal giaBan = ctRequest.getGiaBan() != null ? ctRequest.getGiaBan() : ctsp.getGiaBan();

                hdct.setGiaBan(giaBan);

                // Tính thành tiền = giá bán * số lượng
                BigDecimal thanhTien = giaBan.multiply(BigDecimal.valueOf(ctRequest.getSoLuong()));
                hdct.setThanhTien(thanhTien);
                hdct.setTrangThai(true);

                if (ctRequest.getGhiChu() != null) {
                    hdct.setGhiChu(ctRequest.getGhiChu());
                }

                newDetails.add(hdct);

                // Cộng dồn tổng tiền sản phẩm
                tongTienSanPham = tongTienSanPham.add(thanhTien);

                // Nếu đang trong trạng thái đã xác nhận (>= 1), cần điều chỉnh tồn kho
                if (hoaDon.getTrangThai() != null && hoaDon.getTrangThai() >= 1) {
                    adjustInventoryForProductChange(ctsp, ctRequest.getSoLuong(),
                            oldProductQuantities.getOrDefault(ctsp.getId(), 0));
                }
            }

            // Lưu tất cả chi tiết mới
            hoaDonChiTietRepository.saveAll(newDetails);

            // Cập nhật lại tổng tiền sản phẩm vào hóa đơn
            hoaDon.setTongTien(tongTienSanPham);

            // Ghi log
            luuLichSu(hoaDon, "Cập nhật danh sách sản phẩm",
                    String.format("Đã cập nhật %d sản phẩm trong hóa đơn", newDetails.size()), null);
        }

        // ==================== CẬP NHẬT THÔNG TIN KHÁC ====================
        if (request.getIdNhanVien() != null) {
            NhanVien nv = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + request.getIdNhanVien()));
            hoaDon.setNhanVien(nv);
        }

        if (request.getIdPhuongThucThanhToan() != null) {
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán ID: " + request.getIdPhuongThucThanhToan()));

            // Cập nhật hình thức thanh toán
            HinhThucThanhToan hinhThuc = hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()
                    ? hoaDon.getHinhThucThanhToans().get(0)
                    : new HinhThucThanhToan();

            hinhThuc.setHoaDon(hoaDon);
            hinhThuc.setPhuongThucThanhToan(pttt);
            hinhThuc.setTrangThai(true);
            hinhThucThanhToanRepository.save(hinhThuc);
        }

        if (request.getIdPhieuGiamGia() != null) {
            PhieuGiamGia pgg = phieuGiamGiaRepository.findById(request.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá ID: " + request.getIdPhieuGiamGia()));
            hoaDon.setPhieuGiamGia(pgg);
        }

        // ==================== TÍNH TOÁN LẠI TỔNG TIỀN ====================
        // Tính tổng tiền sản phẩm từ chi tiết (nếu có)
        List<HoaDonChiTiet> currentDetails = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());
        BigDecimal tongTienSanPhamMoi = BigDecimal.ZERO;

        if (!currentDetails.isEmpty()) {
            tongTienSanPhamMoi = currentDetails.stream()
                    .map(ct -> ct.getThanhTien() != null ? ct.getThanhTien() : BigDecimal.ZERO)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        // Cập nhật tổng tiền sản phẩm
        hoaDon.setTongTien(tongTienSanPhamMoi);

        // Tính tổng tiền trước giảm
        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO;
//        BigDecimal phiPhu = hoaDon.getPhiPhu() != null ? hoaDon.getPhiPhu() : BigDecimal.ZERO;
//        BigDecimal tongTienTruocGiam = tongTienSanPhamMoi.add(phiVanChuyen).add(phiPhu);
        BigDecimal tongTienTruocGiam = tongTienSanPhamMoi.add(phiVanChuyen);
        // Tính tiền giảm giá
        BigDecimal tienGiamGia = calculateTienGiamGia(hoaDon, tongTienTruocGiam);

        // Tính tổng tiền sau giảm
        BigDecimal tongTienSauGiam = tongTienTruocGiam.subtract(tienGiamGia);
        if (tongTienSauGiam.compareTo(BigDecimal.ZERO) < 0) {
            tongTienSauGiam = BigDecimal.ZERO;
        }

        // Cập nhật tổng tiền sau giảm
        hoaDon.setTongTienSauGiam(tongTienSauGiam);

        // ==================== TÍNH TOÁN SỐ TIỀN ĐÃ THANH TOÁN VÀ CÒN LẠI ====================
        // Lấy số tiền đã thanh toán từ request (nếu có)
        BigDecimal soTienThanhToan = request.getSoTienThanhToan() != null ?
                request.getSoTienThanhToan() : BigDecimal.ZERO;

        // Tính số tiền còn phải thanh toán
        BigDecimal soTienCanThanhToan = tongTienSauGiam.subtract(soTienThanhToan);
        if (soTienCanThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            soTienCanThanhToan = BigDecimal.ZERO;
        }

        // Ghi log về thay đổi tiền
        luuLichSu(hoaDon, "Cập nhật tổng tiền",
                String.format("Tổng tiền sản phẩm: %s, Phí vận chuyển: %s, Giảm giá: %s, Tổng cuối: %s, Đã thanh toán: %s, Còn lại: %s",
                        formatMoney(tongTienSanPhamMoi),
                        formatMoney(phiVanChuyen),
//                        formatMoney(phiPhu),
                        formatMoney(tienGiamGia),
                        formatMoney(tongTienSauGiam),
                        formatMoney(soTienThanhToan),
                        formatMoney(soTienCanThanhToan)), null);

        // ==================== CẬP NHẬT LỊCH SỬ THANH TOÁN ====================
        if (hasProductChanges || hasStatusChange) {
            updatePaymentHistory(hoaDon, tongTienSauGiam, hasStatusChange);
        }

        // Cập nhật ngày sửa
        hoaDon.setNgaySua(new Date());

        // Lưu hóa đơn
        HoaDon savedHoaDon = hoaDonRepository.save(hoaDon);

        // ==================== TRẢ VỀ RESPONSE ĐẦY ĐỦ ====================
        UpdateHoaDonResponse response = UpdateHoaDonResponse.builder()
                .success(true)
                .message("Cập nhật hóa đơn thành công")
                .diaChiCuThe(request.getDiaChiCuThe())
                .thanhPho(request.getThanhPho())
                .quan(request.getQuan())
//                .phiPhu(phiPhu)
                .phiPhuMoi(hoaDon.getPhiPhuMoi())
                .tongTienSanPham(tongTienSanPhamMoi)
                .phiVanChuyen(phiVanChuyen)
                .tienGiamGia(tienGiamGia)
                .tongTienSauGiam(tongTienSauGiam)
                .soTienThanhToan(soTienThanhToan)  // Thêm số tiền đã thanh toán
                .soTienCanThanhToan(soTienCanThanhToan)  // Thêm số tiền cần thanh toán
                .tongTienCanThanhToan(soTienCanThanhToan) // Để tương thích với code cũ
                .build();

        return response;
    }

    private void subtractInventory(HoaDon hoaDon) {
        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());

        if (chiTietList.isEmpty()) {
            System.out.println("⚠️ Hóa đơn không có sản phẩm để trừ tồn kho");
            return;
        }

        StringBuilder logMessage = new StringBuilder("Trừ tồn kho khi xác nhận đơn hàng: ");

        for (HoaDonChiTiet chiTiet : chiTietList) {
            ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
            int soLuongMua = chiTiet.getSoLuong();

            // Kiểm tra tồn kho
            if (ctsp.getSoLuongTon() < soLuongMua) {
                throw new RuntimeException(
                        String.format("Không đủ tồn kho cho sản phẩm: %s. Tồn kho: %d, Cần: %d",
                                ctsp.getSanPham().getTenSanPham(),
                                ctsp.getSoLuongTon(),
                                soLuongMua
                        )
                );
            }

            // Trừ tồn kho
            int tonKhoMoi = ctsp.getSoLuongTon() - soLuongMua;
            ctsp.setSoLuongTon(tonKhoMoi);
            chiTietSanPhamRepository.save(ctsp);

            logMessage.append(String.format("%s (-%d → %d), ",
                    ctsp.getSanPham().getTenSanPham(),
                    soLuongMua,
                    tonKhoMoi));

            System.out.println("📦 Trừ tồn kho: " + ctsp.getSanPham().getTenSanPham() +
                    " - Số lượng: " + soLuongMua +
                    " - Tồn kho mới: " + tonKhoMoi);
        }

        // Ghi log lịch sử
        luuLichSu(hoaDon, "Xác nhận đơn hàng - Trừ tồn kho",
                logMessage.toString().replaceAll(", $", ""), null);
    }

    /**
     * Điều chỉnh tồn kho khi thay đổi sản phẩm trong đơn hàng đã được xác nhận
     */
    private void adjustInventoryForProductChange(ChiTietSanPham ctsp, int newQuantity, int oldQuantity) {
        if (newQuantity == oldQuantity) {
            return; // Không thay đổi số lượng
        }

        int quantityDifference = newQuantity - oldQuantity;

        if (quantityDifference > 0) {
            // Tăng số lượng -> cần kiểm tra tồn kho và trừ thêm
            if (ctsp.getSoLuongTon() < quantityDifference) {
                throw new RuntimeException(
                        String.format("Không đủ tồn kho để tăng số lượng cho sản phẩm: %s. Tồn kho: %d, Cần thêm: %d",
                                ctsp.getSanPham().getTenSanPham(),
                                ctsp.getSoLuongTon(),
                                quantityDifference
                        )
                );
            }
            ctsp.setSoLuongTon(ctsp.getSoLuongTon() - quantityDifference);
            System.out.println("📦 Điều chỉnh tồn kho (tăng SL): " + ctsp.getSanPham().getTenSanPham() +
                    " - Thêm: " + quantityDifference + " - Tồn kho mới: " + ctsp.getSoLuongTon());
        } else {
            // Giảm số lượng -> trả lại tồn kho
            int returnQuantity = -quantityDifference;
            ctsp.setSoLuongTon(ctsp.getSoLuongTon() + returnQuantity);
            System.out.println("📦 Điều chỉnh tồn kho (giảm SL): " + ctsp.getSanPham().getTenSanPham() +
                    " - Trả lại: " + returnQuantity + " - Tồn kho mới: " + ctsp.getSoLuongTon());
        }

        chiTietSanPhamRepository.save(ctsp);
    }


    private void handleOrderCancellation(HoaDon hoaDon, boolean autoHoanTien) {
        System.out.println("🔄 Bắt đầu xử lý hủy đơn hàng #" + hoaDon.getMaHoaDon());

//        returnProductsToInventory(hoaDon);

        if (!autoHoanTien) {
            updatePaymentHistoryWhenCancelled(hoaDon);
            luuLichSu(hoaDon, "Hủy đơn hàng",
                    String.format("Đã hủy đơn hàng. Tổng tiền: %s - Cần thực hiện hoàn tiền thủ công nếu khách hàng yêu cầu.",
                            formatMoney(hoaDon.getTongTienSauGiam())), null);
        }

        System.out.println("✅ Đã xử lý hủy đơn hàng thành công");
    }



    private String getTrangThaiText(Integer trangThai) {
        if (trangThai == null) return "Không xác định";
        switch (trangThai) {
            case 0: return "Chờ xác nhận";
            case 1: return "Chờ giao hàng";
            case 2: return "Đang giao hàng";
            case 3: return "Đã hoàn thành";
            case 4: return "Đã hủy";
            default: return "Không xác định";
        }
    }

    private void updatePaymentHistoryWhenCancelled(HoaDon hoaDon) {
        try {
            List<LichSuThanhToan> lichSuThanhToanList = lichSuThanhToanRepository
                    .findByHoaDonIdOrderByNgayThanhToanDesc(hoaDon.getId());

            if (lichSuThanhToanList.isEmpty()) {
                System.out.println("⚠️ Không tìm thấy lịch sử thanh toán để cập nhật");
                return;
            }

            LichSuThanhToan lichSuMoiNhat = lichSuThanhToanList.get(0);

            String ghiChuCuMoi = "Đơn hàng đã hủy (ngày " +
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ") - Chưa hoàn tiền";

            if (lichSuMoiNhat.getGhiChu() != null &&
                    !lichSuMoiNhat.getGhiChu().contains("[HOÀN TIỀN]") &&
                    !lichSuMoiNhat.getGhiChu().contains("Đã hủy")) {
                lichSuMoiNhat.setGhiChu(ghiChuCuMoi);
                lichSuThanhToanRepository.save(lichSuMoiNhat);
            }

            System.out.println("📝 Đã cập nhật lịch sử thanh toán: HỦY ĐƠN (chưa hoàn tiền)");

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật lịch sử thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }


//    private void returnProductsToInventory(HoaDon hoaDon) {
//        List<HoaDonChiTiet> chiTietHoaDon = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());
//
//        if (chiTietHoaDon.isEmpty()) {
//            System.out.println("⚠️ Hóa đơn không có sản phẩm để trả về tồn kho");
//            return;
//        }
//
//        StringBuilder logMessage = new StringBuilder("Trả hàng về tồn kho: ");
//
//        for (HoaDonChiTiet chiTiet : chiTietHoaDon) {
//            ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
//            int soLuongTra = chiTiet.getSoLuong();
//
//            int tonKhoMoi = ctsp.getSoLuongTon() + soLuongTra;
//            ctsp.setSoLuongTon(tonKhoMoi);
//            chiTietSanPhamRepository.save(ctsp);
//
//            logMessage.append(String.format("%s (+%d), ",
//                    ctsp.getSanPham().getTenSanPham(), soLuongTra));
//
//            System.out.println("📦 Trả tồn kho: " + ctsp.getSanPham().getTenSanPham() +
//                    " - Số lượng: +" + soLuongTra +
//                    " - Tồn kho mới: " + tonKhoMoi);
//        }
//
//        // Ghi log lịch sử
//        luuLichSu(hoaDon, "Hủy đơn hàng - Trả hàng về tồn kho",
//                logMessage.toString().replaceAll(", $", ""), null);
//    }

    private void updatePaymentHistory(HoaDon hoaDon, BigDecimal tongTienSauGiam, boolean hasStatusChange) {
        try {
            PhuongThucThanhToan phuongThuc = null;
            if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
                phuongThuc = hoaDon.getHinhThucThanhToans().get(0).getPhuongThucThanhToan();
            } else {
                phuongThuc = phuongThucThanhToanRepository.findByTrangThai(true)
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán mặc định"));
            }

            boolean daCoLichSuThanhToan = lichSuThanhToanRepository.existsByHoaDonId(hoaDon.getId());

            LichSuThanhToan lichSuThanhToan;

            if (!daCoLichSuThanhToan) {
                lichSuThanhToan = new LichSuThanhToan();
                lichSuThanhToan.setHoaDon(hoaDon);
                lichSuThanhToan.setPhuongThucThanhToan(phuongThuc);
                lichSuThanhToan.setSoTien(tongTienSauGiam);
                lichSuThanhToan.setNgayThanhToan(new Date());
            } else {
                List<LichSuThanhToan> existingPayments = lichSuThanhToanRepository
                        .findByHoaDonIdOrderByNgayThanhToanDesc(hoaDon.getId());
                lichSuThanhToan = existingPayments.get(0);
                if (!lichSuThanhToan.getSoTien().equals(tongTienSauGiam)) {
                    lichSuThanhToan.setSoTien(tongTienSauGiam);
                    // KHÔNG cập nhật ngày thanh toán ở đây để không ảnh hưởng đến logic
                }
            }

            BigDecimal tongTienTruocGiam = calculateTongTienTruocGiam(hoaDon);
            BigDecimal tienGiamGia = tongTienTruocGiam.subtract(tongTienSauGiam);

            String oldGhiChu = lichSuThanhToan.getGhiChu();
            String newGhiChu = "";

            // ==================== LOGIC CHÍNH ====================
            boolean isOnlineOrder = !hoaDon.getLoaiHoaDon(); // false = online
            boolean isTienMat = phuongThuc.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt") ||
                    "COD".equalsIgnoreCase(phuongThuc.getMaPhuongThucThanhToan());
            Integer trangThaiHoaDon = hoaDon.getTrangThai();

            if (isOnlineOrder) {
                // HÓA ĐƠN ONLINE
                if (isTienMat) {
                    // Online + Tiền mặt (COD)
                    if (trangThaiHoaDon != null && trangThaiHoaDon == 3) {
                        // Trạng thái Đã hoàn thành -> Đã thanh toán
                        lichSuThanhToan.setTrangThai(true);
                        lichSuThanhToan.setNgayThanhToan(new Date());
                        newGhiChu = "Đã thanh toán (Tiền mặt/COD) - Hoàn thành - Số tiền: " +
                                formatMoney(tongTienSauGiam);
                    } else {
                        // Các trạng thái khác -> Chưa thanh toán
                        lichSuThanhToan.setTrangThai(false);
                        lichSuThanhToan.setNgayThanhToan(null);
                        newGhiChu = "Chưa thanh toán (Tiền mặt/COD) - " +
                                getTrangThaiText(trangThaiHoaDon) + " - Số tiền: " +
                                formatMoney(tongTienSauGiam);
                    }
                } else {
                    // Online + Chuyển khoản -> Luôn là Đã thanh toán
                    lichSuThanhToan.setTrangThai(true);
                    if (lichSuThanhToan.getNgayThanhToan() == null) {
                        lichSuThanhToan.setNgayThanhToan(new Date());
                    }
                    newGhiChu = "Đã thanh toán (Chuyển khoản) - " +
                            getTrangThaiText(trangThaiHoaDon) + " - Số tiền: " +
                            formatMoney(tongTienSauGiam);
                }
            } else {
                // HÓA ĐƠN TẠI QUẦY - Logic cũ
                switch (trangThaiHoaDon) {
                    case 0:
                        lichSuThanhToan.setTrangThai(false);
                        lichSuThanhToan.setNgayThanhToan(null);
                        newGhiChu = "Chờ xác nhận thanh toán - Số tiền: " + formatMoney(tongTienSauGiam);
                        break;
                    case 1:
                    case 2:
                    case 3:
                        lichSuThanhToan.setTrangThai(true);
                        if (lichSuThanhToan.getNgayThanhToan() == null) {
                            lichSuThanhToan.setNgayThanhToan(new Date());
                        }
                        newGhiChu = "Đã thanh toán - " + getTrangThaiText(trangThaiHoaDon) +
                                " - Số tiền: " + formatMoney(tongTienSauGiam);
                        break;
                    case 4:
                        lichSuThanhToan.setTrangThai(false);
                        newGhiChu = "Đơn hàng đã hủy - Số tiền: " + formatMoney(tongTienSauGiam);
                        break;
                    default:
                        lichSuThanhToan.setTrangThai(false);
                        newGhiChu = "Chờ xử lý thanh toán - Số tiền: " + formatMoney(tongTienSauGiam);
                }
            }

            // Thêm thông tin giảm giá nếu có
            if (tienGiamGia.compareTo(BigDecimal.ZERO) > 0) {
                newGhiChu += " (Đã giảm: " + formatMoney(tienGiamGia) + ")";
            }

            lichSuThanhToan.setGhiChu(newGhiChu);

            if (!newGhiChu.equals(oldGhiChu) || !daCoLichSuThanhToan) {
                lichSuThanhToanRepository.save(lichSuThanhToan);
                System.out.println("✅ Đã cập nhật lịch sử thanh toán: " + newGhiChu);
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xử lý lịch sử thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }

//    @Transactional
//    public void updatePaymentHistoryWhenStatusChanged(Integer idHoaDon, Integer newStatus) {
//        try {
//            HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
//
//            // Lấy lịch sử thanh toán gần nhất
//            List<LichSuThanhToan> lichSuList = lichSuThanhToanRepository
//                    .findByHoaDonIdOrderByNgayThanhToanDesc(idHoaDon);
//
//            if (lichSuList.isEmpty()) {
//                System.out.println("⚠️ Không tìm thấy lịch sử thanh toán để cập nhật");
//                return;
//            }
//
//            LichSuThanhToan lichSu = lichSuList.get(0);
//            PhuongThucThanhToan pt = lichSu.getPhuongThucThanhToan();
//
//            boolean isOnlineOrder = !hoaDon.getLoaiHoaDon();
//            boolean isTienMat = pt.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt") ||
//                    "COD".equalsIgnoreCase(pt.getMaPhuongThucThanhToan());
//
//            // Chỉ xử lý nếu là hóa đơn online + tiền mặt
//            if (isOnlineOrder && isTienMat) {
//                if (newStatus == 3) {
//                    // Chuyển sang trạng thái Hoàn thành -> Cập nhật thành Đã thanh toán
//                    lichSu.setTrangThai(true);
//                    lichSu.setNgayThanhToan(new Date());
//                    lichSu.setGhiChu("Đã thanh toán (Tiền mặt) - Hoàn thành - Số tiền: " +
//                            formatMoney(lichSu.getSoTien()));
//
//                    lichSuThanhToanRepository.save(lichSu);
//                    System.out.println("✅ Đã cập nhật lịch sử thanh toán khi đơn hàng hoàn thành");
//
//                    // Ghi log lịch sử
//                    luuLichSu(hoaDon, "Cập nhật thanh toán",
//                            "Cập nhật trạng thái thanh toán từ 'Chưa thanh toán' sang 'Đã thanh toán' khi đơn hàng hoàn thành",
//                            null);
//                }
//            }
//        } catch (Exception e) {
//            System.err.println("❌ Lỗi khi cập nhật lịch sử thanh toán theo trạng thái: " + e.getMessage());
//        }
//    }

    private BigDecimal calculateTienGiamGia(HoaDon hoaDon, BigDecimal tongTienTruocGiam) {
        if (hoaDon.getPhieuGiamGia() == null) {
            return BigDecimal.ZERO;
        }

        PhieuGiamGia pgg = hoaDon.getPhieuGiamGia();

        // === KIỂM TRA ĐIỀU KIỆN ÁP DỤNG ===
        if (pgg.getTrangThai() != 1) return BigDecimal.ZERO;

        LocalDate now = LocalDate.now();
        if (pgg.getNgayBatDau() != null && now.isBefore(pgg.getNgayBatDau())) return BigDecimal.ZERO;
        if (pgg.getNgayKetThuc() != null && now.isAfter(pgg.getNgayKetThuc())) return BigDecimal.ZERO;

        if (pgg.getGiaTriDonHangToiThieu() != null &&
                tongTienTruocGiam.compareTo(pgg.getGiaTriDonHangToiThieu()) < 0) {
            return BigDecimal.ZERO;
        }

        if (pgg.getSoLuongDung() != null && pgg.getSoLuongDung() <= 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal giaTriGiam = pgg.getGiaTriGiamGia() == null ? BigDecimal.ZERO : pgg.getGiaTriGiamGia();

        boolean giamTheoTien = Boolean.TRUE.equals(pgg.getLoaiGiamGia()); // <== quan trọng

        BigDecimal tienGiam;

        if (giamTheoTien) {
            // Giảm tiền cố định
            tienGiam = giaTriGiam.min(tongTienTruocGiam); // không giảm quá tổng tiền
        } else {
            // Giảm theo phần trăm
            tienGiam = tongTienTruocGiam.multiply(giaTriGiam)
                    .divide(BigDecimal.valueOf(100), 0, RoundingMode.HALF_UP);

            // Giới hạn mức giảm tối đa (nếu có)
            if (pgg.getMucGiaGiamToiDa() != null && pgg.getMucGiaGiamToiDa().compareTo(BigDecimal.ZERO) > 0) {
                tienGiam = tienGiam.min(pgg.getMucGiaGiamToiDa());
            }
        }

        System.out.println("Áp dụng phiếu " + pgg.getMaGiamGia() +
                " - Giảm: " + formatMoney(tienGiam));

        return tienGiam;
    }



    private BigDecimal calculateTongTienTruocGiam(HoaDon hoaDon) {
        try {
            BigDecimal tongTienSanPham = BigDecimal.ZERO;
            if (hoaDon.getHoaDonChiTiets() != null && !hoaDon.getHoaDonChiTiets().isEmpty()) {
                tongTienSanPham = hoaDon.getHoaDonChiTiets().stream()
                        .map(ct -> ct.getThanhTien() != null ? ct.getThanhTien() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO;

            return tongTienSanPham.add(phiVanChuyen);
        } catch (Exception e) {
            System.err.println("Lỗi tính tổng tiền trước giảm giá: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }


    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 ₫";
        return String.format("%,d ₫", amount.longValue());
    }

    public Optional<HoaDon> findById(Integer id) {
        return hoaDonRepository.findById(id);
    }

    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }

    public void luuLichSu(HoaDon hoaDon, String hanhDong, String moTa, Integer nguoiThucHien) {
        try {
            LichSuHoaDon lichSu = new LichSuHoaDon();
            lichSu.setHoaDon(hoaDon);
            lichSu.setKhachHang(hoaDon.getKhachHang());

            if (nguoiThucHien != null) {
                NhanVien nhanVien = nhanVienRepository.findById(nguoiThucHien).orElse(null);
                lichSu.setNhanVien(nhanVien);
            } else {
                lichSu.setNhanVien(hoaDon.getNhanVien());
            }

            lichSu.setHanhDong(hanhDong);
            lichSu.setMoTa(moTa);
            lichSu.setNgayCapNhat(new Date());
            lichSu.setTrangThai(true);

            lichSuHoaDonRepository.save(lichSu);
            System.out.println("✅ Đã lưu lịch sử: " + hanhDong);
        } catch (Exception e) {
            System.err.println("❌ Lỗi lưu lịch sử: " + e.getMessage());
        }
    }


    public List<LichSuHoaDon> getLichSuHoaDon(Integer hoaDonId) {
        return lichSuHoaDonRepository.findByHoaDon_IdOrderByNgayCapNhatDesc(hoaDonId);
    }



    @Transactional
    public String updateService(Integer invoiceId, Boolean loaiHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + invoiceId));

        Boolean oldService = hoaDon.getLoaiHoaDon();
        String oldServiceText = oldService != null && oldService ? "Tại quầy" : "Online";
        String newServiceText = loaiHoaDon ? "Tại quầy" : "Online";

        hoaDon.setLoaiHoaDon(loaiHoaDon);
        hoaDon.setNgaySua(new Date());
        hoaDonRepository.save(hoaDon);

        String moTa = String.format("Dịch vụ: '%s' → '%s'", oldServiceText, newServiceText);
        luuLichSu(hoaDon, "Cập nhật dịch vụ", moTa, null);

        return "Cập nhật dịch vụ từ " + oldServiceText + " sang " + newServiceText + " thành công";
    }

    @Transactional
    public HoaDon add(HoaDonRequest req) {
        HoaDon hoaDon = new HoaDon();

        // ==================== XỬ LÝ NGÀY TẠO ====================
        if (req.getNgayTao() != null) {
            hoaDon.setNgayTao(req.getNgayTao());
        } else {
            hoaDon.setNgayTao(new Date());
        }

        // ==================== XỬ LÝ NGÀY THANH TOÁN ====================
        if (req.getNgayThanhToan() != null) {
            hoaDon.setNgayThanhToan(req.getNgayThanhToan());
        } else {
            if (req.getTrangThai() == null || req.getTrangThai() != 0) {
                hoaDon.setNgayThanhToan(new Date());
            }
        }

        hoaDon.setNguoiTao(req.getNguoiTao() != null ? req.getNguoiTao() : 1);

        // ==================== XỬ LÝ SỐ TIỀN THANH TOÁN ====================
        BigDecimal soTienThanhToanValue;

        // Kiểm tra loại hóa đơn và phương thức thanh toán
        boolean isOnlineOrder = req.getLoaiHoaDon() != null && !req.getLoaiHoaDon(); // false = online
        boolean isTienMat = false;

        // Kiểm tra phương thức thanh toán
        if (req.getIdPhuongThucThanhToan() != null) {
            try {
                PhuongThucThanhToan pt = phuongThucThanhToanRepository.findById(req.getIdPhuongThucThanhToan())
                        .orElse(null);
                if (pt != null) {
                    isTienMat = pt.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt") ||
                            "COD".equalsIgnoreCase(pt.getMaPhuongThucThanhToan());
                }
            } catch (Exception e) {
                System.err.println("Lỗi khi kiểm tra phương thức thanh toán: " + e.getMessage());
            }
        }

        // Logic xác định số tiền đã thanh toán
        if (isOnlineOrder && isTienMat) {
            // Hóa đơn online + tiền mặt: chưa thanh toán (COD)
            soTienThanhToanValue = BigDecimal.ZERO;
            System.out.println("⚠️ Hóa đơn online + tiền mặt (COD): đặt soTienThanhToan = 0");
        } else if (isOnlineOrder && !isTienMat) {
            // Hóa đơn online + chuyển khoản: đã thanh toán
            soTienThanhToanValue = req.getSoTienThanhToan() != null ?
                    req.getSoTienThanhToan() :
                    (req.getTongTienSauGiam() != null ? req.getTongTienSauGiam() : req.getTongTien());
            System.out.println("✅ Hóa đơn online + chuyển khoản: đặt soTienThanhToan = " + formatMoney(soTienThanhToanValue));
        } else if (!isOnlineOrder) {
            // Hóa đơn tại quầy
            if (req.getTrangThai() != null && req.getTrangThai() != 0) {
                // Đã thanh toán tại quầy
                soTienThanhToanValue = req.getSoTienThanhToan() != null ?
                        req.getSoTienThanhToan() :
                        (req.getTongTienSauGiam() != null ? req.getTongTienSauGiam() : req.getTongTien());
                System.out.println("✅ Hóa đơn tại quầy (đã thanh toán): đặt soTienThanhToan = " + formatMoney(soTienThanhToanValue));
            } else {
                // Chưa thanh toán tại quầy
                soTienThanhToanValue = BigDecimal.ZERO;
                System.out.println("⚠️ Hóa đơn tại quầy (chưa thanh toán): đặt soTienThanhToan = 0");
            }
        } else {
            // Mặc định
            soTienThanhToanValue = req.getSoTienThanhToan() != null ?
                    req.getSoTienThanhToan() : BigDecimal.ZERO;
        }

        // Thiết lập ngày thanh toán
        if (req.getNgayThanhToan() != null) {
            hoaDon.setNgayThanhToan(req.getNgayThanhToan());
        } else {
            // Chỉ đặt ngày thanh toán nếu đã thanh toán
            if (soTienThanhToanValue.compareTo(BigDecimal.ZERO) > 0) {
                hoaDon.setNgayThanhToan(new Date());
            } else {
                hoaDon.setNgayThanhToan(null);
            }
        }

        hoaDon.setSoTienThanhToan(soTienThanhToanValue);
        hoaDon.setNguoiTao(req.getNguoiTao() != null ? req.getNguoiTao() : 1);

        System.out.println("💰 FINAL - soTienThanhToan được đặt: " + formatMoney(soTienThanhToanValue) +
                " | Loại đơn: " + (isOnlineOrder ? "Online" : "Tại quầy") +
                " | Phương thức: " + (isTienMat ? "Tiền mặt" : "Chuyển khoản"));

        // ==================== XỬ LÝ KHÁCH HÀNG ====================
        KhachHang khachHang = null;
        if (req.getIdKhachHang() != null) {
            khachHang = khachHangRepository.findById(req.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + req.getIdKhachHang()));
            hoaDon.setKhachHang(khachHang);

            System.out.println("=== DEBUG THÊM ĐỊA CHỈ BẮT ĐẦU ===");
            System.out.println("Khách hàng: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");

            if (req.getEmail() != null && !req.getEmail().isEmpty()) {
                khachHang.setEmail(req.getEmail());
                khachHangRepository.save(khachHang);
                System.out.println("✅ Đã cập nhật email cho khách hàng hiện có: " + req.getEmail());
            }

            // Xử lý địa chỉ cho khách hàng hiện có
            if (req.getDiaChiKhachHang() != null && !req.getDiaChiKhachHang().isEmpty() &&
                    !req.getDiaChiKhachHang().equals("Chưa có địa chỉ")) {

                List<DiaChi> existingAddresses = diaChiRepository.findByKhachHangId(khachHang.getId());
                System.out.println("Số địa chỉ hiện có: " + existingAddresses.size());

                if (existingAddresses.isEmpty()) {
                    System.out.println("✅ Khách hàng chưa có địa chỉ nào");

                    if (req.getIdTinh() != null && req.getIdQuan() != null) {
                        System.out.println("✅ Có đủ idTinh và idQuan");

                        try {
                            TinhThanh tinhThanh = tinhThanhRepository.findById(req.getIdTinh())
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tỉnh/thành ID: " + req.getIdTinh()));

                            QuanHuyen quanHuyen = quanHuyenRepository.findById(req.getIdQuan())
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện ID: " + req.getIdQuan()));

                            System.out.println("✅ Tìm thấy tỉnh: " + tinhThanh.getTenTinh() + " (ID: " + tinhThanh.getId() + ")");
                            System.out.println("✅ Tìm thấy quận: " + quanHuyen.getTenQuan() + " (ID: " + quanHuyen.getId() + ")");

                            DiaChi newAddress = new DiaChi();
                            newAddress.setKhachHang(khachHang);
                            newAddress.setTinhThanh(tinhThanh);
                            newAddress.setQuanHuyen(quanHuyen);
                            newAddress.setDiaChiCuThe(req.getDiaChiCuThe() != null ? req.getDiaChiCuThe() : req.getDiaChiKhachHang());
                            newAddress.setTrangThai(true);
                            newAddress.setTenDiaChi("Địa chỉ giao hàng");

                            DiaChi savedAddress = diaChiRepository.save(newAddress);

                            System.out.println("🎉 ĐÃ THÊM ĐỊA CHỈ MỚI THÀNH CÔNG!");
                            System.out.println("📍 Địa chỉ ID: " + savedAddress.getId());
                            System.out.println("📍 Chi tiết: " + savedAddress.getDiaChiCuThe());
                            System.out.println("📍 Tỉnh: " + savedAddress.getTinhThanh().getTenTinh());
                            System.out.println("📍 Quận: " + savedAddress.getQuanHuyen().getTenQuan());

                        } catch (Exception e) {
                            System.out.println("❌ Lỗi khi thêm địa chỉ: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("⚠️ Thiếu thông tin: idTinh=" + req.getIdTinh() + ", idQuan=" + req.getIdQuan());
                    }
                } else {
                    System.out.println("ℹ️ Khách hàng đã có địa chỉ, không thêm mới");
                    for (DiaChi addr : existingAddresses) {
                        System.out.println("   - Địa chỉ: " + addr.getDiaChiCuThe() + " (ID: " + addr.getId() + ")");
                    }
                }
            } else {
                System.out.println("⚠️ Không có diaChiKhachHang hoặc là 'Chưa có địa chỉ'");
            }
            System.out.println("=== DEBUG THÊM ĐỊA CHỈ KẾT THÚC ===");
        } else {
            // Tạo khách hàng mới nếu không có idKhachHang
            if (req.getHoTen() != null && !req.getHoTen().isEmpty() &&
                    req.getSdt() != null && !req.getSdt().isEmpty()) {

                try {
                    Optional<KhachHang> existingCustomer = khachHangRepository.findBySdt(req.getSdt());

                    if (existingCustomer.isPresent()) {
                        khachHang = existingCustomer.get();
                        System.out.println("✅ Sử dụng khách hàng đã tồn tại: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");
                        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
                            khachHang.setEmail(req.getEmail());
                            khachHangRepository.save(khachHang);
                            System.out.println("✅ Đã cập nhật email cho khách hàng hiện có: " + req.getEmail());
                        }
                    } else {
                        KhachHang newKhachHang = new KhachHang();
                        newKhachHang.setHoTen(req.getHoTen());
                        newKhachHang.setSdt(req.getSdt());
                        if (req.getEmail() != null && !req.getEmail().isEmpty()) {
                            newKhachHang.setEmail(req.getEmail());
                            System.out.println("✅ Đã thêm email cho khách hàng mới: " + req.getEmail());
                        }
                        newKhachHang.setGioiTinh(true);
                        newKhachHang.setNgaySinh(new Date());
                        newKhachHang.setTrangThai(true);
                        newKhachHang.setNgayTao(new Date());

                        khachHang = khachHangRepository.save(newKhachHang);
                        System.out.println("🎉 ĐÃ TẠO KHÁCH HÀNG MỚI: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");

                        // Thêm địa chỉ cho khách hàng mới
                        if (req.getIdTinh() != null && req.getIdQuan() != null && req.getDiaChiCuThe() != null) {
                            try {
                                TinhThanh tinhThanh = tinhThanhRepository.findById(req.getIdTinh())
                                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tỉnh/thành ID: " + req.getIdTinh()));

                                QuanHuyen quanHuyen = quanHuyenRepository.findById(req.getIdQuan())
                                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện ID: " + req.getIdQuan()));

                                DiaChi newAddress = new DiaChi();
                                newAddress.setKhachHang(khachHang);
                                newAddress.setTinhThanh(tinhThanh);
                                newAddress.setQuanHuyen(quanHuyen);
                                newAddress.setDiaChiCuThe(req.getDiaChiCuThe());
                                newAddress.setTrangThai(true);
                                newAddress.setTenDiaChi("Địa chỉ mặc định");

                                DiaChi savedAddress = diaChiRepository.save(newAddress);
                                System.out.println("📍 ĐÃ THÊM ĐỊA CHỈ CHO KHÁCH HÀNG MỚI: " + savedAddress.getDiaChiCuThe());

                            } catch (Exception e) {
                                System.out.println("⚠️ Không thể thêm địa chỉ cho khách hàng mới: " + e.getMessage());
                            }
                        }
                    }

                    hoaDon.setKhachHang(khachHang);

                } catch (Exception e) {
                    e.printStackTrace();
                    hoaDon.setKhachHang(null);
                }
            } else {
                hoaDon.setKhachHang(null);
            }
        }

        // ==================== XỬ LÝ NHÂN VIÊN ====================
        NhanVien nhanVien = nhanVienRepository.findById(
                req.getIdNhanVien() != null ? req.getIdNhanVien() : 1
        ).orElse(null);
        hoaDon.setNhanVien(nhanVien);

        // ==================== XỬ LÝ PHIẾU GIẢM GIÁ ====================
        PhieuGiamGia giamGia = null;
        if (req.getIdPhieuGiamGia() != null) {
            giamGia = phieuGiamGiaRepository.findById(req.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));

            if (giamGia.getSoLuongDung() <= 0) {
                throw new RuntimeException("Phiếu giảm giá này đã hết lượt sử dụng!");
            }

            giamGia.setSoLuongDung(giamGia.getSoLuongDung() - 1);
            phieuGiamGiaRepository.save(giamGia);

            hoaDon.setPhieuGiamGia(giamGia);
        }

        // ==================== THIẾT LẬP CÁC THUỘC TÍNH KHÁC ====================
        hoaDon.setLoaiHoaDon(req.getLoaiHoaDon() != null ? req.getLoaiHoaDon() : false);
        hoaDon.setPhiVanChuyen(req.getPhiVanChuyen() != null ? req.getPhiVanChuyen() : BigDecimal.ZERO);
        hoaDon.setTongTien(req.getTongTien() != null ? req.getTongTien() : BigDecimal.ZERO);
        hoaDon.setTongTienSauGiam(req.getTongTienSauGiam() != null ? req.getTongTienSauGiam() : BigDecimal.ZERO);
        hoaDon.setDiaChiKhachHang(req.getDiaChiKhachHang());
        hoaDon.setGhiChu(req.getGhiChu());

        // ==================== XỬ LÝ TRẠNG THÁI ====================
        Integer trangThai = req.getTrangThai();
        if (trangThai == null) {
            throw new RuntimeException("Trạng thái hóa đơn không được để trống");
        }
        hoaDon.setTrangThai(trangThai);

        // ==================== XỬ LÝ CHI TIẾT HÓA ĐƠN ====================
        if (req.getChiTietList() == null || req.getChiTietList().isEmpty()) {
            throw new RuntimeException("Không có chi tiết sản phẩm trong hóa đơn");
        }

        List<HoaDonChiTiet> listCT = new ArrayList<>();

        for (HoaDonChiTietRequest ctReq : req.getChiTietList()) {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctReq.getIdChiTietSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm"));

            BigDecimal giaGoc = ctsp.getGiaBan();
            BigDecimal giaSauGiam = getGiaSauGiamFromDotGiamGia(ctsp.getId(), giaGoc);

            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setHoaDon(hoaDon);
            hdct.setChiTietSanPham(ctsp);
            hdct.setSoLuong(ctReq.getSoLuong());
            hdct.setGiaBan(giaSauGiam);
            hdct.setThanhTien(giaSauGiam.multiply(BigDecimal.valueOf(ctReq.getSoLuong())));
            hdct.setGhiChu(ctReq.getGhiChu());
            hdct.setTrangThai(true);

            listCT.add(hdct);
        }

        hoaDon.setHoaDonChiTiets(listCT);

        // ==================== LƯU HÓA ĐƠN ====================
        HoaDon saved = hoaDonRepository.save(hoaDon);

        // ==================== QUAN TRỌNG: REFRESH ENTITY ĐỂ LẤY MÃ HÓA ĐƠN ====================
        // Flush để đảm bảo lưu xuống database
        hoaDonRepository.flush();

        // Refresh entity từ database để lấy computed column (maHoaDon)
        if (entityManager != null) {
            try {
                entityManager.refresh(saved);
                System.out.println("✅ Đã refresh entity từ database");
            } catch (Exception e) {
                System.err.println("❌ Lỗi khi refresh entity: " + e.getMessage());
                // Fallback: query lại entity
                saved = hoaDonRepository.findById(saved.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn sau khi lưu"));
            }
        } else {
            // Fallback nếu không có EntityManager
            saved = hoaDonRepository.findById(saved.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn sau khi lưu"));
        }

        System.out.println("✅ Mã hóa đơn sau khi refresh: " + saved.getMaHoaDon());
        System.out.println("✅ Thông tin hóa đơn: ID=" + saved.getId() + ", Mã HD=" + saved.getMaHoaDon());

        // ==================== GỬI EMAIL XÁC NHẬN (VỚI ENTITY ĐÃ ĐƯỢC REFRESH) ====================
        if (!saved.getLoaiHoaDon()) {
            try {
                String toEmail = null;

                if (saved.getKhachHang() != null && saved.getKhachHang().getEmail() != null) {
                    toEmail = saved.getKhachHang().getEmail();
                    System.out.println("📧 Lấy email từ khách hàng: " + toEmail);
                }

                // Nếu không có trong khách hàng, thử lấy từ request
                if ((toEmail == null || toEmail.isEmpty()) && req.getEmail() != null) {
                    toEmail = req.getEmail();
                    System.out.println("📧 Lấy email từ request: " + toEmail);
                }

                if (toEmail != null && !toEmail.isEmpty()) {
                    sendOrderConfirmationEmail(saved, toEmail);
                } else {
                    System.out.println("⚠️ Không có email để gửi xác nhận đơn hàng #" + saved.getId());
                }
            } catch (Exception e) {
                System.err.println("⚠️ Lỗi khi gửi email xác nhận: " + e.getMessage());
                e.printStackTrace();
                // KHÔNG throw exception ở đây để không rollback giao dịch
            }
        }

        // ==================== TẠO LỊCH SỬ HÓA ĐƠN ====================
        LichSuHoaDon log = new LichSuHoaDon();
        log.setHoaDon(saved);
        log.setKhachHang(khachHang);
        log.setNhanVien(saved.getNhanVien());
        log.setTrangThai(true);
        log.setNgayCapNhat(new Date());

        String customerInfo;
        if (khachHang != null) {
            customerInfo = "Khách hàng: " + khachHang.getHoTen();
        } else {
            if (req.getGhiChu() != null && req.getGhiChu().contains("Khách lẻ")) {
                customerInfo = "Khách lẻ (nhập thông tin giao hàng)";
            } else {
                customerInfo = "Khách lẻ";
            }
        }

        if (trangThai == 3) {
            log.setHanhDong("Thanh toán tại quầy");
            log.setMoTa("Hóa đơn #" + saved.getId() + " đã thanh toán thành công tại quầy. " + customerInfo);
        } else if (trangThai == 1) {
            log.setHanhDong("Bán giao hàng");
            log.setMoTa("Hóa đơn #" + saved.getId() + " đã được tạo và đang chờ giao hàng. " + customerInfo);
        } else {
            log.setHanhDong("Tạo hóa đơn");
            log.setMoTa("Hóa đơn #" + saved.getId() + " đã được tạo với trạng thái: " + trangThai + ". " + customerInfo);
        }

        lichSuHoaDonRepository.save(log);

        // ==================== XỬ LÝ PHƯƠNG THỨC THANH TOÁN ====================
        if (req.getIdPhuongThucThanhToan() != null) {
            PhuongThucThanhToan pt = phuongThucThanhToanRepository.findById(req.getIdPhuongThucThanhToan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

            LichSuThanhToan ls = new LichSuThanhToan();
            ls.setHoaDon(saved);
            ls.setPhuongThucThanhToan(pt);
            ls.setSoTien(
                    req.getSoTienThanhToan() != null ?
                            req.getSoTienThanhToan() :
                            saved.getTongTienSauGiam()
            );

            // ==================== XỬ LÝ LOGIC THANH TOÁN ====================

            if (isOnlineOrder && isTienMat) {
                // Hóa đơn online + tiền mặt: chưa thanh toán
                ls.setTrangThai(false);
                if (trangThai == 3) {
                    // Nếu ngay lúc tạo đã là trạng thái hoàn thành
                    ls.setGhiChu("Đã thanh toán (Tiền mặt) - Hoàn thành - Số tiền: " +
                            formatMoney(ls.getSoTien()));
                    ls.setNgayThanhToan(new Date());
                    ls.setTrangThai(true);
                } else {
                    ls.setGhiChu("Chưa thanh toán (Tiền mặt) - Số tiền: " +
                            formatMoney(ls.getSoTien()));
                    ls.setNgayThanhToan(null); // Chưa thanh toán nên không có ngày
                }
            } else if (isOnlineOrder && !isTienMat) {
                // Hóa đơn online + chuyển khoản: đã thanh toán
                ls.setTrangThai(true);
                ls.setNgayThanhToan(new Date());
                ls.setGhiChu("Đã thanh toán (Chuyển khoản) - Số tiền: " +
                        formatMoney(ls.getSoTien()));
            } else {
                // Hóa đơn tại quầy: xử lý theo logic cũ
                if (trangThai != 0) {
                    ls.setNgayThanhToan(new Date());
                    ls.setTrangThai(true);
                    ls.setGhiChu("Đã thanh toán tại quầy - Số tiền: " +
                            formatMoney(ls.getSoTien()));
                } else {
                    ls.setTrangThai(false);
                    ls.setGhiChu("Chờ thanh toán tại quầy - Số tiền: " +
                            formatMoney(ls.getSoTien()));
                }
            }

            // Thêm ghi chú từ request nếu có
            if (req.getGhiChuThanhToan() != null) {
                ls.setGhiChu(ls.getGhiChu() + " - " + req.getGhiChuThanhToan());
            }

            lichSuThanhToanRepository.save(ls);

            boolean loaiThanhToan = !isTienMat;
            HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
            hinhThuc.setHoaDon(saved);
            hinhThuc.setPhuongThucThanhToan(pt);
            hinhThuc.setLoaiThanhToan(loaiThanhToan);
            hinhThuc.setTrangThai(true);
            hinhThucThanhToanRepository.save(hinhThuc);
        }

        System.out.println("🎉 HOÀN TẤT TẠO HÓA ĐƠN - ID: " + saved.getId() +
                ", Mã HD: " + saved.getMaHoaDon() +
                ", Trạng thái: " + saved.getTrangThai() +
                ", Loại: " + saved.getLoaiHoaDon() +
                ", Khách hàng: " + (khachHang != null ? khachHang.getHoTen() : "Khách lẻ"));

        return saved;
    }

    private void sendOrderConfirmationEmail(HoaDon hoaDon, String toEmail) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

            String subject = "Xác nhận đơn hàng #" + hoaDon.getMaHoaDon() + " - The Autumn";

            StringBuilder content = new StringBuilder();
            content.append("<!DOCTYPE html>");
            content.append("<html lang='vi'>");
            content.append("<head>");
            content.append("<meta charset='UTF-8'>");
            content.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
            content.append("<title>Xác nhận đơn hàng</title>");
            content.append("<style>");
            content.append("body { font-family: 'Arial', sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f9f9f9; }");
            content.append(".container { max-width: 600px; margin: 20px auto; background: #ffffff; border-radius: 10px; overflow: hidden; box-shadow: 0 4px 15px rgba(0,0,0,0.1); }");
            content.append(".header { background: linear-gradient(135deg, #E67E22, #D35400); color: white; padding: 30px 20px; text-align: center; }");
            content.append(".header h1 { margin: 0; font-size: 28px; font-weight: bold; }");
            content.append(".content { padding: 40px 30px; }");
            content.append(".info-box { background: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #E67E22; }");
            content.append(".product-table { width: 100%; border-collapse: collapse; margin: 20px 0; }");
            content.append(".product-table th { background: #E67E22; color: white; padding: 12px; text-align: left; }");
            content.append(".product-table td { padding: 12px; border-bottom: 1px solid #ddd; }");
            content.append(".total-box { background: #e7f3ff; padding: 15px; border-radius: 8px; margin: 20px 0; }");
            content.append(".footer { text-align: center; padding: 25px; font-size: 12px; color: #666; background: #f8f9fa; border-top: 1px solid #eee; }");
            content.append("</style>");
            content.append("</head>");
            content.append("<body>");
            content.append("<div class='container'>");
            content.append("<div class='header'>");
            content.append("<h1>THE AUTUMN</h1>");
            content.append("<p>Hệ thống thời trang cao cấp</p>");
            content.append("</div>");
            content.append("<div class='content'>");

            content.append("<h2 style='color: #E67E22; margin-top: 0;'>🎉 Đơn hàng của bạn đã được đặt thành công!</h2>");
            content.append("<p>Cảm ơn bạn đã đặt hàng tại <strong>The Autumn</strong>. Chúng tôi sẽ xử lý đơn hàng của bạn trong thời gian sớm nhất.</p>");

            // Thông tin đơn hàng
            content.append("<div class='info-box'>");
            content.append("<h3 style='color: #E67E22;'>📦 Thông tin đơn hàng</h3>");
            content.append("<p><strong>Mã đơn hàng:</strong> ").append(hoaDon.getMaHoaDon()).append("</p>");
            content.append("<p><strong>Ngày đặt:</strong> ").append(dateFormat.format(hoaDon.getNgayTao())).append("</p>");
            content.append("<p><strong>Trạng thái:</strong> ").append(getTrangThaiText(hoaDon.getTrangThai())).append("</p>");
            if (hoaDon.getKhachHang() != null) {
                content.append("<p><strong>Khách hàng:</strong> ").append(hoaDon.getKhachHang().getHoTen()).append("</p>");
                content.append("<p><strong>Số điện thoại:</strong> ").append(hoaDon.getKhachHang().getSdt()).append("</p>");
            }
            content.append("</div>");

            // Thông tin giao hàng
            if (hoaDon.getDiaChiKhachHang() != null && !hoaDon.getDiaChiKhachHang().isEmpty()) {
                content.append("<div class='info-box'>");
                content.append("<h3 style='color: #E67E22;'>📍 Địa chỉ giao hàng</h3>");
                content.append("<p>").append(hoaDon.getDiaChiKhachHang()).append("</p>");
                content.append("</div>");
            }

            // Chi tiết sản phẩm
            if (hoaDon.getHoaDonChiTiets() != null && !hoaDon.getHoaDonChiTiets().isEmpty()) {
                content.append("<h3 style='color: #E67E22;'>🛒 Chi tiết sản phẩm</h3>");
                content.append("<table class='product-table'>");
                content.append("<thead>");
                content.append("<tr>");
                content.append("<th>Sản phẩm</th>");
                content.append("<th>Số lượng</th>");
                content.append("<th>Đơn giá</th>");
                content.append("<th>Thành tiền</th>");
                content.append("</tr>");
                content.append("</thead>");
                content.append("<tbody>");

                for (HoaDonChiTiet ct : hoaDon.getHoaDonChiTiets()) {
                    content.append("<tr>");
                    if (ct.getChiTietSanPham() != null && ct.getChiTietSanPham().getSanPham() != null) {
                        content.append("<td>").append(ct.getChiTietSanPham().getSanPham().getTenSanPham()).append("</td>");
                    } else {
                        content.append("<td>Sản phẩm</td>");
                    }
                    content.append("<td>").append(ct.getSoLuong()).append("</td>");
                    content.append("<td>").append(currencyFormat.format(ct.getGiaBan())).append("</td>");
                    content.append("<td>").append(currencyFormat.format(ct.getThanhTien())).append("</td>");
                    content.append("</tr>");
                }

                content.append("</tbody>");
                content.append("</table>");
            }

            // Tổng thanh toán
            content.append("<div class='total-box'>");
            content.append("<h3 style='color: #E67E22;'>💰 Tổng thanh toán</h3>");
            content.append("<p><strong>Tổng tiền sản phẩm:</strong> ").append(currencyFormat.format(hoaDon.getTongTien() != null ? hoaDon.getTongTien() : BigDecimal.ZERO)).append("</p>");
            if (hoaDon.getPhiVanChuyen() != null && hoaDon.getPhiVanChuyen().compareTo(BigDecimal.ZERO) > 0) {
                content.append("<p><strong>Phí vận chuyển:</strong> ").append(currencyFormat.format(hoaDon.getPhiVanChuyen())).append("</p>");
            }
            if (hoaDon.getPhieuGiamGia() != null) {
                content.append("<p><strong>Giảm giá:</strong> -").append(currencyFormat.format(
                        hoaDon.getTongTien() != null && hoaDon.getTongTienSauGiam() != null ?
                                hoaDon.getTongTien().subtract(hoaDon.getTongTienSauGiam()) : BigDecimal.ZERO
                )).append("</p>");
            }
            content.append("<p style='font-size: 18px; font-weight: bold; color: #E67E22;'>");
            content.append("Tổng cộng: ").append(currencyFormat.format(hoaDon.getTongTienSauGiam() != null ? hoaDon.getTongTienSauGiam() : BigDecimal.ZERO));
            content.append("</p>");
            content.append("</div>");

            // Thông tin thêm
            content.append("<div class='info-box'>");
            content.append("<h3 style='color: #E67E22;'>📞 Liên hệ hỗ trợ</h3>");
            content.append("<p>Nếu bạn có bất kỳ câu hỏi nào về đơn hàng, vui lòng liên hệ:</p>");
            content.append("<p>📧 Email: TheAutumnShop@gmail.com</p>");
            content.append("<p>📞 Hotline: 0900 123 456</p>");
            content.append("<p>🕒 Giờ làm việc: 8:00 - 22:00 hàng ngày</p>");
            content.append("</div>");

            content.append("<p style='margin-top: 30px;'>Chúng tôi sẽ gửi thông báo cập nhật trạng thái đơn hàng qua email.</p>");
            content.append("<p>Trân trọng,<br><strong>Đội ngũ The Autumn</strong></p>");

            content.append("</div>"); // end content
            content.append("<div class='footer'>");
            content.append("<p><strong>The Autumn - Hệ thống thời trang cao cấp</strong></p>");
            content.append("<p>© 2025 The Autumn. All rights reserved.</p>");
            content.append("</div>");
            content.append("</div>"); // end container
            content.append("</body>");
            content.append("</html>");

            // Gửi email
            emailService.sendEmail(toEmail, subject, content.toString());

            System.out.println("📧 Đã gửi email xác nhận đến: " + toEmail + " cho đơn hàng #" + hoaDon.getId());

        } catch (Exception e) {
            System.err.println("❌ Lỗi gửi email xác nhận: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private BigDecimal getGiaSauGiamFromDotGiamGia(Integer idChiTietSanPham, BigDecimal giaGoc) {
        try {
            List<DotGiamGiaChiTiet> dotGiamGiaList = dotGiamGiaChiTietRepository
                    .findByChiTietSanPhamId(idChiTietSanPham);

            if (dotGiamGiaList.isEmpty()) {
                System.out.println("ℹ️ Không có đợt giảm giá cho sản phẩm ID: " + idChiTietSanPham + ", sử dụng giá gốc");
                return giaGoc;
            }

            LocalDate now = LocalDate.now();
            DotGiamGiaChiTiet dotGiamGiaActive = null;

            for (DotGiamGiaChiTiet dot : dotGiamGiaList) {
                DotGiamGia dotGiamGia = dot.getDotGiamGia();
                if (dotGiamGia != null &&
                        dotGiamGia.getTrangThai() == 1 &&
                        dotGiamGia.getNgayBatDau() != null &&
                        dotGiamGia.getNgayKetThuc() != null) {

                    boolean isAfterStart = now.isAfter(dotGiamGia.getNgayBatDau()) || now.isEqual(dotGiamGia.getNgayBatDau());
                    boolean isBeforeEnd = now.isBefore(dotGiamGia.getNgayKetThuc()) || now.isEqual(dotGiamGia.getNgayKetThuc());

                    if (isAfterStart && isBeforeEnd) {
                        dotGiamGiaActive = dot;
                        System.out.println("🎯 Tìm thấy đợt giảm giá active: " + dotGiamGia.getTenDot() +
                                " từ " + dotGiamGia.getNgayBatDau() + " đến " + dotGiamGia.getNgayKetThuc());
                        break;
                    }
                }
            }

            if (dotGiamGiaActive == null) {
                System.out.println("ℹ️ Không có đợt giảm giá active cho sản phẩm ID: " + idChiTietSanPham);
                return giaGoc;
            }

            DotGiamGia dotGiamGia = dotGiamGiaActive.getDotGiamGia();
            BigDecimal giaTriGiam = dotGiamGia.getGiaTriGiam();

            if (giaTriGiam == null) {
                System.out.println("⚠️ Đợt giảm giá không có giá trị giảm, sử dụng giá gốc");
                return giaGoc;
            }

            BigDecimal giaSauGiam;

            if (!dotGiamGia.getLoaiGiamGia()) {
                BigDecimal phanTramGiam = giaTriGiam.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                BigDecimal soTienGiam = giaGoc.multiply(phanTramGiam);
                giaSauGiam = giaGoc.subtract(soTienGiam);

                System.out.println("💰 Giảm " + giaTriGiam + "%: " + soTienGiam + " từ " + giaGoc + " xuống " + giaSauGiam);
            } else {
                giaSauGiam = giaGoc.subtract(giaTriGiam);

                if (giaSauGiam.compareTo(BigDecimal.ZERO) < 0) {
                    giaSauGiam = BigDecimal.ZERO;
                    System.out.println("⚠️ Giá sau giảm âm, đặt về 0");
                }

                System.out.println("💰 Giảm " + giaTriGiam + " VND: từ " + giaGoc + " xuống " + giaSauGiam);
            }

            if (dotGiamGiaActive.getGiaSauGiam() == null ||
                    !dotGiamGiaActive.getGiaSauGiam().equals(giaSauGiam)) {
                dotGiamGiaActive.setGiaSauGiam(giaSauGiam);
                dotGiamGiaChiTietRepository.save(dotGiamGiaActive);
                System.out.println("💾 Đã cập nhật giá sau giảm vào DotGiamGiaChiTiet: " + giaSauGiam);
            }

            return giaSauGiam;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy giá sau giảm cho sản phẩm ID " + idChiTietSanPham + ": " + e.getMessage());
            e.printStackTrace();
            return giaGoc;
        }
    }

//    @Transactional
//    public VNPayResponse createHoaDonAndPayWithVNPAY(HoaDonRequest request) {
//        try {
//            Integer trangThai;
//            if (request.getLoaiHoaDon() == null) {
//                trangThai = request.getTrangThai();
//                System.out.println("✅ Sử dụng trạng thái từ FE: " + trangThai);
//            }else {
//                if (Boolean.TRUE.equals(request.getLoaiHoaDon())) {
//                    trangThai = 3;
//                } else {
//                    trangThai = 1;
//                }
//            }
//            request.setTrangThai(trangThai);
//            request.setNgayTao(new Date());
//            request.setNgayThanhToan(null);
//
//            HoaDon savedHoaDon = add(request);
//
//            System.out.println("Invoice created - ID: " + savedHoaDon.getId() + ", Code: " + savedHoaDon.getMaHoaDon());
//
//            int amount = request.getTongTienSauGiam().intValue();
//            String orderInfo = "Thanh toan don hang " + savedHoaDon.getMaHoaDon();
//
//            System.out.println("Calling VNPay Service - Amount: " + amount + " (" + (amount * 100) + " VND)");
//            System.out.println("Order Info: " + orderInfo);
//
//            String paymentUrl = vnPayService.createOrder(amount, orderInfo, savedHoaDon.getId().toString());
//
//            VNPayResponse response = new VNPayResponse();
//            response.setPaymentUrl(paymentUrl);
//            response.setOrderId(savedHoaDon.getId());
//            response.setAmount(request.getTongTienSauGiam());
//            response.setOrderInfo(orderInfo);
//
//            System.out.println("=== VNPay Order Created Successfully ===");
//            return response;
//
//        } catch (Exception e) {
//            System.err.println("Error in createHoaDonAndPayWithVNPAY: " + e.getMessage());
//            e.printStackTrace();
//            throw new RuntimeException("Lỗi tạo thanh toán VNPay: " + e.getMessage(), e);
//        }
//    }

    public String handleVNPayReturn(Map<String, String> params) {
        String vnp_ResponseCode = params.get("vnp_ResponseCode");
        String vnp_TxnRef = params.get("vnp_TxnRef"); // orderId
        String vnp_Amount = params.get("vnp_Amount");

        try {
            Integer orderId = Integer.parseInt(vnp_TxnRef);
            HoaDon hoaDon = hoaDonRepository.findById(orderId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

            if ("00".equals(vnp_ResponseCode)) {
                hoaDon.setTrangThai(3);
                hoaDon.setNgayThanhToan(new Date());

                HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
                hinhThuc.setHoaDon(hoaDon);
                PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(2)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));
                hinhThuc.setPhuongThucThanhToan(pttt);
                hinhThucThanhToanRepository.save(hinhThuc);

                hoaDonRepository.save(hoaDon);
                return "Thanh toán thành công! Cảm ơn bạn đã mua hàng.";
            } else {
                hoaDon.setTrangThai(4);
                hoaDonRepository.save(hoaDon);
                return "Thanh toán thất bại. Vui lòng thử lại.";
            }
        } catch (Exception e) {
            return "Lỗi xử lý thanh toán: " + e.getMessage();
        }
    }

    @Transactional
    public void xoaChiTietSanPhamKhoiHoaDon(Integer idHoaDon, Integer idChiTietSanPham) {
        // Tìm hóa đơn
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + idHoaDon));

        // Tìm chi tiết sản phẩm trong hóa đơn
        HoaDonChiTiet chiTietToDelete = hoaDonChiTietRepository
                .findByHoaDonIdAndChiTietSanPhamId(idHoaDon, idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong hóa đơn"));

        ChiTietSanPham ctsp = chiTietToDelete.getChiTietSanPham();
        chiTietSanPhamRepository.save(ctsp);

        // Xóa chi tiết hóa đơn
        hoaDonChiTietRepository.delete(chiTietToDelete);

        capNhatTongTienHoaDon(hoaDon);

        luuLichSu(hoaDon, "Xóa sản phẩm khỏi hóa đơn",
                String.format("Đã xóa sản phẩm: %s ",
                        ctsp.getSanPham().getTenSanPham()), null);

        System.out.println("✅ Đã xóa sản phẩm khỏi hóa đơn: " + ctsp.getSanPham().getTenSanPham());
    }

    private void capNhatTongTienHoaDon(HoaDon hoaDon) {
        // Tính lại tổng tiền từ các chi tiết còn lại
        BigDecimal tongTienSanPham = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId())
                .stream()
                .map(ct -> ct.getThanhTien() != null ? ct.getThanhTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO;
        BigDecimal tongTienTruocGiam = tongTienSanPham.add(phiVanChuyen);

        // Tính lại tổng tiền sau giảm giá
        BigDecimal tienGiamGia = calculateTienGiamGia(hoaDon, tongTienTruocGiam);
        BigDecimal tongTienSauGiam = tongTienTruocGiam.subtract(tienGiamGia);

        // Cập nhật hóa đơn
        hoaDon.setTongTien(tongTienTruocGiam);
        hoaDon.setTongTienSauGiam(tongTienSauGiam.compareTo(BigDecimal.ZERO) > 0 ? tongTienSauGiam : BigDecimal.ZERO);

        hoaDonRepository.save(hoaDon);

        System.out.println("💰 Đã cập nhật tổng tiền hóa đơn: " + formatMoney(tongTienSauGiam));
    }

//    public String handleVNPayIPN(Map<String, String> params) {
//        boolean isValid = vnPayService.validateIPN(params);
//
//        if (!isValid) {
//            return "Invalid signature";
//        }
//
//        String vnp_ResponseCode = params.get("vnp_ResponseCode");
//        String vnp_TxnRef = params.get("vnp_TxnRef");
//
//        try {
//            Integer orderId = Integer.parseInt(vnp_TxnRef);
//            HoaDon hoaDon = hoaDonRepository.findById(orderId)
//                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
//
//            if ("00".equals(vnp_ResponseCode)) {
//                hoaDon.setTrangThai(3);
//                hoaDon.setNgayThanhToan(new Date());
//                hoaDonRepository.save(hoaDon);
//            }
//
//            return "OK";
//        } catch (Exception e) {
//            return "ERROR";
//        }
//    }

    @Transactional
    public HoanTienResponse hoanTienHoaDon(Integer idHoaDon, HoanTienRequest request) {
        try {
            // 1. Lấy hóa đơn
            HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + idHoaDon));

            // 2. Lấy toàn bộ lịch sử thanh toán
            List<LichSuThanhToan> lichSu = lichSuThanhToanRepository
                    .findByHoaDonIdOrderByNgayThanhToanDesc(idHoaDon);

            if (lichSu.isEmpty()) {
                throw new RuntimeException("Không tìm thấy lịch sử thanh toán để hoàn tiền");
            }

            // 3. Kiểm tra điều kiện hoàn tiền
            validateHoanTienConditions(hoaDon, lichSu);

            // 4. Lấy thanh toán gần nhất
            LichSuThanhToan thanhToanGanNhat = lichSu.get(0);

            // 5. Tạo bản ghi hoàn tiền
            LichSuThanhToan lichSuHoanTien = createLichSuHoanTien(
                    hoaDon,
                    thanhToanGanNhat,
                    request
            );

            // 6. Cập nhật số tiền đã thanh toán về 0
            hoaDon.setSoTienThanhToan(BigDecimal.ZERO);

            // 7. Cập nhật trạng thái hóa đơn
            updateTrangThaiHoaDonKhiHoanTien(hoaDon, request);

            // 8. Trả hàng về kho khi hủy
            if (request.getLyDoHoanTien() != null &&
                    request.getLyDoHoanTien().toLowerCase().contains("hủy")) {
                // returnProductsToInventory(hoaDon);
            }

            // 9. Lưu hóa đơn
            hoaDonRepository.save(hoaDon);

            // 10. Lưu lịch sử chi tiết
            luuLichSuHoanTien(hoaDon, lichSuHoanTien, request);

            // 11. Gửi mail nếu có
            sendEmailHoanTien(hoaDon, lichSuHoanTien);

            return new HoanTienResponse(
                    true,
                    "Hoàn tiền thành công",
                    lichSuHoanTien.getId(),
                    lichSuHoanTien.getSoTien(),
                    lichSuHoanTien.getNgayThanhToan(),
                    lichSuHoanTien.getGhiChu()
            );

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi hoàn tiền hóa đơn: " + e.getMessage());
            throw new RuntimeException("Không thể hoàn tiền: " + e.getMessage());
        }
    }

    // Cập nhật phương thức validateHoanTienConditions để cho phép hoàn tiền khi soTienThanhToan > 0
    public void validateHoanTienConditions(HoaDon hoaDon, List<LichSuThanhToan> lichSuThanhToan) {

        boolean loaiHoaDonTaiQuay = Boolean.TRUE.equals(hoaDon.getLoaiHoaDon()); // true = tại quầy
        boolean loaiHoaDonOnline = Boolean.FALSE.equals(hoaDon.getLoaiHoaDon()); // false = online

        // ========== 1️⃣ KIỂM TRA LOẠI HÓA ĐƠN = TẠI QUẦY ==========
        if (loaiHoaDonTaiQuay) {
            // Kiểm tra xem có số tiền đã thanh toán không
            BigDecimal soTienDaThanhToan = hoaDon.getSoTienThanhToan() != null ?
                    hoaDon.getSoTienThanhToan() : BigDecimal.ZERO;

            if (soTienDaThanhToan.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Hóa đơn tại quầy chưa thanh toán, không thể hoàn tiền.");
            }

            // Kiểm tra trạng thái hóa đơn - cho phép hoàn tiền ngay cả khi chưa hủy
            // nhưng phải có số tiền đã thanh toán
            if (hoaDon.getTrangThai() == 0) {
                throw new RuntimeException("Hóa đơn tại quầy đang ở trạng thái chờ xác nhận, chưa thể hoàn tiền.");
            }

            return;
        }

        // ========== 2️⃣ KIỂM TRA HOÁ ĐƠN ONLINE ==========
        if (loaiHoaDonOnline) {
            // Kiểm tra xem có số tiền đã thanh toán không
            BigDecimal soTienDaThanhToan = BigDecimal.ZERO;

            // Ưu tiên lấy từ trường soTienThanhToan
            if (hoaDon.getSoTienThanhToan() != null) {
                soTienDaThanhToan = hoaDon.getSoTienThanhToan();
            } else {
                // Hoặc tính từ lịch sử thanh toán
                if (!lichSuThanhToan.isEmpty()) {
                    soTienDaThanhToan = lichSuThanhToan.stream()
                            .filter(ls -> ls.getTrangThai() != null && ls.getTrangThai())
                            .map(LichSuThanhToan::getSoTien)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                }
            }

            if (soTienDaThanhToan.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Đơn hàng online chưa được thanh toán, không thể hoàn tiền.");
            }

            // Lấy lịch sử thanh toán gần nhất
            LichSuThanhToan lsMoiNhat = lichSuThanhToan.isEmpty() ? null : lichSuThanhToan.get(0);
            PhuongThucThanhToan pt = lsMoiNhat != null ? lsMoiNhat.getPhuongThucThanhToan() : null;
            String maPT = pt != null ? pt.getMaPhuongThucThanhToan() : null;

            // Nếu là thanh toán COD (tiền mặt)
            if ("COD".equalsIgnoreCase(maPT) || "TIEN_MAT".equalsIgnoreCase(maPT)) {
                throw new RuntimeException("Đơn hàng COD (trả tiền khi nhận hàng) chưa thanh toán, không thể hoàn tiền.");
            }
        }

        // ========== 3️⃣ KIỂM TRA CHUNG (CẢ HAI LOẠI ĐƠN) ==========

        // Tổng đã hoàn
        BigDecimal tongTienDaHoan = lichSuThanhToan.stream()
                .filter(ls -> ls.getGhiChu() != null && ls.getGhiChu().contains("[HOÀN TIỀN]"))
                .map(LichSuThanhToan::getSoTien)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Lấy số tiền đã thanh toán hiện tại
        BigDecimal soTienDaThanhToan = hoaDon.getSoTienThanhToan() != null ?
                hoaDon.getSoTienThanhToan() : BigDecimal.ZERO;

        if (tongTienDaHoan.compareTo(soTienDaThanhToan) >= 0) {
            throw new RuntimeException("Hóa đơn đã hoàn đủ số tiền, không thể hoàn thêm.");
        }

        // Kiểm tra quá 30 ngày
        if (hoaDon.getNgayThanhToan() != null) {
            LocalDate ngayTT = hoaDon.getNgayThanhToan().toInstant()
                    .atZone(ZoneId.systemDefault()).toLocalDate();

            if (ChronoUnit.DAYS.between(ngayTT, LocalDate.now()) > 30) {
                throw new RuntimeException("Đã quá 30 ngày kể từ ngày thanh toán, không thể hoàn tiền.");
            }
        }
    }

    // Cập nhật phương thức updateTrangThaiHoaDonKhiHoanTien để reset ngày thanh toán
    private void updateTrangThaiHoaDonKhiHoanTien(HoaDon hoaDon, HoanTienRequest request) {
        // Reset ngày thanh toán khi hoàn tiền
        hoaDon.setNgayThanhToan(null);

        // Cập nhật trạng thái nếu là hủy đơn
        if (request.getLyDoHoanTien() != null &&
                request.getLyDoHoanTien().toLowerCase().contains("hủy")) {
            hoaDon.setTrangThai(4); // Trạng thái đã hủy
        }
    }

    // Thêm phương thức để tạo ghi chú hoàn tiền chi tiết hơn
    private LichSuThanhToan createLichSuHoanTien(
            HoaDon hoaDon,
            LichSuThanhToan lichSuCu,
            HoanTienRequest request
    ) {
        LichSuThanhToan lichSuHoanTien = new LichSuThanhToan();
        lichSuHoanTien.setHoaDon(hoaDon);
        lichSuHoanTien.setPhuongThucThanhToan(lichSuCu.getPhuongThucThanhToan());

        // Lấy số tiền đã thanh toán hiện tại
        BigDecimal soTienDaThanhToan = hoaDon.getSoTienThanhToan() != null ?
                hoaDon.getSoTienThanhToan() : lichSuCu.getSoTien();

        // Số tiền hoàn có thể là toàn bộ hoặc một phần
        BigDecimal soTienHoan;
        if (request.getSoTienHoan() != null &&
                request.getSoTienHoan().compareTo(BigDecimal.ZERO) > 0) {
            // Hoàn một phần
            soTienHoan = request.getSoTienHoan().min(soTienDaThanhToan);
        } else {
            // Hoàn toàn bộ
            soTienHoan = soTienDaThanhToan;
        }

        lichSuHoanTien.setSoTien(soTienHoan);
        lichSuHoanTien.setNgayThanhToan(new Date());
        lichSuHoanTien.setTrangThai(false); // Đặt false vì đây là giao dịch hoàn tiền (không phải thanh toán)

        // Tạo ghi chú chi tiết
        String ghiChu = String.format(
                "[HOÀN TIỀN] %s - Số tiền hoàn: %s (Số tiền đã thanh toán trước đó: %s) - Phương thức: %s%s",
                request.getLyDoHoanTien() != null ? request.getLyDoHoanTien() : "Hoàn tiền đơn hàng",
                formatMoney(soTienHoan),
                formatMoney(soTienDaThanhToan),
                lichSuCu.getPhuongThucThanhToan().getTenPhuongThucThanhToan(),
                request.getGhiChuBoSung() != null ? " - " + request.getGhiChuBoSung() : ""
        );

        lichSuHoanTien.setGhiChu(ghiChu);

        // Cập nhật ghi chú của lịch sử cũ
        String ghiChuCuMoi = lichSuCu.getGhiChu() +
                String.format(" (Đã hoàn tiền %s ngày %s)",
                        formatMoney(soTienHoan),
                        new SimpleDateFormat("dd/MM/yyyy").format(new Date()));

        lichSuCu.setGhiChu(ghiChuCuMoi);
        lichSuThanhToanRepository.save(lichSuCu);

        return lichSuThanhToanRepository.save(lichSuHoanTien);
    }


    private void luuLichSuHoanTien(HoaDon hoaDon, LichSuThanhToan lichSuHoanTien, HoanTienRequest request) {
        String moTa = String.format(
                "Đã hoàn tiền: %s - Số tiền: %s - Phương thức: %s - Lý do: %s%s",
                formatMoney(lichSuHoanTien.getSoTien()),
                formatMoney(lichSuHoanTien.getSoTien()),
                lichSuHoanTien.getPhuongThucThanhToan().getTenPhuongThucThanhToan(),
                request.getLyDoHoanTien() != null ? request.getLyDoHoanTien() : "Hoàn tiền",
                request.getGhiChuBoSung() != null ? " - " + request.getGhiChuBoSung() : ""
        );

        luuLichSu(hoaDon, "Hoàn tiền hóa đơn", moTa, request.getIdNhanVienThucHien());
    }
    // Thêm phương thức lấy thông tin thanh toán trước khi thực hiện
    public ThanhToanResponse getThongTinThanhToan(Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + idHoaDon));

        // Kiểm tra xem hóa đơn đã được thanh toán chưa
        if (hoaDon.getTrangThai() == 3) {
            throw new RuntimeException("Hóa đơn đã được thanh toán hoàn tất");
        }

        if (hoaDon.getTrangThai() == 4) {
            throw new RuntimeException("Hóa đơn đã bị hủy, không thể thanh toán");
        }

        // Tính toán số tiền cần thanh toán
        BigDecimal tongTienSauGiam = hoaDon.getTongTienSauGiam() != null ?
                hoaDon.getTongTienSauGiam() : BigDecimal.ZERO;

        // Lấy số tiền đã thanh toán
        BigDecimal soTienDaThanhToan = BigDecimal.ZERO;
        if (hoaDon.getSoTienThanhToan() != null) {
            soTienDaThanhToan = hoaDon.getSoTienThanhToan();
        } else {
            // Hoặc tính từ lịch sử thanh toán
            List<LichSuThanhToan> lichSu = lichSuThanhToanRepository
                    .findByHoaDonIdOrderByNgayThanhToanDesc(idHoaDon);

            if (!lichSu.isEmpty()) {
                soTienDaThanhToan = lichSu.stream()
                        .filter(ls -> ls.getTrangThai() != null && ls.getTrangThai())
                        .map(LichSuThanhToan::getSoTien)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }
        }

        BigDecimal soTienCanThanhToan = tongTienSauGiam.subtract(soTienDaThanhToan);
        if (soTienCanThanhToan.compareTo(BigDecimal.ZERO) < 0) {
            soTienCanThanhToan = BigDecimal.ZERO;
        }

        ThanhToanResponse response = new ThanhToanResponse();
        response.setSuccess(true);
        response.setMessage("Lấy thông tin thanh toán thành công");
        response.setIdHoaDon(hoaDon.getId());
        response.setMaHoaDon(hoaDon.getMaHoaDon());
        response.setSoTienCanThanhToanTruoc(soTienCanThanhToan);
        response.setSoTienDaThanhToan(soTienDaThanhToan);
        response.setSoTienConLai(soTienCanThanhToan); // Ban đầu chưa thanh toán, số tiền còn lại = số tiền cần thanh toán

        // Lấy thông tin phương thức thanh toán hiện tại (nếu có)
        if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
            HinhThucThanhToan hinhThuc = hoaDon.getHinhThucThanhToans().get(0);
            if (hinhThuc.getPhuongThucThanhToan() != null) {
                response.setIdPhuongThucThanhToan(hinhThuc.getPhuongThucThanhToan().getId());
                response.setTenPhuongThucThanhToan(hinhThuc.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
            }
        }

        return response;
    }

    // Phương thức chính để cập nhật thanh toán
    @Transactional
    public ThanhToanResponse updateThanhToan(ThanhToanRequest request) {
        try {
            HoaDon hoaDon = hoaDonRepository.findById(request.getIdHoaDon())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + request.getIdHoaDon()));

            // ==================== KIỂM TRA ĐIỀU KIỆN ====================
            if (hoaDon.getTrangThai() == 3) {
                throw new RuntimeException("Hóa đơn đã được thanh toán hoàn tất");
            }

            if (hoaDon.getTrangThai() == 4) {
                throw new RuntimeException("Hóa đơn đã bị hủy, không thể thanh toán");
            }

            // Lấy thông tin phương thức thanh toán
            PhuongThucThanhToan phuongThucThanhToan = null;
            if (request.getIdPhuongThucThanhToan() != null) {
                phuongThucThanhToan = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán ID: " + request.getIdPhuongThucThanhToan()));
            }

            // ==================== TÍNH TOÁN SỐ TIỀN ====================
            BigDecimal tongTienSauGiam = hoaDon.getTongTienSauGiam() != null ?
                    hoaDon.getTongTienSauGiam() : BigDecimal.ZERO;

            // Lấy số tiền đã thanh toán trước đó
            BigDecimal soTienDaThanhToanTruoc = BigDecimal.ZERO;
            if (hoaDon.getSoTienThanhToan() != null) {
                soTienDaThanhToanTruoc = hoaDon.getSoTienThanhToan();
            }

            // Số tiền thanh toán lần này
            BigDecimal soTienThanhToanLanNay = request.getSoTienThanhToan() != null ?
                    request.getSoTienThanhToan() : BigDecimal.ZERO;

            if (soTienThanhToanLanNay.compareTo(BigDecimal.ZERO) <= 0) {
                throw new RuntimeException("Số tiền thanh toán phải lớn hơn 0");
            }

            // Tổng số tiền đã thanh toán sau cập nhật
            BigDecimal tongTienDaThanhToanSau = soTienDaThanhToanTruoc.add(soTienThanhToanLanNay);

            // Kiểm tra không thanh toán quá tổng tiền
            if (tongTienDaThanhToanSau.compareTo(tongTienSauGiam) > 0) {
                throw new RuntimeException(
                        String.format("Số tiền thanh toán vượt quá tổng tiền. Tổng tiền: %s, Đã thanh toán: %s, Thanh toán thêm: %s",
                                formatMoney(tongTienSauGiam),
                                formatMoney(soTienDaThanhToanTruoc),
                                formatMoney(soTienThanhToanLanNay)
                        )
                );
            }

            // Tính số tiền còn lại sau khi thanh toán
            BigDecimal soTienConLaiSau = tongTienSauGiam.subtract(tongTienDaThanhToanSau);

            // ==================== CẬP NHẬT HÓA ĐƠN ====================
            // Cập nhật số tiền đã thanh toán
            hoaDon.setSoTienThanhToan(tongTienDaThanhToanSau);

            // Nếu thanh toán đủ -> cập nhật trạng thái
            if (soTienConLaiSau.compareTo(BigDecimal.ZERO) == 0) {
                hoaDon.setNgayThanhToan(new Date());
            }

            hoaDon.setNgaySua(new Date());
            hoaDonRepository.save(hoaDon);

            // ==================== TẠO LỊCH SỬ THANH TOÁN ====================
            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setPhuongThucThanhToan(phuongThucThanhToan);
            lichSuThanhToan.setSoTien(soTienThanhToanLanNay);
            lichSuThanhToan.setNgayThanhToan(new Date());
            lichSuThanhToan.setTrangThai(true); // Đã thanh toán

            String ghiChu = String.format("Thanh toán %s số tiền: %s%s",
                    soTienConLaiSau.compareTo(BigDecimal.ZERO) == 0 ? "đủ" : "một phần",
                    formatMoney(soTienThanhToanLanNay),
                    request.getGhiChu() != null ? " - " + request.getGhiChu() : ""
            );

            if (request.getMaGiaoDich() != null && !request.getMaGiaoDich().isEmpty()) {
                ghiChu += String.format(" - Mã giao dịch: %s", request.getMaGiaoDich());
                lichSuThanhToan.setMaGiaoDich(request.getMaGiaoDich());
            }

            lichSuThanhToan.setGhiChu(ghiChu);
            lichSuThanhToanRepository.save(lichSuThanhToan);

            // ==================== CẬP NHẬT HÌNH THỨC THANH TOÁN ====================
            // Nếu có phương thức thanh toán mới, cập nhật hoặc tạo mới
            if (phuongThucThanhToan != null) {
                HinhThucThanhToan hinhThucThanhToan;

                if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
                    // Cập nhật phương thức thanh toán hiện tại
                    hinhThucThanhToan = hoaDon.getHinhThucThanhToans().get(0);
                    hinhThucThanhToan.setPhuongThucThanhToan(phuongThucThanhToan);
                } else {
                    // Tạo mới hình thức thanh toán
                    hinhThucThanhToan = new HinhThucThanhToan();
                    hinhThucThanhToan.setHoaDon(hoaDon);
                    hinhThucThanhToan.setPhuongThucThanhToan(phuongThucThanhToan);
                    hinhThucThanhToan.setTrangThai(true);
                }

                hinhThucThanhToanRepository.save(hinhThucThanhToan);
            }

            // ==================== GHI LỊCH SỬ HÓA ĐƠN ====================
            String moTaLichSu = String.format("Thanh toán %s số tiền: %s. Đã thanh toán: %s/%s, Còn lại: %s",
                    soTienConLaiSau.compareTo(BigDecimal.ZERO) == 0 ? "đủ" : "một phần",
                    formatMoney(soTienThanhToanLanNay),
                    formatMoney(tongTienDaThanhToanSau),
                    formatMoney(tongTienSauGiam),
                    formatMoney(soTienConLaiSau)
            );

            luuLichSu(hoaDon, "Cập nhật thanh toán", moTaLichSu, request.getIdNhanVienThucHien());

            // ==================== TRẢ VỀ KẾT QUẢ ====================
            ThanhToanResponse response = new ThanhToanResponse();
            response.setSuccess(true);
            response.setMessage(
                    soTienConLaiSau.compareTo(BigDecimal.ZERO) == 0 ?
                            "Thanh toán thành công toàn bộ hóa đơn" :
                            String.format("Thanh toán thành công một phần. Đã thanh toán: %s, Còn lại: %s",
                                    formatMoney(soTienThanhToanLanNay),
                                    formatMoney(soTienConLaiSau))
            );
            response.setIdHoaDon(hoaDon.getId());
            response.setMaHoaDon(hoaDon.getMaHoaDon());
            response.setSoTienCanThanhToanTruoc(tongTienSauGiam.subtract(soTienDaThanhToanTruoc));
            response.setSoTienDaThanhToan(soTienThanhToanLanNay);
            response.setSoTienConLai(soTienConLaiSau);
            response.setNgayThanhToan(new Date());

            if (phuongThucThanhToan != null) {
                response.setIdPhuongThucThanhToan(phuongThucThanhToan.getId());
                response.setTenPhuongThucThanhToan(phuongThucThanhToan.getTenPhuongThucThanhToan());
            }

            response.setMaGiaoDich(request.getMaGiaoDich());
            response.setGhiChu(ghiChu);

            System.out.println("✅ Đã cập nhật thanh toán cho hóa đơn #" + hoaDon.getMaHoaDon());

            return response;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật thanh toán: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật thanh toán: " + e.getMessage());
        }
    }

    // Phương thức thanh toán toàn bộ (tiện ích)
    @Transactional
    public ThanhToanResponse thanhToanToanBo(Integer idHoaDon, Integer idPhuongThucThanhToan, Integer idNhanVienThucHien) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + idHoaDon));

        ThanhToanResponse thongTin = getThongTinThanhToan(idHoaDon);
        BigDecimal soTienCanThanhToan = thongTin.getSoTienCanThanhToanTruoc();

        ThanhToanRequest request = new ThanhToanRequest();
        request.setIdHoaDon(idHoaDon);
        request.setSoTienThanhToan(soTienCanThanhToan);
        request.setIdPhuongThucThanhToan(idPhuongThucThanhToan);
        request.setIdNhanVienThucHien(idNhanVienThucHien);
        request.setGhiChu("Thanh toán toàn bộ hóa đơn");

        return updateThanhToan(request);
    }
    private void handleOrderCompletion(HoaDon hoaDon) {
        System.out.println("✅ Xử lý đơn hàng hoàn thành #" + hoaDon.getMaHoaDon());

        // 1. Tính toán số tiền chưa thanh toán
        BigDecimal tongTienSauGiam = hoaDon.getTongTienSauGiam() != null ?
                hoaDon.getTongTienSauGiam() : BigDecimal.ZERO;

        // Lấy số tiền đã thanh toán hiện tại
        BigDecimal soTienDaThanhToan = hoaDon.getSoTienThanhToan() != null ?
                hoaDon.getSoTienThanhToan() : BigDecimal.ZERO;

        // Tính số tiền chưa thanh toán
        BigDecimal soTienChuaThanhToan = tongTienSauGiam.subtract(soTienDaThanhToan);

        if (soTienChuaThanhToan.compareTo(BigDecimal.ZERO) > 0) {
            // 2. Cập nhật số tiền đã thanh toán = tổng tiền sau giảm
            BigDecimal soTienMoi = soTienDaThanhToan.add(soTienChuaThanhToan);
            hoaDon.setSoTienThanhToan(soTienMoi);
            hoaDon.setSoTienCanThanhToan(BigDecimal.ZERO);
            hoaDon.setNgayThanhToan(new Date());

            System.out.println("💰 Cập nhật số tiền đã thanh toán: " +
                    formatMoney(soTienDaThanhToan) + " → " + formatMoney(soTienMoi));

            // 3. Cập nhật lịch sử thanh toán
            updatePaymentHistoryWhenCompleted(hoaDon, soTienChuaThanhToan);

            // 4. Ghi log lịch sử
            luuLichSu(hoaDon, "Hoàn thành đơn hàng",
                    String.format("Đã hoàn thành đơn hàng. Cập nhật số tiền đã thanh toán: %s → %s. Đã thanh toán thêm: %s",
                            formatMoney(soTienDaThanhToan),
                            formatMoney(soTienMoi),
                            formatMoney(soTienChuaThanhToan)), null);
        } else {
            System.out.println("ℹ️ Đơn hàng đã thanh toán đủ, không cần cập nhật");
        }
    }
    private void updatePaymentHistoryWhenCompleted(HoaDon hoaDon, BigDecimal soTienChuaThanhToan) {
        try {
            // Lấy phương thức thanh toán từ hóa đơn
            PhuongThucThanhToan phuongThuc = null;
            if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
                phuongThuc = hoaDon.getHinhThucThanhToans().get(0).getPhuongThucThanhToan();
            } else {
                // Nếu không có, lấy phương thức mặc định (Tiền mặt)
                phuongThuc = phuongThucThanhToanRepository.findByTrangThai(true)
                        .stream()
                        .filter(pt -> pt.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt"))
                        .findFirst()
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán mặc định"));
            }

            // Tạo lịch sử thanh toán mới cho số tiền chưa thanh toán
            LichSuThanhToan lichSuThanhToan = new LichSuThanhToan();
            lichSuThanhToan.setHoaDon(hoaDon);
            lichSuThanhToan.setPhuongThucThanhToan(phuongThuc);
            lichSuThanhToan.setSoTien(soTienChuaThanhToan);
            lichSuThanhToan.setNgayThanhToan(new Date());
            lichSuThanhToan.setTrangThai(true);

            // Xác định ghi chú dựa trên loại hóa đơn
            String ghiChu;
            if (Boolean.TRUE.equals(hoaDon.getLoaiHoaDon())) {
                // Hóa đơn tại quầy
                ghiChu = String.format("Thanh toán nốt khi hoàn thành đơn hàng tại quầy - Số tiền: %s",
                        formatMoney(soTienChuaThanhToan));
            } else {
                // Hóa đơn online
                if (phuongThuc.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt")) {
                    ghiChu = String.format("Thanh toán COD khi nhận hàng - Số tiền: %s",
                            formatMoney(soTienChuaThanhToan));
                } else {
                    ghiChu = String.format("Thanh toán hoàn tất khi giao hàng thành công - Số tiền: %s",
                            formatMoney(soTienChuaThanhToan));
                }
            }

            lichSuThanhToan.setGhiChu(ghiChu);
            lichSuThanhToanRepository.save(lichSuThanhToan);

            System.out.println("📝 Đã tạo lịch sử thanh toán khi hoàn thành đơn hàng: " + ghiChu);

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi cập nhật lịch sử thanh toán khi hoàn thành: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void sendEmailHoanTien(HoaDon hoaDon, LichSuThanhToan lichSuHoanTien) {
        if (hoaDon.getKhachHang() != null &&
                hoaDon.getKhachHang().getEmail() != null &&
                !hoaDon.getKhachHang().getEmail().isEmpty()) {

            try {
                String subject = "Thông báo hoàn tiền đơn hàng #" + hoaDon.getMaHoaDon();

                String content = String.format(
                        "<h3>Thông báo hoàn tiền đơn hàng</h3>" +
                                "<p>Kính gửi Quý khách %s,</p>" +
                                "<p>Chúng tôi xin thông báo đã hoàn tiền cho đơn hàng của Quý khách với thông tin sau:</p>" +
                                "<ul>" +
                                "<li><strong>Mã hóa đơn:</strong> %s</li>" +
                                "<li><strong>Số tiền hoàn:</strong> %s</li>" +
                                "<li><strong>Phương thức hoàn tiền:</strong> %s</li>" +
                                "<li><strong>Ngày hoàn tiền:</strong> %s</li>" +
                                "<li><strong>Ghi chú:</strong> %s</li>" +
                                "</ul>" +
                                "<p>Tiền sẽ được chuyển về tài khoản của Quý khách trong vòng 3-5 ngày làm việc.</p>" +
                                "<p>Trân trọng,<br>Đội ngũ %s</p>",
                        hoaDon.getKhachHang().getHoTen(),
                        hoaDon.getMaHoaDon(),
                        formatMoney(lichSuHoanTien.getSoTien()),
                        lichSuHoanTien.getPhuongThucThanhToan().getTenPhuongThucThanhToan(),
                        new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(lichSuHoanTien.getNgayThanhToan()),
                        lichSuHoanTien.getGhiChu(),
                        "Cửa hàng của bạn"
                );

                emailService.sendEmail(
                        hoaDon.getKhachHang().getEmail(),
                        subject,
                        content
                );

                System.out.println("✅ Đã gửi email thông báo hoàn tiền đến: " + hoaDon.getKhachHang().getEmail());

            } catch (Exception e) {
                System.err.println("❌ Lỗi gửi email hoàn tiền: " + e.getMessage());
            }
        }
    }
    private boolean kiemTraDaThanhToan(HoaDon hoaDon) {
        // Kiểm tra xem hóa đơn đã thanh toán chưa
        // Dựa vào trạng thái hoặc lịch sử thanh toán
        if (hoaDon.getTrangThai() != null) {
            // Trạng thái 3 (đã hoàn thành) thường là đã thanh toán
            return hoaDon.getTrangThai() == 3;
        }
        List<LichSuThanhToan> lichSu = lichSuThanhToanRepository
                .findByHoaDonIdOrderByNgayThanhToanDesc(hoaDon.getId());

        if (!lichSu.isEmpty()) {
            LichSuThanhToan ls = lichSu.get(0);
            return ls.getTrangThai() != null && ls.getTrangThai();
        }

        return false;
    }



    private BigDecimal tinhPhiPhuDoiDiaChi(HoaDon hoaDon) {

        return BigDecimal.valueOf(30000);
    }

    private BigDecimal tinhPhiPhuThemSanPham(HoaDon hoaDon, UpdateHoaDonRequest request) {
        // Logic tính phụ phí thêm sản phẩm
        // Ví dụ: mỗi sản phẩm thêm mới tính 10,000 VND

        if (request.getChiTietSanPhams() == null) {
            return BigDecimal.ZERO;
        }

        // Đếm số sản phẩm thêm mới
        List<HoaDonChiTiet> chiTietHienTai = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());
        Set<Integer> sanPhamHienTai = chiTietHienTai.stream()
                .map(ct -> ct.getChiTietSanPham().getId())
                .collect(Collectors.toSet());

        long soSanPhamThemMoi = request.getChiTietSanPhams().stream()
                .filter(sp -> !sanPhamHienTai.contains(sp.getIdChiTietSanPham()))
                .count();

        // Phí 10,000 VND cho mỗi sản phẩm thêm mới
        return BigDecimal.valueOf(soSanPhamThemMoi );
    }

    @Transactional
    public HoaDon addHoaDon(HoaDonRequest request) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setTrangThai(request.getTrangThai() != null ? request.getTrangThai() : 5);
        hoaDon.setNgayTao(new Date());
        hoaDon.setLoaiHoaDon(true);
        if (request.getIdNhanVien() == null) {
            throw new RuntimeException("ID nhân viên là bắt buộc");
        }
        NhanVien nhanVien = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại với ID: " + request.getIdNhanVien()));

        hoaDon.setNhanVien(nhanVien);
        HoaDon saved = hoaDonRepository.save(hoaDon);
        hoaDonRepository.flush();
        entityManager.clear();
        Optional<HoaDon> refreshed = hoaDonRepository.findById(saved.getId());

        return refreshed.orElse(saved);
    }

    public List<HoaDon> getHoaDonTheoTrangThai(Integer trangThai) {
        return hoaDonRepository.findByTrangThai(trangThai);
    }

    public void deleteHoaDon(Integer id) {
        hoaDonRepository.deleteById(id);
    }

    @Transactional
    public HoaDon updateHoaDon(Integer id, HoaDonRequest req) {
        // ==================== TÌM HÓA ĐƠN HIỆN TẠI ====================
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID: " + id));

        System.out.println("=== BẮT ĐẦU CẬP NHẬT HÓA ĐƠN TẠI QUẦY ID: " + id + " ===");
        System.out.println("Trạng thái trước: " + hoaDon.getTrangThai());
        System.out.println("Tổng tiền trước: " + hoaDon.getTongTien());

        // Đảm bảo là hóa đơn tại quầy
        hoaDon.setLoaiHoaDon(true);

        // ==================== XÁC ĐỊNH HÌNH THỨC BÁN ====================
        boolean isBanGiaoHang = false;
        String ghiChu = req.getGhiChu();

        if (ghiChu != null && !ghiChu.isEmpty()) {
            String ghiChuLower = ghiChu.toLowerCase();
            isBanGiaoHang = ghiChuLower.contains("giao hàng") ||
                    ghiChuLower.contains("ship") ||
                    ghiChuLower.contains("delivery") ||
                    ghiChuLower.contains("giao");

            System.out.println("📝 Ghi chú: " + ghiChu);
            System.out.println("🚚 Là bán giao hàng: " + isBanGiaoHang);
        }

        // ==================== XỬ LÝ TRẠNG THÁI MẶC ĐỊNH ====================
        Integer trangThai = req.getTrangThai();

        if (trangThai == null) {
            if (isBanGiaoHang) {
                trangThai = 2;
                System.out.println("📦 Hình thức: BÁN GIAO HÀNG → Trạng thái mặc định: 2 (Chờ giao hàng)");
            } else {
                trangThai = 3;
                System.out.println("🏪 Hình thức: BÁN TẠI CỬA HÀNG → Trạng thái mặc định: 3 (Đã hoàn thành)");
            }
        } else {
            System.out.println("📊 Sử dụng trạng thái từ request: " + trangThai);
        }

        hoaDon.setTrangThai(trangThai);

        // ==================== XỬ LÝ NGÀY THANH TOÁN ====================
        if (req.getNgayThanhToan() != null) {
            hoaDon.setNgayThanhToan(req.getNgayThanhToan());
        } else {
            hoaDon.setNgayThanhToan(new Date());
            System.out.println("✅ ĐÃ THANH TOÁN TẠI QUẦY → Ngày thanh toán: " + hoaDon.getNgayThanhToan());
        }

        // ==================== XỬ LÝ SỐ TIỀN THANH TOÁN ====================
        BigDecimal soTienThanhToanValue;

        if (req.getSoTienThanhToan() != null && req.getSoTienThanhToan().compareTo(BigDecimal.ZERO) > 0) {
            soTienThanhToanValue = req.getSoTienThanhToan();
        } else {
            soTienThanhToanValue = req.getTongTienSauGiam() != null ?
                    req.getTongTienSauGiam() :
                    (req.getTongTien() != null ? req.getTongTien() : hoaDon.getTongTienSauGiam());
        }

        if (soTienThanhToanValue == null) {
            soTienThanhToanValue = BigDecimal.ZERO;
        }

        if (soTienThanhToanValue.compareTo(BigDecimal.ZERO) < 0) {
            soTienThanhToanValue = BigDecimal.ZERO;
        }

        hoaDon.setSoTienThanhToan(soTienThanhToanValue);
        hoaDon.setNguoiTao(req.getNguoiTao() != null ? req.getNguoiTao() : hoaDon.getNguoiTao());

        System.out.println("💰 TẤT CẢ ĐƠN TẠI QUẦY ĐÃ THANH TOÁN = " + formatMoney(soTienThanhToanValue));

        // ==================== XỬ LÝ PHÍ VẬN CHUYỂN ====================
        if (isBanGiaoHang) {
            if (req.getPhiVanChuyen() != null && req.getPhiVanChuyen().compareTo(BigDecimal.ZERO) > 0) {
                hoaDon.setPhiVanChuyen(req.getPhiVanChuyen());
                System.out.println("🚚 Phí vận chuyển: " + formatMoney(req.getPhiVanChuyen()));
            } else {
                hoaDon.setPhiVanChuyen(BigDecimal.valueOf(30000));
                System.out.println("🚚 Phí vận chuyển mặc định: " + formatMoney(BigDecimal.valueOf(30000)));
            }
        } else {
            hoaDon.setPhiVanChuyen(BigDecimal.ZERO);
            System.out.println("🏪 Bán tại cửa hàng → Không có phí vận chuyển");
        }

        // ==================== XỬ LÝ KHÁCH HÀNG ====================
        KhachHang khachHang = hoaDon.getKhachHang();

        if (req.getIdKhachHang() != null) {
            // *** TRƯỜNG HỢP 1: CÓ ID KHÁCH HÀNG (KHÁCH HÀNG ĐÃ ĐĂNG KÝ) ***
            khachHang = khachHangRepository.findById(req.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + req.getIdKhachHang()));
            hoaDon.setKhachHang(khachHang);

            System.out.println("=== DEBUG CẬP NHẬT ĐỊA CHỈ BẮT ĐẦU ===");
            System.out.println("Khách hàng đã đăng ký: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");

            if (req.getEmail() != null && !req.getEmail().isEmpty()) {
                khachHang.setEmail(req.getEmail());
                khachHangRepository.save(khachHang);
                System.out.println("✅ Đã cập nhật email cho khách hàng hiện có: " + req.getEmail());
            }

            // Xử lý địa chỉ CHO KHÁCH HÀNG ĐÃ ĐĂNG KÝ - LƯU VÀO DANH SÁCH ĐỊA CHỈ
            if (isBanGiaoHang && req.getDiaChiKhachHang() != null && !req.getDiaChiKhachHang().isEmpty() &&
                    !req.getDiaChiKhachHang().equals("Chưa có địa chỉ")) {

                List<DiaChi> existingAddresses = diaChiRepository.findByKhachHangId(khachHang.getId());
                System.out.println("Số địa chỉ hiện có: " + existingAddresses.size());

                if (existingAddresses.isEmpty()) {
                    System.out.println("✅ Khách hàng chưa có địa chỉ nào");

                    if (req.getIdTinh() != null && req.getIdQuan() != null) {
                        System.out.println("✅ Có đủ idTinh và idQuan");

                        try {
                            TinhThanh tinhThanh = tinhThanhRepository.findById(req.getIdTinh())
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy tỉnh/thành ID: " + req.getIdTinh()));

                            QuanHuyen quanHuyen = quanHuyenRepository.findById(req.getIdQuan())
                                    .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện ID: " + req.getIdQuan()));

                            System.out.println("✅ Tìm thấy tỉnh: " + tinhThanh.getTenTinh() + " (ID: " + tinhThanh.getId() + ")");
                            System.out.println("✅ Tìm thấy quận: " + quanHuyen.getTenQuan() + " (ID: " + quanHuyen.getId() + ")");

                            DiaChi newAddress = new DiaChi();
                            newAddress.setKhachHang(khachHang);
                            newAddress.setTinhThanh(tinhThanh);
                            newAddress.setQuanHuyen(quanHuyen);
                            newAddress.setDiaChiCuThe(req.getDiaChiCuThe() != null ? req.getDiaChiCuThe() : req.getDiaChiKhachHang());
                            newAddress.setTrangThai(true);
                            newAddress.setTenDiaChi("Địa chỉ giao hàng");

                            DiaChi savedAddress = diaChiRepository.save(newAddress);

                            System.out.println("🎉 ĐÃ THÊM ĐỊA CHỈ MỚI VÀO DANH SÁCH ĐỊA CHỈ KHÁCH HÀNG!");
                            System.out.println("📍 Địa chỉ ID: " + savedAddress.getId());
                            System.out.println("📍 Chi tiết: " + savedAddress.getDiaChiCuThe());

                        } catch (Exception e) {
                            System.out.println("❌ Lỗi khi thêm địa chỉ: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println("⚠️ Thiếu thông tin: idTinh=" + req.getIdTinh() + ", idQuan=" + req.getIdQuan());
                    }
                } else {
                    System.out.println("ℹ️ Khách hàng đã có địa chỉ, không thêm mới");
                    for (DiaChi addr : existingAddresses) {
                        System.out.println("   - Địa chỉ: " + addr.getDiaChiCuThe() + " (ID: " + addr.getId() + ")");
                    }
                }
            } else if (!isBanGiaoHang) {
                System.out.println("ℹ️ Bán tại cửa hàng, không cần địa chỉ giao hàng");
            }
            System.out.println("=== DEBUG CẬP NHẬT ĐỊA CHỈ KẾT THÚC ===");
        } else {
            // *** TRƯỜNG HỢP 2: KHÔNG CÓ ID KHÁCH HÀNG (KHÁCH LẺ) ***
            System.out.println("=== XỬ LÝ KHÁCH LẺ ===");

            if (isBanGiaoHang) {
                // *** BÁN GIAO HÀNG VỚI KHÁCH LẺ: KHÔNG LƯU KHÁCH HÀNG MỚI ***
                System.out.println("🚚 BÁN GIAO HÀNG CHO KHÁCH LẺ - KHÔNG LƯU THÔNG TIN KHÁCH HÀNG");

                // Đặt khách hàng là null
                hoaDon.setKhachHang(null);

                // *** XỬ LÝ ĐỊA CHỈ: CHỈ LƯU VÀO HÓA ĐƠN ***
                StringBuilder diaChiBuilder = new StringBuilder();

                // Thêm tên khách hàng và số điện thoại (nếu có)
                if (req.getHoTen() != null && !req.getHoTen().isEmpty()) {
                    diaChiBuilder.append("Người nhận: ").append(req.getHoTen());
                }

                if (req.getSdt() != null && !req.getSdt().isEmpty()) {
                    if (diaChiBuilder.length() > 0) diaChiBuilder.append(" - ");
                    diaChiBuilder.append("SĐT: ").append(req.getSdt());
                }

                if (diaChiBuilder.length() > 0) {
                    diaChiBuilder.append("\n");
                }

                // Thêm địa chỉ cụ thể
                if (req.getDiaChiCuThe() != null && !req.getDiaChiCuThe().isEmpty()) {
                    diaChiBuilder.append(req.getDiaChiCuThe());
                } else if (req.getDiaChiKhachHang() != null && !req.getDiaChiKhachHang().isEmpty()) {
                    diaChiBuilder.append(req.getDiaChiKhachHang());
                }

                // Nếu có tỉnh/quận, thêm vào địa chỉ hiển thị
                if (req.getIdTinh() != null || req.getIdQuan() != null) {
                    try {
                        if (req.getIdQuan() != null) {
                            QuanHuyen quanHuyen = quanHuyenRepository.findById(req.getIdQuan())
                                    .orElse(null);
                            if (quanHuyen != null) {
                                if (diaChiBuilder.length() > 0) diaChiBuilder.append(", ");
                                diaChiBuilder.append(quanHuyen.getTenQuan());
                            }
                        }

                        if (req.getIdTinh() != null) {
                            TinhThanh tinhThanh = tinhThanhRepository.findById(req.getIdTinh())
                                    .orElse(null);
                            if (tinhThanh != null) {
                                if (diaChiBuilder.length() > 0) diaChiBuilder.append(", ");
                                diaChiBuilder.append(tinhThanh.getTenTinh());
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("⚠️ Không thể lấy thông tin tỉnh/quận: " + e.getMessage());
                    }
                }

                String diaChiHienThi = diaChiBuilder.toString().trim();

                if (!diaChiHienThi.isEmpty()) {
                    // *** CHỈ LƯU ĐỊA CHỈ VÀO HÓA ĐƠN ***
                    hoaDon.setDiaChiKhachHang(diaChiHienThi);
                    System.out.println("📍 Địa chỉ giao hàng (khách lẻ): " + diaChiHienThi);
                    System.out.println("ℹ️ Không lưu khách hàng mới, chỉ lưu địa chỉ vào hóa đơn");
                } else {
                    hoaDon.setDiaChiKhachHang("Khách lẻ - Không có địa chỉ chi tiết");
                    System.out.println("ℹ️ Không có địa chỉ chi tiết cho khách lẻ");
                }
            } else {
                // *** BÁN TẠI CỬA HÀNG VỚI KHÁCH LẺ ***
                System.out.println("🏪 BÁN TẠI CỬA HÀNG CHO KHÁCH LẺ");

                // Vẫn tạo/tìm khách hàng để lưu lịch sử
                if (req.getHoTen() != null && !req.getHoTen().isEmpty() &&
                        req.getSdt() != null && !req.getSdt().isEmpty()) {

                    try {
                        Optional<KhachHang> existingCustomer = khachHangRepository.findBySdt(req.getSdt());

                        if (existingCustomer.isPresent()) {
                            khachHang = existingCustomer.get();
                            System.out.println("✅ Sử dụng khách hàng đã tồn tại: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");
                        } else {
                            // Tạo khách hàng mới nhưng KHÔNG có địa chỉ
                            System.out.println("🎉 TẠO KHÁCH HÀNG LẺ CHO BÁN TẠI CỬA HÀNG");

                            KhachHang newKhachHang = new KhachHang();
                            newKhachHang.setHoTen(req.getHoTen());
                            newKhachHang.setSdt(req.getSdt());
                            if (req.getEmail() != null && !req.getEmail().isEmpty()) {
                                newKhachHang.setEmail(req.getEmail());
                                System.out.println("✅ Đã thêm email cho khách hàng mới: " + req.getEmail());
                            }
                            newKhachHang.setGioiTinh(true);
                            newKhachHang.setNgaySinh(new Date());
                            newKhachHang.setTrangThai(true);
                            newKhachHang.setNgayTao(new Date());

                            khachHang = khachHangRepository.save(newKhachHang);
                            System.out.println("✅ ĐÃ TẠO KHÁCH HÀNG LẺ: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");
                        }

                        hoaDon.setKhachHang(khachHang);

                    } catch (Exception e) {
                        e.printStackTrace();
                        hoaDon.setKhachHang(null);
                        System.out.println("❌ Lỗi khi xử lý thông tin khách lẻ");
                    }
                } else {
                    // Không có thông tin khách hàng
                    hoaDon.setKhachHang(null);
                    System.out.println("ℹ️ Không có thông tin khách hàng, đặt là null");
                }
            }
        }

        // ==================== XỬ LÝ NHÂN VIÊN ====================
        if (req.getIdNhanVien() != null) {
            NhanVien nhanVien = nhanVienRepository.findById(req.getIdNhanVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + req.getIdNhanVien()));
            hoaDon.setNhanVien(nhanVien);
            System.out.println("👤 Đã cập nhật nhân viên: " + nhanVien.getHoTen());
        }

        // ==================== XỬ LÝ PHIẾU GIẢM GIÁ ====================
        if (req.getIdPhieuGiamGia() != null) {
            PhieuGiamGia giamGia = phieuGiamGiaRepository.findById(req.getIdPhieuGiamGia())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phiếu giảm giá"));

            if (giamGia.getSoLuongDung() <= 0) {
                throw new RuntimeException("Phiếu giảm giá này đã hết lượt sử dụng!");
            }

            // Giảm số lượng sử dụng
            giamGia.setSoLuongDung(giamGia.getSoLuongDung() - 1);
            phieuGiamGiaRepository.save(giamGia);

            hoaDon.setPhieuGiamGia(giamGia);
            System.out.println("🎫 Đã áp dụng phiếu giảm giá: " + giamGia.getMaGiamGia());
        }

        // ==================== THIẾT LẬP CÁC THUỘC TÍNH KHÁC ====================
        if (req.getTongTien() != null) {
            hoaDon.setTongTien(req.getTongTien());
        }

        if (req.getTongTienSauGiam() != null) {
            hoaDon.setTongTienSauGiam(req.getTongTienSauGiam());
        } else if (req.getTongTien() != null) {
            hoaDon.setTongTienSauGiam(req.getTongTien());
        }

        // Lưu ý: Đối với khách lẻ giao hàng, địa chỉ đã được xử lý ở trên
        // Chỉ ghi đè nếu request có gửi diaChiKhachHang và là khách hàng đã đăng ký
        if (req.getIdKhachHang() != null && req.getDiaChiKhachHang() != null) {
            hoaDon.setDiaChiKhachHang(req.getDiaChiKhachHang());
        }

        if (req.getGhiChu() != null) {
            hoaDon.setGhiChu(req.getGhiChu());
        }

        // ==================== XỬ LÝ CHI TIẾT HÓA ĐƠN - FIX LỖI CASCADE ====================
        if (req.getChiTietList() != null) {
            System.out.println("🔄 Cập nhật chi tiết hóa đơn...");

            // Lấy collection hiện tại và clear nó
            // QUAN TRỌNG: Không tạo mới collection, chỉ clear collection hiện có
            List<HoaDonChiTiet> existingDetails = hoaDon.getHoaDonChiTiets();
            if (existingDetails != null) {
                existingDetails.clear(); // Cascade sẽ tự xóa các orphan
            } else {
                existingDetails = new ArrayList<>();
                hoaDon.setHoaDonChiTiets(existingDetails);
            }

            // Thêm chi tiết mới vào collection hiện có
            for (HoaDonChiTietRequest ctReq : req.getChiTietList()) {
                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctReq.getIdChiTietSanPham())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm ID: " + ctReq.getIdChiTietSanPham()));

                // Kiểm tra tồn kho
                if (ctsp.getSoLuongTon() < ctReq.getSoLuong()) {
                    throw new RuntimeException("Sản phẩm " + ctsp.getSanPham().getTenSanPham() +
                            " chỉ còn " + ctsp.getSoLuongTon() + " sản phẩm trong kho");
                }

                // Cập nhật tồn kho
                ctsp.setSoLuongTon(ctsp.getSoLuongTon() - ctReq.getSoLuong());
                chiTietSanPhamRepository.save(ctsp);

                // Tính giá bán
                BigDecimal giaGoc = ctsp.getGiaBan();
                BigDecimal giaSauGiam = getGiaSauGiamFromDotGiamGia(ctsp.getId(), giaGoc);

                // Tạo chi tiết hóa đơn
                HoaDonChiTiet hdct = new HoaDonChiTiet();
                hdct.setHoaDon(hoaDon); // QUAN TRỌNG: Phải set hoaDon
                hdct.setChiTietSanPham(ctsp);
                hdct.setSoLuong(ctReq.getSoLuong());
                hdct.setGiaBan(giaSauGiam);
                hdct.setThanhTien(giaSauGiam.multiply(BigDecimal.valueOf(ctReq.getSoLuong())));
                hdct.setGhiChu(ctReq.getGhiChu());
                hdct.setTrangThai(true);

                // Thêm vào collection hiện có
                existingDetails.add(hdct);

                System.out.println("➕ Đã thêm sản phẩm: " + ctsp.getSanPham().getTenSanPham() +
                        " x" + ctReq.getSoLuong() + " = " + formatMoney(hdct.getThanhTien()));
            }

            System.out.println("✅ Đã cập nhật " + existingDetails.size() + " sản phẩm trong hóa đơn");
        }

        // ==================== XỬ LÝ PHƯƠNG THỨC THANH TOÁN ====================
        if (req.getIdPhuongThucThanhToan() != null) {
            PhuongThucThanhToan pt = phuongThucThanhToanRepository.findById(req.getIdPhuongThucThanhToan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

            // Xóa lịch sử thanh toán cũ
            List<LichSuThanhToan> existingPayments = lichSuThanhToanRepository.findByHoaDonId(hoaDon.getId());
            if (!existingPayments.isEmpty()) {
                lichSuThanhToanRepository.deleteAll(existingPayments);
            }

            // Tạo lịch sử thanh toán mới
            LichSuThanhToan ls = new LichSuThanhToan();
            ls.setHoaDon(hoaDon);
            ls.setPhuongThucThanhToan(pt);
            ls.setSoTien(soTienThanhToanValue);
            ls.setNgayThanhToan(hoaDon.getNgayThanhToan());
            ls.setTrangThai(true);

            if (isBanGiaoHang) {
                ls.setGhiChu("Đã thanh toán tại quầy (Giao hàng) - Số tiền: " +
                        formatMoney(ls.getSoTien()));
            } else {
                ls.setGhiChu("Đã thanh toán tại quầy (Tại cửa hàng) - Số tiền: " +
                        formatMoney(ls.getSoTien()));
            }

            if (req.getGhiChuThanhToan() != null) {
                ls.setGhiChu(ls.getGhiChu() + " - " + req.getGhiChuThanhToan());
            }

            lichSuThanhToanRepository.save(ls);

            // Xác định loại thanh toán
            boolean isTienMat = pt.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt") ||
                    "COD".equalsIgnoreCase(pt.getMaPhuongThucThanhToan());
            boolean loaiThanhToan = !isTienMat;

            // Xóa hình thức thanh toán cũ
            List<HinhThucThanhToan> existingHinhThuc = hinhThucThanhToanRepository.findByHoaDonId(hoaDon.getId());
            if (!existingHinhThuc.isEmpty()) {
                hinhThucThanhToanRepository.deleteAll(existingHinhThuc);
            }

            // Tạo hình thức thanh toán mới
            HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
            hinhThuc.setHoaDon(hoaDon);
            hinhThuc.setPhuongThucThanhToan(pt);
            hinhThuc.setLoaiThanhToan(loaiThanhToan);
            hinhThuc.setTrangThai(true);
            hinhThucThanhToanRepository.save(hinhThuc);

            System.out.println("💳 Đã ghi nhận thanh toán: " + pt.getTenPhuongThucThanhToan());
        }

        // ==================== LƯU HÓA ĐƠN ====================
        HoaDon saved = hoaDonRepository.save(hoaDon);
        hoaDonRepository.flush();

        // Refresh để lấy thông tin đầy đủ
        if (entityManager != null) {
            try {
                entityManager.refresh(saved);
            } catch (Exception e) {
                saved = hoaDonRepository.findById(saved.getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn sau khi lưu"));
            }
        }

        System.out.println("✅ Mã hóa đơn sau khi refresh: " + saved.getMaHoaDon());

        // ==================== TẠO LỊCH SỬ HÓA ĐƠN ====================
        LichSuHoaDon log = new LichSuHoaDon();
        log.setHoaDon(saved);
        log.setKhachHang(khachHang);
        log.setNhanVien(saved.getNhanVien());
        log.setTrangThai(true);
        log.setNgayCapNhat(new Date());

        String customerInfo;
        if (khachHang != null) {
            customerInfo = "Khách hàng: " + khachHang.getHoTen();
        } else {
            customerInfo = "Khách lẻ";
        }

        if (isBanGiaoHang) {
            log.setHanhDong("Bán giao hàng tại quầy");
            log.setMoTa("Hóa đơn #" + saved.getMaHoaDon() + " đã thanh toán tại quầy và chờ giao hàng. " + customerInfo);
        } else {
            log.setHanhDong("Bán tại cửa hàng");
            log.setMoTa("Hóa đơn #" + saved.getMaHoaDon() + " đã thanh toán và hoàn thành tại quầy. " + customerInfo);
        }

        lichSuHoaDonRepository.save(log);

        System.out.println("🎉 HOÀN TẤT CẬP NHẬT HÓA ĐƠN TẠI QUẦY ===");
        System.out.println("📋 Mã HD: " + saved.getMaHoaDon());
        System.out.println("📊 Trạng thái: " + saved.getTrangThai() + " (" + (isBanGiaoHang ? "Chờ giao hàng" : "Đã hoàn thành") + ")");
        System.out.println("💰 Tổng tiền: " + formatMoney(saved.getTongTienSauGiam()));
        System.out.println("💳 Số tiền thanh toán: " + formatMoney(saved.getSoTienThanhToan()));
        System.out.println("🚚 Phí vận chuyển: " + formatMoney(saved.getPhiVanChuyen()));
        System.out.println("👤 Khách hàng: " + (khachHang != null ? khachHang.getHoTen() : "Khách lẻ"));
        System.out.println("📦 Số sản phẩm: " + (saved.getHoaDonChiTiets() != null ? saved.getHoaDonChiTiets().size() : 0));
        System.out.println("======================================");

        return saved;
    }
}




