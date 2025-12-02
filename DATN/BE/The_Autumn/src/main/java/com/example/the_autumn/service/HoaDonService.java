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

    public PageHoaDonRequest<HoaDonRespone> getAll(Pageable pageable) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "ngayTao")
        );

        Page<HoaDon> page = hoaDonRepository.findAll(sortedPageable);

        List<HoaDonRespone> dtoList = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        return new PageHoaDonRequest<>(
                dtoList,
                page.getTotalPages(),
                page.getTotalElements(),
                page.getNumber(),
                page.getSize(),
                page.isFirst(),
                page.isLast()
        );
    }

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

            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText.toLowerCase().trim() + "%";
                Predicate maPredicate = cb.like(cb.lower(root.get("maHoaDon")), searchPattern);
                Predicate tenKhPredicate = cb.like(cb.lower(root.get("khachHang").get("hoTen")), searchPattern);
                Predicate tenNvPredicate = cb.like(cb.lower(root.get("nhanVien").get("hoTen")), searchPattern);

                predicates.add(cb.or(maPredicate, tenKhPredicate, tenNvPredicate));
            }

            if (loaiHoaDon != null && !loaiHoaDon.isEmpty()) {
                predicates.add(root.get("loaiHoaDon").in(loaiHoaDon));
            }

            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }
            if (ngayTao != null) {
                LocalDateTime startOfDay = ngayTao.atStartOfDay();
                LocalDateTime endOfDay = ngayTao.atTime(23, 59, 59);
                predicates.add(cb.between(root.get("ngayTao"), startOfDay, endOfDay));
            }

            if (hinhThucThanhToan != null && !hinhThucThanhToan.trim().isEmpty()) {
                predicates.add(cb.like(
                        cb.lower(root.get("hinhThucThanhToans").get("phuongThucThanhToan").get("tenPhuongThucThanhToan")),
                        "%" + hinhThucThanhToan.toLowerCase().trim() + "%"
                ));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<HoaDon> pageResult = hoaDonRepository.findAll(spec, pageable);

        List<HoaDonRespone> dtoList = pageResult.getContent().stream()
                .map(HoaDonRespone::new)
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

        dto.setId(hoaDon.getId());
        dto.setMaHoaDon(hoaDon.getMaHoaDon());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgayThanhToan(hoaDon.getNgayThanhToan());

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

        if (hoaDon.getNhanVien() != null) {
            dto.setMaNhanVien(hoaDon.getNhanVien().getMaNhanVien());
            dto.setTenNhanVien(hoaDon.getNhanVien().getHoTen());
            dto.setSdtNhanVien(hoaDon.getNhanVien().getSdt());
        }

        if (hoaDon.getPhieuGiamGia() != null) {
            PhieuGiamGia phieuGiamGia = hoaDon.getPhieuGiamGia();
            dto.setMaGiamGia(phieuGiamGia.getMaGiamGia());
            dto.setTenChuongTrinh(phieuGiamGia.getTenChuongTrinh());

            dto.setGiaTriGiamGia(phieuGiamGia.getGiaTriGiamGia());
            dto.setMucGiaGiamToiDa(phieuGiamGia.getMucGiaGiamToiDa());
            dto.setGiaTriDonHangToiThieu(phieuGiamGia.getGiaTriDonHangToiThieu());

            dto.setLoaiGiamGia(phieuGiamGia.getLoaiGiamGia());
            dto.setSoLuongDung(phieuGiamGia.getSoLuongDung());
            dto.setNgayBatDau(phieuGiamGia.getNgayBatDau());
            dto.setNgayKetThuc(phieuGiamGia.getNgayKetThuc());
            dto.setTrangThaiPhieuGiamGia(phieuGiamGia.getTrangThai());
        }

        if (hoaDon.getLichSuThanhToans() != null && !hoaDon.getLichSuThanhToans().isEmpty()) {
            LichSuThanhToan lichSu = hoaDon.getLichSuThanhToans().get(0);
            dto.setMaGiaoDich(lichSu.getMaGiaoDich());
            dto.setSoTien(lichSu.getSoTien());
            dto.setGhiChuThanhToan(lichSu.getGhiChu());
        }

        if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
            HinhThucThanhToan hinhThuc = hoaDon.getHinhThucThanhToans().get(0);
            if (hinhThuc.getPhuongThucThanhToan() != null) {
                dto.setHinhThucThanhToan(hinhThuc.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
            } else {
                dto.setHinhThucThanhToan("Không xác định");
            }
        } else {
            dto.setHinhThucThanhToan("Chưa thanh toán");
        }

        if (hoaDon.getNhanVien() != null) {
            dto.setIdNhanVien(hoaDon.getNhanVien().getId());
            dto.setTenNhanVien(hoaDon.getNhanVien().getHoTen());
            dto.setSdtNhanVien(hoaDon.getNhanVien().getSdt());
        }

        if (hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()) {
            HinhThucThanhToan hinhThuc = hoaDon.getHinhThucThanhToans().get(0);
            if (hinhThuc.getPhuongThucThanhToan() != null) {
                dto.setIdPhuongThucThanhToan(hinhThuc.getPhuongThucThanhToan().getId());
                dto.setHinhThucThanhToan(hinhThuc.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
            }
        }

        dto.setLoaiHoaDon(hoaDon.getLoaiHoaDon());
        dto.setPhiVanChuyen(hoaDon.getPhiVanChuyen());
        dto.setTongTien(hoaDon.getTongTien());
        dto.setTongTienSauGiam(hoaDon.getTongTienSauGiam());
        dto.setTrangThai(hoaDon.getTrangThai());
        dto.setGhiChu(hoaDon.getGhiChu());

        List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDon_Id(id);
        List<HoaDonDetailResponse.ChiTietSanPhamResponse> chiTietDTOs = chiTietList.stream()
                .map(ct -> {
                    HoaDonDetailResponse.ChiTietSanPhamResponse ctDTO = new HoaDonDetailResponse.ChiTietSanPhamResponse();
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


    @Transactional
    public UpdateHoaDonResponse updateHoaDon(Integer id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID " + id));

        // ==================== XỬ LÝ KHI HỦY ĐƠN HÀNG ====================
        boolean isCancellingOrder = request.getTrangThai() != null && request.getTrangThai().equals(4)
                && !hoaDon.getTrangThai().equals(4);

        if (isCancellingOrder) {
            handleOrderCancellation(hoaDon, false); // false = KHÔNG hoàn tiền tự động
        }

        // ==================== CẬP NHẬT LOẠI HÓA ĐƠN ====================
        if (request.getLoaiHoaDon() != null && !request.getLoaiHoaDon().equals(hoaDon.getLoaiHoaDon())) {
            Boolean oldLoai = hoaDon.getLoaiHoaDon();
            hoaDon.setLoaiHoaDon(request.getLoaiHoaDon());
            luuLichSu(hoaDon, "Cập nhật loại hóa đơn",
                    String.format("Loại hóa đơn: '%s' → '%s'",
                            oldLoai != null && oldLoai ? "Tại quầy" : "Online",
                            request.getLoaiHoaDon() ? "Tại quầy" : "Online"), null);
        }

        // ==================== CẬP NHẬT THÔNG TIN KHÁCH HÀNG ====================
        if (hoaDon.getKhachHang() != null) {
            KhachHang kh = hoaDon.getKhachHang();
            StringBuilder logKH = new StringBuilder();
            boolean coThayDoi = false;

            if (request.getHoTenKhachHang() != null && !request.getHoTenKhachHang().equals(kh.getHoTen())) {
                logKH.append(String.format("Tên: '%s' → '%s'. ", kh.getHoTen(), request.getHoTenKhachHang()));
                kh.setHoTen(request.getHoTenKhachHang());
                coThayDoi = true;
            }
            if (request.getSdtKhachHang() != null && !request.getSdtKhachHang().equals(kh.getSdt())) {
                logKH.append(String.format("SĐT: '%s' → '%s'. ", kh.getSdt(), request.getSdtKhachHang()));
                kh.setSdt(request.getSdtKhachHang());
                coThayDoi = true;
            }
            if (request.getEmailKhachHang() != null && !request.getEmailKhachHang().equals(kh.getEmail())) {
                logKH.append(String.format("Email: '%s' → '%s'. ", kh.getEmail() != null ? kh.getEmail() : "N/A", request.getEmailKhachHang()));
                kh.setEmail(request.getEmailKhachHang());
                coThayDoi = true;
            }
            if (coThayDoi) {
                khachHangRepository.save(kh);
                luuLichSu(hoaDon, "Cập nhật thông tin khách hàng", logKH.toString().trim(), null);
            }
        }

        // ==================== CẬP NHẬT ĐỊA CHỈ GIAO HÀNG ====================
        if (hoaDon.getKhachHang() != null) {
            KhachHang kh = hoaDon.getKhachHang();

            Integer idDiaChiChon = request.getIdDiaChi();
            String diaChiCuTheMoi = request.getDiaChiCuThe() != null ? request.getDiaChiCuThe().trim() : null;
            Integer idTinhMoi = request.getThanhPho();
            Integer idQuanMoi = request.getQuan();

            boolean coThayDoiDiaChi = (idDiaChiChon != null) ||
                    (diaChiCuTheMoi != null && !diaChiCuTheMoi.isEmpty()) ||
                    idTinhMoi != null || idQuanMoi != null;

            if (coThayDoiDiaChi) {
                StringBuilder fullAddress = new StringBuilder();
                DiaChi diaChiSeLamMacDinh = null;
                Integer idDiaChiMacDinhMoi = null;

                // CASE 1: Người dùng CHỌN 1 địa chỉ có sẵn trong danh sách
                if (idDiaChiChon != null) {
                    diaChiSeLamMacDinh = diaChiRepository.findById(idDiaChiChon)
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ ID: " + idDiaChiChon));

                    if (!diaChiSeLamMacDinh.getKhachHang().getId().equals(kh.getId())) {
                        throw new RuntimeException("Không được chọn địa chỉ của khách hàng khác!");
                    }

                    idDiaChiMacDinhMoi = idDiaChiChon;

                    fullAddress.append(diaChiSeLamMacDinh.getDiaChiCuThe());
                    if (diaChiSeLamMacDinh.getQuanHuyen() != null)
                        fullAddress.append(", ").append(diaChiSeLamMacDinh.getQuanHuyen().getTenQuan());
                    if (diaChiSeLamMacDinh.getTinhThanh() != null)
                        fullAddress.append(", ").append(diaChiSeLamMacDinh.getTinhThanh().getTenTinh());

                    diaChiSeLamMacDinh.setDiaChiCuThe(diaChiCuTheMoi != null ? diaChiCuTheMoi : diaChiSeLamMacDinh.getDiaChiCuThe());
                    if (idTinhMoi != null) diaChiSeLamMacDinh.setTinhThanh(tinhThanhRepository.getById(idTinhMoi));
                    if (idQuanMoi != null) diaChiSeLamMacDinh.setQuanHuyen(quanHuyenRepository.getById(idQuanMoi));
                    diaChiRepository.save(diaChiSeLamMacDinh);

                } else {
                    // CASE 2: Người dùng NHẬP ĐỊA CHỈ MỚI hoàn toàn
                    if (diaChiCuTheMoi != null && !diaChiCuTheMoi.isEmpty()) fullAddress.append(diaChiCuTheMoi);
                    if (idQuanMoi != null) {
                        QuanHuyen quan = quanHuyenRepository.findById(idQuanMoi)
                                .orElseThrow(() -> new RuntimeException("Quận/huyện không tồn tại"));
                        if (fullAddress.length() > 0) fullAddress.append(", ");
                        fullAddress.append(quan.getTenQuan());
                    }
                    if (idTinhMoi != null) {
                        TinhThanh tinh = tinhThanhRepository.findById(idTinhMoi)
                                .orElseThrow(() -> new RuntimeException("Tỉnh/thành không tồn tại"));
                        if (fullAddress.length() > 0) fullAddress.append(", ");
                        fullAddress.append(tinh.getTenTinh());
                    }

                    diaChiSeLamMacDinh = new DiaChi();
                    diaChiSeLamMacDinh.setKhachHang(kh);
                    diaChiSeLamMacDinh.setDiaChiCuThe(diaChiCuTheMoi);
                    if (idTinhMoi != null) diaChiSeLamMacDinh.setTinhThanh(tinhThanhRepository.getById(idTinhMoi));
                    if (idQuanMoi != null) diaChiSeLamMacDinh.setQuanHuyen(quanHuyenRepository.getById(idQuanMoi));
                    diaChiSeLamMacDinh.setTenDiaChi("Địa chỉ từ hóa đơn #" + hoaDon.getMaHoaDon());
                    diaChiSeLamMacDinh.setTrangThai(true);

                    diaChiSeLamMacDinh = diaChiRepository.save(diaChiSeLamMacDinh);
                    idDiaChiMacDinhMoi = diaChiSeLamMacDinh.getId();
                }

                String newAddress = fullAddress.toString().trim();
                String oldAddress = hoaDon.getDiaChiKhachHang();

                if (!Objects.equals(newAddress, oldAddress)) {
                    hoaDon.setDiaChiKhachHang(newAddress);

                    diaChiRepository.disableAllExcept(kh.getId(), idDiaChiMacDinhMoi);

                    diaChiSeLamMacDinh.setTrangThai(true);
                    diaChiRepository.save(diaChiSeLamMacDinh);

                    luuLichSu(hoaDon, "Cập nhật địa chỉ giao hàng",
                            "Địa chỉ: '" + (oldAddress != null ? oldAddress : "(Trống)") + "' → '" + newAddress + "'", null);
                }
            }
        }

        // ==================== CẬP NHẬT GHI CHÚ ====================
        if (request.getGhiChu() != null && !request.getGhiChu().equals(hoaDon.getGhiChu())) {
            String old = hoaDon.getGhiChu();
            hoaDon.setGhiChu(request.getGhiChu());
            luuLichSu(hoaDon, "Cập nhật ghi chú",
                    String.format("Ghi chú: '%s' → '%s'", old != null ? old : "(Trống)", request.getGhiChu()), null);
        }

        // ==================== CẬP NHẬT TRẠNG THÁI ====================
        boolean hasStatusChange = false;
        if (request.getTrangThai() != null && !request.getTrangThai().equals(hoaDon.getTrangThai())) {
            Integer oldStatus = hoaDon.getTrangThai();
            Integer newStatus = request.getTrangThai();

            hoaDon.setTrangThai(newStatus);

            // CHỈ TRỪ KHO KHI CHUYỂN TỪ "Chờ xác nhận (0) SANG Chờ giao hàng (1) HOẶC CAO HƠN
            boolean isConfirmingOrder = (oldStatus == 0) && (newStatus == 1 || newStatus == 2 || newStatus == 3);

            if (isConfirmingOrder) {
                System.out.println("Xác nhận đơn hàng - Bắt đầu trừ tồn kho cho hóa đơn #" + hoaDon.getMaHoaDon());

                List<HoaDonChiTiet> chiTietList = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());

                for (HoaDonChiTiet ct : chiTietList) {
                    ChiTietSanPham ctsp = ct.getChiTietSanPham();
                    int soLuongCanTru = ct.getSoLuong();

                    if (ctsp.getSoLuongTon() < soLuongCanTru) {
                        throw new RuntimeException("Không đủ tồn kho để xác nhận đơn hàng cho sản phẩm: " +
                                ctsp.getSanPham().getTenSanPham() + " (Còn: " + ctsp.getSoLuongTon() + ")");
                    }

                    ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuongCanTru);
                    chiTietSanPhamRepository.save(ctsp);

                    System.out.println("Đã trừ tồn kho: " + ctsp.getSanPham().getTenSanPham() +
                            " - SL: " + soLuongCanTru + " - Tồn mới: " + ctsp.getSoLuongTon());
                }

                luuLichSu(hoaDon, "Xác nhận đơn hàng - Trừ tồn kho",
                        "Đã trừ tồn kho cho tất cả sản phẩm trong đơn hàng khi xác nhận giao hàng", null);
            }

            luuLichSu(hoaDon, "Cập nhật trạng thái đơn hàng",
                    String.format("Trạng thái: '%s' → '%s'",
                            getTrangThaiText(oldStatus),
                            getTrangThaiText(newStatus)), null);

            hasStatusChange = true;
        }

        // ==================== CẬP NHẬT NHÂN VIÊN ====================
        if (request.getIdNhanVien() != null) {
            NhanVien nv = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + request.getIdNhanVien()));

            if (hoaDon.getNhanVien() == null || !hoaDon.getNhanVien().getId().equals(nv.getId())) {
                String old = hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getHoTen() : "N/A";
                hoaDon.setNhanVien(nv);
                luuLichSu(hoaDon, "Cập nhật nhân viên", String.format("Nhân viên: '%s' → '%s'", old, nv.getHoTen()), null);
            }
        }

        // ==================== CẬP NHẬT PHƯƠNG THỨC THANH TOÁN ====================
        if (request.getIdPhuongThucThanhToan() != null) {
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

            String oldPTTT = hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()
                    ? hoaDon.getHinhThucThanhToans().get(0).getPhuongThucThanhToan().getTenPhuongThucThanhToan()
                    : "Chưa có";

            boolean needUpdate = hoaDon.getHinhThucThanhToans() == null ||
                    hoaDon.getHinhThucThanhToans().isEmpty() ||
                    !hoaDon.getHinhThucThanhToans().get(0).getPhuongThucThanhToan().getId().equals(pttt.getId());

            if (needUpdate) {
                if (hoaDon.getHinhThucThanhToans() != null) hoaDon.getHinhThucThanhToans().clear();

                HinhThucThanhToan ht = new HinhThucThanhToan();
                ht.setHoaDon(hoaDon);
                ht.setPhuongThucThanhToan(pttt);
                ht.setTrangThai(true);
                if (hoaDon.getHinhThucThanhToans() == null) hoaDon.setHinhThucThanhToans(new ArrayList<>());
                hoaDon.getHinhThucThanhToans().add(ht);

                luuLichSu(hoaDon, "Cập nhật phương thức thanh toán",
                        String.format("Phương thức thanh toán: '%s' → '%s'", oldPTTT, pttt.getTenPhuongThucThanhToan()), null);
            }
        }

        hoaDon.setNgaySua(new Date());

        // ==================== XỬ LÝ CẬP NHẬT SẢN PHẨM ====================
        boolean hasProductChanges = false;
        if (request.getChiTietSanPhams() != null && !request.getChiTietSanPhams().isEmpty()) {
            System.out.println("🔄 Bắt đầu xử lý cập nhật sản phẩm...");

            List<HoaDonChiTiet> chiTietHienTai = hoaDonChiTietRepository.findByHoaDonId(id);
            System.out.println("📦 Số sản phẩm hiện tại: " + chiTietHienTai.size());

            Map<Integer, HoaDonChiTiet> chiTietMap = chiTietHienTai.stream()
                    .collect(Collectors.toMap(
                            ct -> ct.getChiTietSanPham().getId(),
                            Function.identity()
                    ));

            for (UpdateHoaDonRequest.ChiTietSanPhamRequest spReq : request.getChiTietSanPhams()) {
                if (spReq.getIdChiTietSanPham() == null) {
                    throw new RuntimeException("idChiTietSanPham không được null");
                }

                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(spReq.getIdChiTietSanPham())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ChiTietSanPham ID: " + spReq.getIdChiTietSanPham()));

                HoaDonChiTiet existingCT = chiTietMap.get(spReq.getIdChiTietSanPham());

                if (existingCT != null) {
                    int soLuongCu = existingCT.getSoLuong();
                    int soLuongMoi = spReq.getSoLuong();
                    int chechLech = soLuongMoi - soLuongCu;

                    if (chechLech != 0) {
                        if (chechLech > 0) {
                            if (ctsp.getSoLuongTon() < chechLech) {
                                throw new RuntimeException("Không đủ tồn kho cho sản phẩm: " + ctsp.getSanPham().getTenSanPham());
                            }
                            ctsp.setSoLuongTon(ctsp.getSoLuongTon() - chechLech);
                        } else {
                            ctsp.setSoLuongTon(ctsp.getSoLuongTon() + Math.abs(chechLech));
                        }
                        chiTietSanPhamRepository.save(ctsp);

                        existingCT.setSoLuong(soLuongMoi);
                        existingCT.setGiaBan(spReq.getGiaBan());
                        existingCT.setThanhTien(spReq.getGiaBan().multiply(BigDecimal.valueOf(soLuongMoi)));
                        existingCT.setGhiChu(spReq.getGhiChu());
                        hoaDonChiTietRepository.save(existingCT);

                        luuLichSu(hoaDon, "Cập nhật sản phẩm",
                                String.format("Sản phẩm: %s - Số lượng: %d → %d",
                                        ctsp.getSanPham().getTenSanPham(), soLuongCu, soLuongMoi), null);
                        hasProductChanges = true;
                    }
                } else {
                    if (ctsp.getSoLuongTon() < spReq.getSoLuong()) {
                        throw new RuntimeException("Không đủ tồn kho cho sản phẩm: " + ctsp.getSanPham().getTenSanPham());
                    }

                    System.out.println("➕ Thêm mới sản phẩm: " + ctsp.getSanPham().getTenSanPham());
                    ctsp.setSoLuongTon(ctsp.getSoLuongTon() - spReq.getSoLuong());
                    chiTietSanPhamRepository.save(ctsp);

                    HoaDonChiTiet newCT = new HoaDonChiTiet();
                    newCT.setHoaDon(hoaDon);
                    newCT.setChiTietSanPham(ctsp);
                    newCT.setSoLuong(spReq.getSoLuong());
                    newCT.setGiaBan(spReq.getGiaBan());
                    newCT.setThanhTien(spReq.getGiaBan().multiply(BigDecimal.valueOf(spReq.getSoLuong())));
                    newCT.setGhiChu(spReq.getGhiChu());
                    hoaDonChiTietRepository.save(newCT);

                    luuLichSu(hoaDon, "Thêm sản phẩm vào hóa đơn",
                            String.format("Sản phẩm: %s - Số lượng: %d",
                                    ctsp.getSanPham().getTenSanPham(), spReq.getSoLuong()), null);
                    hasProductChanges = true;
                }
            }

            Set<Integer> requestedProductIds = request.getChiTietSanPhams().stream()
                    .map(UpdateHoaDonRequest.ChiTietSanPhamRequest::getIdChiTietSanPham)
                    .collect(Collectors.toSet());

            for (HoaDonChiTiet existingCT : chiTietHienTai) {
                Integer existingProductId = existingCT.getChiTietSanPham().getId();
                if (!requestedProductIds.contains(existingProductId)) {
                    ChiTietSanPham ctsp = existingCT.getChiTietSanPham();

                    ctsp.setSoLuongTon(ctsp.getSoLuongTon() + existingCT.getSoLuong());
                    chiTietSanPhamRepository.save(ctsp);

                    hoaDonChiTietRepository.delete(existingCT);

                    luuLichSu(hoaDon, "Xóa sản phẩm khỏi hóa đơn",
                            String.format("Sản phẩm: %s - Số lượng: %d",
                                    ctsp.getSanPham().getTenSanPham(), existingCT.getSoLuong()), null);
                    hasProductChanges = true;
                }
            }

            System.out.println("✅ Hoàn thành cập nhật sản phẩm");
        }

        // ==================== TỰ ĐỘNG CẬP NHẬT GIẢM GIÁ ====================
        if (hasProductChanges) {
            autoUpdateGiamGiaWhenProductsChange(hoaDon, hasProductChanges);
        }

        // ==================== TÍNH TOÁN TỔNG TIỀN ====================
        BigDecimal tongTienSauGiam = calculateTongTienSauGiamGia(hoaDon);

        if (hasProductChanges || !tongTienSauGiam.equals(hoaDon.getTongTienSauGiam() != null ? hoaDon.getTongTienSauGiam() : BigDecimal.ZERO)) {
            BigDecimal oldTongTien = hoaDon.getTongTienSauGiam() != null ? hoaDon.getTongTienSauGiam() : BigDecimal.ZERO;
            hoaDon.setTongTienSauGiam(tongTienSauGiam);

            luuLichSu(hoaDon, "Cập nhật tổng tiền hóa đơn",
                    String.format("Tổng tiền sau giảm giá: %s → %s",
                            formatMoney(oldTongTien),
                            formatMoney(tongTienSauGiam)), null);
            System.out.println("💰 Đã cập nhật tổng tiền sau giảm giá");
        }

        // ==================== CẬP NHẬT LỊCH SỬ THANH TOÁN ====================
        if (hasStatusChange || hasProductChanges) {
            updatePaymentHistory(hoaDon, tongTienSauGiam, hasStatusChange);
        }

        hoaDonRepository.save(hoaDon);
        return new UpdateHoaDonResponse(true, "Cập nhật hóa đơn thành công");
    }

    private void handleOrderCancellation(HoaDon hoaDon, boolean autoHoanTien) {
        System.out.println("🔄 Bắt đầu xử lý hủy đơn hàng #" + hoaDon.getMaHoaDon());

        returnProductsToInventory(hoaDon);

        // 2. Cập nhật lịch sử thanh toán HIỆN CÓ (nếu có)
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

            // [QUAN TRỌNG] CHỈ cập nhật ghi chú, KHÔNG tạo bản ghi mới
            String ghiChuCuMoi = "Đơn hàng đã hủy (ngày " +
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ") - Chưa hoàn tiền";

            // [SỬA] Thêm điều kiện để không ghi đè nếu đã có ghi chú hoàn tiền
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


    private void returnProductsToInventory(HoaDon hoaDon) {
        List<HoaDonChiTiet> chiTietHoaDon = hoaDonChiTietRepository.findByHoaDonId(hoaDon.getId());

        if (chiTietHoaDon.isEmpty()) {
            System.out.println("⚠️ Hóa đơn không có sản phẩm để trả về tồn kho");
            return;
        }

        StringBuilder logMessage = new StringBuilder("Trả hàng về tồn kho: ");

        for (HoaDonChiTiet chiTiet : chiTietHoaDon) {
            ChiTietSanPham ctsp = chiTiet.getChiTietSanPham();
            int soLuongTra = chiTiet.getSoLuong();

            int tonKhoMoi = ctsp.getSoLuongTon() + soLuongTra;
            ctsp.setSoLuongTon(tonKhoMoi);
            chiTietSanPhamRepository.save(ctsp);

            logMessage.append(String.format("%s (+%d), ",
                    ctsp.getSanPham().getTenSanPham(), soLuongTra));

            System.out.println("📦 Trả tồn kho: " + ctsp.getSanPham().getTenSanPham() +
                    " - Số lượng: +" + soLuongTra +
                    " - Tồn kho mới: " + tonKhoMoi);
        }

        // Ghi log lịch sử
        luuLichSu(hoaDon, "Hủy đơn hàng - Trả hàng về tồn kho",
                logMessage.toString().replaceAll(", $", ""), null);
    }

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
                    lichSuThanhToan.setNgayThanhToan(new Date());
                }
            }

            BigDecimal tongTienTruocGiam = calculateTongTienTruocGiam(hoaDon);
            BigDecimal tienGiamGia = tongTienTruocGiam.subtract(tongTienSauGiam);

            String oldGhiChu = lichSuThanhToan.getGhiChu();
            String newGhiChu = "";

            Boolean loaiHoaDon = hoaDon.getLoaiHoaDon(); // true = tại quầy, false = online
            Integer trangThaiHoaDon = hoaDon.getTrangThai(); // trạng thái đơn hiện tại
            boolean isTienMat = phuongThuc.getTenPhuongThucThanhToan().toLowerCase().contains("tiền mặt");

            if (loaiHoaDon != null && loaiHoaDon) {
                // Tại quầy: giữ nguyên logic cũ
                switch (trangThaiHoaDon) {
                    case 0:
                        lichSuThanhToan.setTrangThai(false);
                        newGhiChu = "Chờ xác nhận thanh toán - Số tiền: " + formatMoney(tongTienSauGiam) +
                                (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                        break;
                    case 1:
                    case 2:
                    case 3:
                        lichSuThanhToan.setTrangThai(true);
                        newGhiChu = "Đã thanh toán - " + getTrangThaiText(trangThaiHoaDon) + " - Số tiền: " + formatMoney(tongTienSauGiam) +
                                (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                        break;
                    case 4:
                        lichSuThanhToan.setTrangThai(false);
                        newGhiChu = "Đơn hàng đã hủy - Số tiền: " + formatMoney(tongTienSauGiam) +
                                (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                        break;
                    default:
                        lichSuThanhToan.setTrangThai(false);
                        newGhiChu = "Chờ xử lý thanh toán - Số tiền: " + formatMoney(tongTienSauGiam) +
                                (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                }
            } else {
                boolean daThanhToan = lichSuThanhToan.getTrangThai();

                if (daThanhToan) {
                    lichSuThanhToan.setTrangThai(true);
                    newGhiChu = "Đã thanh toán - " + getTrangThaiText(trangThaiHoaDon) + " - Số tiền: " + formatMoney(tongTienSauGiam) +
                            (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                } else {

                    if (isTienMat) {
                        if (trangThaiHoaDon != null && trangThaiHoaDon == 3) {
                            lichSuThanhToan.setTrangThai(true);
                            newGhiChu = "Đã thanh toán (Tiền mặt) - Hoàn thành - Số tiền: " + formatMoney(tongTienSauGiam) +
                                    (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                        } else {
                            lichSuThanhToan.setTrangThai(false);
                            newGhiChu = "Chưa thanh toán (Tiền mặt) - Số tiền: " + formatMoney(tongTienSauGiam) +
                                    (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                        }
                    } else {
                        lichSuThanhToan.setTrangThai(false);
                        newGhiChu = "Chưa thanh toán - Số tiền: " + formatMoney(tongTienSauGiam) +
                                (tienGiamGia.compareTo(BigDecimal.ZERO) > 0 ? " (Đã giảm: " + formatMoney(tienGiamGia) + ")" : "");
                    }
                }
            }

            lichSuThanhToan.setGhiChu(newGhiChu);

            if (!newGhiChu.equals(oldGhiChu) || !daCoLichSuThanhToan) {
                lichSuThanhToanRepository.save(lichSuThanhToan);
                System.out.println("✅ Đã cập nhật lịch sử thanh toán");
            }

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi xử lý lịch sử thanh toán: " + e.getMessage());
            e.printStackTrace();
        }
    }



    private void updatePaymentHistoryToRefunded(HoaDon hoaDon) {
        try {
            List<LichSuThanhToan> lichSuThanhToanList = lichSuThanhToanRepository
                    .findByHoaDonIdOrderByNgayThanhToanDesc(hoaDon.getId());

            if (lichSuThanhToanList.isEmpty()) {
                System.out.println("⚠️ Không tìm thấy lịch sử thanh toán để hoàn tiền");
                return;
            }

            LichSuThanhToan lichSuMoiNhat = lichSuThanhToanList.get(0);

            // Tạo bản ghi hoàn tiền
            LichSuThanhToan lichSuHoanTien = new LichSuThanhToan();
            lichSuHoanTien.setHoaDon(hoaDon);
            lichSuHoanTien.setPhuongThucThanhToan(lichSuMoiNhat.getPhuongThucThanhToan());
            lichSuHoanTien.setSoTien(lichSuMoiNhat.getSoTien());
            lichSuHoanTien.setNgayThanhToan(new Date());
            lichSuHoanTien.setTrangThai(true);
            lichSuHoanTien.setGhiChu("[HOÀN TIỀN] Đã hoàn tiền do hủy đơn hàng - Số tiền: " +
                    formatMoney(lichSuMoiNhat.getSoTien()));

            lichSuThanhToanRepository.save(lichSuHoanTien);

            // Cập nhật ghi chú của lịch sử cũ
            String ghiChuCuMoi = lichSuMoiNhat.getGhiChu() + " (Đã hoàn tiền ngày " +
                    new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ")";
            lichSuMoiNhat.setGhiChu(ghiChuCuMoi);
            lichSuThanhToanRepository.save(lichSuMoiNhat);

            System.out.println("💰 Đã tạo bản ghi hoàn tiền tự động: " +
                    formatMoney(lichSuMoiNhat.getSoTien()));

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi tạo bản ghi hoàn tiền: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Không thể xử lý hoàn tiền tự động: " + e.getMessage());
        }
    }

    private BigDecimal calculateTongTienSauGiamGia(HoaDon hoaDon) {
        try {
            BigDecimal tongTienSanPham = BigDecimal.ZERO;
            if (hoaDon.getHoaDonChiTiets() != null && !hoaDon.getHoaDonChiTiets().isEmpty()) {
                tongTienSanPham = hoaDon.getHoaDonChiTiets().stream()
                        .map(ct -> ct.getThanhTien() != null ? ct.getThanhTien() : BigDecimal.ZERO)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            BigDecimal phiVanChuyen = hoaDon.getPhiVanChuyen() != null ? hoaDon.getPhiVanChuyen() : BigDecimal.ZERO;
            BigDecimal tongTienTruocGiam = tongTienSanPham.add(phiVanChuyen);

            BigDecimal tienGiamGia = calculateTienGiamGia(hoaDon, tongTienTruocGiam);
            BigDecimal tongTienSauGiam = tongTienTruocGiam.subtract(tienGiamGia);

            return tongTienSauGiam.compareTo(BigDecimal.ZERO) > 0 ? tongTienSauGiam : BigDecimal.ZERO;
        } catch (Exception e) {
            System.err.println("Lỗi tính tổng tiền sau giảm giá: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }

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

    private void autoUpdateGiamGiaWhenProductsChange(HoaDon hoaDon, boolean hasProductChanges) {
        if (hasProductChanges && hoaDon.getPhieuGiamGia() != null) {
            System.out.println("🔄 Tự động kiểm tra lại điều kiện phiếu giảm giá sau khi thay đổi sản phẩm");

            BigDecimal tongTienTruocGiam = calculateTongTienTruocGiam(hoaDon);
            BigDecimal tienGiamGiaMoi = calculateTienGiamGia(hoaDon, tongTienTruocGiam);
            BigDecimal tongTienSauGiamMoi = tongTienTruocGiam.subtract(tienGiamGiaMoi);

            BigDecimal tongTienSauGiamHienTai = hoaDon.getTongTienSauGiam() != null ?
                    hoaDon.getTongTienSauGiam() : BigDecimal.ZERO;

            if (!tongTienSauGiamMoi.equals(tongTienSauGiamHienTai)) {
                System.out.println("🔄 Cập nhật tự động tổng tiền sau giảm giá:");
                System.out.println("   - Từ: " + formatMoney(tongTienSauGiamHienTai));
                System.out.println("   - Thành: " + formatMoney(tongTienSauGiamMoi));

                hoaDon.setTongTienSauGiam(tongTienSauGiamMoi);

                luuLichSu(hoaDon, "Tự động cập nhật giảm giá",
                        String.format("Tổng tiền sau giảm giá: %s → %s (do thay đổi sản phẩm)",
                                formatMoney(tongTienSauGiamHienTai),
                                formatMoney(tongTienSauGiamMoi)), null);
            }
        }
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

        if (req.getNgayTao() != null) {
            hoaDon.setNgayTao(req.getNgayTao());
        } else {
            hoaDon.setNgayTao(new Date());
        }

        if (req.getNgayThanhToan() != null) {
            hoaDon.setNgayThanhToan(req.getNgayThanhToan());
        } else {
            if (req.getTrangThai() == null || req.getTrangThai() != 0) {
                hoaDon.setNgayThanhToan(new Date());
            }
        }

        hoaDon.setNguoiTao(req.getNguoiTao() != null ? req.getNguoiTao() : 1);

        KhachHang khachHang = null;
        if (req.getIdKhachHang() != null) {
            khachHang = khachHangRepository.findById(req.getIdKhachHang())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + req.getIdKhachHang()));
            hoaDon.setKhachHang(khachHang);

            System.out.println("=== DEBUG THÊM ĐỊA CHỈ BẮT ĐẦU ===");
            System.out.println("Khách hàng: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");

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
            if (req.getHoTen() != null && !req.getHoTen().isEmpty() &&
                    req.getSdt() != null && !req.getSdt().isEmpty()) {

                try {
                    Optional<KhachHang> existingCustomer = khachHangRepository.findBySdt(req.getSdt());

                    if (existingCustomer.isPresent()) {
                        khachHang = existingCustomer.get();
                        System.out.println("✅ Sử dụng khách hàng đã tồn tại: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");
                    } else {
                        KhachHang newKhachHang = new KhachHang();
                        newKhachHang.setHoTen(req.getHoTen());
                        newKhachHang.setSdt(req.getSdt());
                        newKhachHang.setGioiTinh(true);
                        newKhachHang.setNgaySinh(new Date());
                        newKhachHang.setTrangThai(true);
                        newKhachHang.setNgayTao(new Date());

                        khachHang = khachHangRepository.save(newKhachHang);
                        System.out.println("🎉 ĐÃ TẠO KHÁCH HÀNG MỚI: " + khachHang.getHoTen() + " (ID: " + khachHang.getId() + ")");

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

        NhanVien nhanVien = nhanVienRepository.findById(
                req.getIdNhanVien() != null ? req.getIdNhanVien() : 1
        ).orElse(null);
        hoaDon.setNhanVien(nhanVien);

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

        hoaDon.setLoaiHoaDon(req.getLoaiHoaDon() != null ? req.getLoaiHoaDon() : false);


        hoaDon.setPhiVanChuyen(req.getPhiVanChuyen() != null ? req.getPhiVanChuyen() : BigDecimal.ZERO);
        hoaDon.setTongTien(req.getTongTien() != null ? req.getTongTien() : BigDecimal.ZERO);
        hoaDon.setTongTienSauGiam(req.getTongTienSauGiam() != null ? req.getTongTienSauGiam() : BigDecimal.ZERO);
        hoaDon.setDiaChiKhachHang(req.getDiaChiKhachHang());
        hoaDon.setGhiChu(req.getGhiChu());
        hoaDon.setNgayThanhToan(new Date());

        Integer trangThai = req.getTrangThai();
        if (trangThai == null) {
            throw new RuntimeException("Trạng thái hóa đơn không được để trống");
        }
        hoaDon.setTrangThai(trangThai);

        if (req.getChiTietList() == null || req.getChiTietList().isEmpty()) {
            throw new RuntimeException("Không có chi tiết sản phẩm trong hóa đơn");
        }

        List<HoaDonChiTiet> listCT = new ArrayList<>();

        for (HoaDonChiTietRequest ctReq : req.getChiTietList()) {
            ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(ctReq.getIdChiTietSanPham())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết sản phẩm"));

            // if (ctsp.getSoLuongTon() < ctReq.getSoLuong()) {
            //     throw new RuntimeException("Không đủ tồn kho");
            // }

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

            // ctsp.setSoLuongTon(ctsp.getSoLuongTon() - ctReq.getSoLuong());
            // chiTietSanPhamRepository.save(ctsp);
        }

        hoaDon.setHoaDonChiTiets(listCT);
        HoaDon saved = hoaDonRepository.save(hoaDon);

        LichSuHoaDon log = new LichSuHoaDon();
        log.setHoaDon(saved);
        log.setKhachHang(khachHang);
        log.setNhanVien(nhanVien);
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
            ls.setGhiChu(req.getGhiChuThanhToan());
            if (trangThai != 0) {
                ls.setNgayThanhToan(new Date());
            }
            ls.setTrangThai(true);
            lichSuThanhToanRepository.save(ls);

            boolean loaiThanhToan =
                    pt.getTenPhuongThucThanhToan().toLowerCase().contains("chuyển") ||
                            pt.getTenPhuongThucThanhToan().toLowerCase().contains("online");

            HinhThucThanhToan hinhThuc = new HinhThucThanhToan();
            hinhThuc.setHoaDon(saved);
            hinhThuc.setPhuongThucThanhToan(pt);
            hinhThuc.setLoaiThanhToan(loaiThanhToan);
            hinhThuc.setTrangThai(true);
            hinhThucThanhToanRepository.save(hinhThuc);
        }

        System.out.println("🎉 HOÀN TẤT TẠO HÓA ĐƠN - ID: " + saved.getId() +
                ", Trạng thái: " + saved.getTrangThai() +
                ", Loại: " + saved.getLoaiHoaDon() +
                ", Khách hàng: " + (khachHang != null ? khachHang.getHoTen() : "Khách lẻ"));
        return saved;
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

        // Lấy thông tin sản phẩm để trả lại tồn kho
        ChiTietSanPham ctsp = chiTietToDelete.getChiTietSanPham();
        int soLuongXoa = chiTietToDelete.getSoLuong();

        // Trả lại tồn kho
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() + soLuongXoa);
        chiTietSanPhamRepository.save(ctsp);

        // Xóa chi tiết hóa đơn
        hoaDonChiTietRepository.delete(chiTietToDelete);

        capNhatTongTienHoaDon(hoaDon);

        // Ghi log lịch sử
        luuLichSu(hoaDon, "Xóa sản phẩm khỏi hóa đơn",
                String.format("Đã xóa sản phẩm: %s - Số lượng: %d - Trả lại tồn kho: %d",
                        ctsp.getSanPham().getTenSanPham(), soLuongXoa, soLuongXoa), null);

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

            // ⭐ SỬA LỖI QUAN TRỌNG: truyền đúng tham số
            validateHoanTienConditions(hoaDon, lichSu);

            // 3. Lấy thanh toán gần nhất
            LichSuThanhToan thanhToanGanNhat = lichSu.get(0);

            // 4. Tạo bản ghi hoàn tiền
            LichSuThanhToan lichSuHoanTien = createLichSuHoanTien(
                    hoaDon,
                    thanhToanGanNhat,
                    request
            );

            // 5. Cập nhật trạng thái hóa đơn
            updateTrangThaiHoaDonKhiHoanTien(hoaDon, request);

            // 6. Trả hàng về kho khi hủy
            if (request.getLyDoHoanTien() != null &&
                    request.getLyDoHoanTien().toLowerCase().contains("hủy")) {
                returnProductsToInventory(hoaDon);
            }

            // 7. Lưu lịch sử chi tiết
            luuLichSuHoanTien(hoaDon, lichSuHoanTien, request);

            // 8. Gửi mail nếu có
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

    public void validateHoanTienConditions(HoaDon hoaDon, List<LichSuThanhToan> lichSuThanhToan) {

        boolean loaiHoaDonTaiQuay = Boolean.TRUE.equals(hoaDon.getLoaiHoaDon()); // true = tại quầy
        boolean loaiHoaDonOnline = Boolean.FALSE.equals(hoaDon.getLoaiHoaDon()); // false = online

        // Lấy lịch sử thanh toán gần nhất
        LichSuThanhToan lsMoiNhat =
                lichSuThanhToan.isEmpty() ? null : lichSuThanhToan.get(0);

        PhuongThucThanhToan pt = lsMoiNhat != null ? lsMoiNhat.getPhuongThucThanhToan() : null;
        String maPT = pt != null ? pt.getMaPhuongThucThanhToan() : null;

        // ========== 1️⃣ KIỂM TRA LOẠI HÓA ĐƠN = TẠI QUẦY ==========
        if (loaiHoaDonTaiQuay) {

            // Với hóa đơn tại quầy: chỉ cần trạng thái là đã hủy là được hoàn
            if (hoaDon.getTrangThai() == null || hoaDon.getTrangThai() != 4) {
                throw new RuntimeException("Chỉ có thể hoàn tiền cho hóa đơn tại quầy đã hủy.");
            }

            // Không kiểm tra đã thanh toán hay chưa
            // Không kiểm tra phương thức thanh toán
            // → return để bỏ qua kiểm tra online
            return;
        }

        // ========== 2️⃣ KIỂM TRA HOÁ ĐƠN ONLINE ==========
        if (loaiHoaDonOnline) {

            if (hoaDon.getTrangThai() != 4) {
                throw new RuntimeException("Chỉ thể hoàn tiền cho hóa đơn online đã hủy.");
            }

            // Nếu chưa có thanh toán → đơn COD chưa thanh toán → không hoàn tiền
            if (lsMoiNhat == null || lsMoiNhat.getSoTien().compareTo(BigDecimal.ZERO) == 0) {
                throw new RuntimeException("Đơn hàng chưa được thanh toán, không thể hoàn tiền.");
            }

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

        BigDecimal tongTien = hoaDon.getTongTienSauGiam() != null
                ? hoaDon.getTongTienSauGiam()
                : BigDecimal.ZERO;

        if (tongTienDaHoan.compareTo(tongTien) >= 0) {
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


    private LichSuThanhToan createLichSuHoanTien(
            HoaDon hoaDon,
            LichSuThanhToan lichSuCu,
            HoanTienRequest request
    ) {
        LichSuThanhToan lichSuHoanTien = new LichSuThanhToan();
        lichSuHoanTien.setHoaDon(hoaDon);
        lichSuHoanTien.setPhuongThucThanhToan(lichSuCu.getPhuongThucThanhToan());

        // Tính số tiền hoàn (có thể hoàn toàn bộ hoặc 1 phần)
        BigDecimal soTienHoan = request.getSoTienHoan() != null
                ? request.getSoTienHoan().min(lichSuCu.getSoTien())
                : lichSuCu.getSoTien();

        lichSuHoanTien.setSoTien(soTienHoan);
        lichSuHoanTien.setNgayThanhToan(new Date());
        lichSuHoanTien.setTrangThai(true);

        // Tạo ghi chú chi tiết
        String ghiChu = String.format(
                "[HOÀN TIỀN] %s - Số tiền: %s - Phương thức: %s%s",
                request.getLyDoHoanTien() != null ? request.getLyDoHoanTien() : "Hoàn tiền đơn hàng",
                formatMoney(soTienHoan),
                lichSuCu.getPhuongThucThanhToan().getTenPhuongThucThanhToan(),
                request.getGhiChuBoSung() != null ? " - " + request.getGhiChuBoSung() : ""
        );

        lichSuHoanTien.setGhiChu(ghiChu);

        // Cập nhật ghi chú của lịch sử cũ
        String ghiChuCuMoi = lichSuCu.getGhiChu() + " (Đã hoàn tiền ngày " + new SimpleDateFormat("dd/MM/yyyy").format(new Date()) + ")";
        lichSuCu.setGhiChu(ghiChuCuMoi);
        lichSuThanhToanRepository.save(lichSuCu);

        return lichSuThanhToanRepository.save(lichSuHoanTien);
    }

    private void updateTrangThaiHoaDonKhiHoanTien(HoaDon hoaDon, HoanTienRequest request) {
        // Chỉ cập nhật trạng thái nếu là hủy đơn
        if (request.getLyDoHoanTien() != null &&
                request.getLyDoHoanTien().toLowerCase().contains("hủy")) {
            hoaDon.setTrangThai(4); // Trạng thái đã hủy
            hoaDonRepository.save(hoaDon);
        }
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

}




