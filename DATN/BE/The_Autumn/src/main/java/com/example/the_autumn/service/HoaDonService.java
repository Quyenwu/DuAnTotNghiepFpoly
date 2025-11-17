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
import com.example.the_autumn.model.request.HoaDonChiTietRequest;
import com.example.the_autumn.model.request.HoaDonRequest;
import com.example.the_autumn.model.request.PageHoaDonRequest;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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
        Page<HoaDon> page = hoaDonRepository.findAll(pageable);

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

        // Set độ rộng cột (tổng = 12)
        float[] columnWidths = {0.6f, 1.2f, 1.8f, 1.5f, 1.3f, 1f, 1.3f, 1.2f, 1.5f};
        table.setWidths(columnWidths);

        // Header bảng
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
            String searchText,  // ⭐ THAY: gộp 3 tham số thành 1
            List<Boolean> loaiHoaDon,
            Integer trangThai,
            LocalDate ngayTao,
            String hinhThucThanhToan,  // ⭐ THÊM
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<HoaDon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // ⭐ TÌM KIẾM TRÊN 3 TRƯỜNG (mã HĐ, tên khách hàng, tên nhân viên)
            if (searchText != null && !searchText.trim().isEmpty()) {
                String searchPattern = "%" + searchText.toLowerCase().trim() + "%";
                Predicate maPredicate = cb.like(cb.lower(root.get("maHoaDon")), searchPattern);
                Predicate tenKhPredicate = cb.like(cb.lower(root.get("khachHang").get("hoTen")), searchPattern);
                Predicate tenNvPredicate = cb.like(cb.lower(root.get("nhanVien").get("hoTen")), searchPattern);

                // OR: tìm trên bất kỳ trường nào
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

            // ⭐ THÊM: Lọc theo hình thức thanh toán
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
            dto.setMaGiamGia(hoaDon.getPhieuGiamGia().getMaGiamGia());
            dto.setTenChuongTrinh(hoaDon.getPhieuGiamGia().getTenChuongTrinh());
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
                    ctDTO.setGiaBan(ct.getGiaBan());
                    ctDTO.setThanhTien(ct.getThanhTien());
                    ctDTO.setGhiChu(ct.getGhiChu());


                    if (ct.getChiTietSanPham() != null) {
                        ChiTietSanPham ctsp = ct.getChiTietSanPham();
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
                                    .map(anh -> anh.getDuongDanAnh()) // lấy đường dẫn ảnh
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

        // ==================== CẬP NHẬT THÔNG TIN CƠ BẢN ====================
        if (request.getLoaiHoaDon() != null && !request.getLoaiHoaDon().equals(hoaDon.getLoaiHoaDon())) {
            Boolean oldLoai = hoaDon.getLoaiHoaDon();
            hoaDon.setLoaiHoaDon(request.getLoaiHoaDon());
            luuLichSu(hoaDon, "Cập nhật loại hóa đơn",
                    String.format("Loại hóa đơn: '%s' → '%s'",
                            oldLoai != null && oldLoai ? "Tại quầy" : "Online",
                            request.getLoaiHoaDon() ? "Tại quầy" : "Online"), null);
        }

        // Cập nhật thông tin khách hàng (nếu có)
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
        if (request.getDiaChiCuThe() != null || request.getThanhPho() != null || request.getQuan() != null || request.getIdDiaChi() != null) {
            String diaChiCuThe = request.getDiaChiCuThe() != null ? request.getDiaChiCuThe().trim() : "";
            Integer idTinh = request.getThanhPho();
            Integer idQuan = request.getQuan();
            Integer idDiaChi = request.getIdDiaChi();

            StringBuilder fullAddress = new StringBuilder();
            if (!diaChiCuThe.isEmpty()) fullAddress.append(diaChiCuThe);
            if (idQuan != null) {
                QuanHuyen quan = quanHuyenRepository.findById(idQuan)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy quận/huyện ID: " + idQuan));
                if (fullAddress.length() > 0) fullAddress.append(", ");
                fullAddress.append(quan.getTenQuan());
            }
            if (idTinh != null) {
                TinhThanh tinh = tinhThanhRepository.findById(idTinh)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy tỉnh/thành ID: " + idTinh));
                if (fullAddress.length() > 0) fullAddress.append(", ");
                fullAddress.append(tinh.getTenTinh());
            }

            String newAddress = fullAddress.toString();
            String oldAddress = hoaDon.getDiaChiKhachHang();
            hoaDon.setDiaChiKhachHang(newAddress);
            luuLichSu(hoaDon, "Cập nhật địa chỉ giao hàng",
                    String.format("Địa chỉ: '%s' → '%s'",
                            oldAddress != null ? oldAddress : "(Trống)", newAddress), null);

            if (hoaDon.getKhachHang() != null) {
                KhachHang kh = hoaDon.getKhachHang();
                DiaChi diaChiMacDinh;

                if (idDiaChi != null) {
                    diaChiMacDinh = diaChiRepository.findById(idDiaChi)
                            .orElseThrow(() -> new RuntimeException("Địa chỉ ID không tồn tại: " + idDiaChi));
                    if (!diaChiMacDinh.getKhachHang().getId().equals(kh.getId())) {
                        throw new RuntimeException("Không được sửa địa chỉ của khách hàng khác!");
                    }
                } else {
                    diaChiMacDinh = new DiaChi();
                    diaChiMacDinh.setKhachHang(kh);
                    diaChiMacDinh.setTenDiaChi("Địa chỉ từ hóa đơn #" + hoaDon.getMaHoaDon());
                }

                diaChiMacDinh.setDiaChiCuThe(diaChiCuThe);
                if (idTinh != null) {
                    diaChiMacDinh.setTinhThanh(tinhThanhRepository.getById(idTinh));
                }
                if (idQuan != null) {
                    diaChiMacDinh.setQuanHuyen(quanHuyenRepository.getById(idQuan));
                }

                diaChiRepository.updateTrangThaiByKhachHangId(kh.getId(), false);
                diaChiMacDinh.setTrangThai(true);
                diaChiRepository.save(diaChiMacDinh);

                luuLichSu(hoaDon, "Đồng bộ địa chỉ khách hàng",
                        idDiaChi != null ? "Cập nhật & đặt làm mặc định địa chỉ ID: " + idDiaChi
                                : "Tạo mới & đặt làm mặc định địa chỉ từ hóa đơn", null);
            }
        }

        // ==================== CẬP NHẬT GHI CHÚ, TRẠNG THÁI, NHÂN VIÊN, PTT ====================
        if (request.getGhiChu() != null && !request.getGhiChu().equals(hoaDon.getGhiChu())) {
            String old = hoaDon.getGhiChu();
            hoaDon.setGhiChu(request.getGhiChu());
            luuLichSu(hoaDon, "Cập nhật ghi chú",
                    String.format("Ghi chú: '%s' → '%s'", old != null ? old : "(Trống)", request.getGhiChu()), null);
        }

        if (request.getTrangThai() != null && !request.getTrangThai().equals(hoaDon.getTrangThai())) {
            Integer old = hoaDon.getTrangThai();
            hoaDon.setTrangThai(request.getTrangThai());
            luuLichSu(hoaDon, "Cập nhật trạng thái hóa đơn",
                    String.format("Trạng thái: %s → %s", old, request.getTrangThai()), null);
        }

        if (request.getIdNhanVien() != null) {
            NhanVien nv = nhanVienRepository.findById(request.getIdNhanVien())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên ID: " + request.getIdNhanVien()));
            String old = hoaDon.getNhanVien() != null ? hoaDon.getNhanVien().getHoTen() : "N/A";
            hoaDon.setNhanVien(nv);
            luuLichSu(hoaDon, "Cập nhật nhân viên", String.format("Nhân viên: '%s' → '%s'", old, nv.getHoTen()), null);
        }

        if (request.getIdPhuongThucThanhToan() != null) {
            PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findById(request.getIdPhuongThucThanhToan())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phương thức thanh toán"));

            String oldPTTT = hoaDon.getHinhThucThanhToans() != null && !hoaDon.getHinhThucThanhToans().isEmpty()
                    ? hoaDon.getHinhThucThanhToans().get(0).getPhuongThucThanhToan().getTenPhuongThucThanhToan()
                    : "Chưa có";

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

        hoaDon.setNgaySua(new Date());

        // ==================== CẬP NHẬT CHI TIẾT SẢN PHẨM (QUAN TRỌNG NHẤT) ====================
        if (request.getChiTietSanPhams() != null && !request.getChiTietSanPhams().isEmpty()) {

            List<HoaDonChiTiet> chiTietHienTai = hoaDonChiTietRepository.findByHoaDonId(id);
            Map<Integer, HoaDonChiTiet> mapTonTai = chiTietHienTai.stream()
                    .collect(Collectors.toMap(ct -> ct.getChiTietSanPham().getId(), ct -> ct));

            Map<Integer, UpdateHoaDonRequest.ChiTietSanPhamRequest> requestMap = new HashMap<>();

            // 1. Xử lý các request có id (HoaDonChiTiet) → cập nhật chính xác dòng đó
            for (UpdateHoaDonRequest.ChiTietSanPhamRequest spReq : request.getChiTietSanPhams()) {
                if (spReq.getIdChiTietSanPham() == null) {
                    throw new RuntimeException("idChiTietSanPham không được null");
                }

                if (spReq.getId() != null) {
                    HoaDonChiTiet existing = hoaDonChiTietRepository.findById(spReq.getId())
                            .orElseThrow(() -> new RuntimeException("Không tìm thấy chi tiết hóa đơn ID: " + spReq.getId()));

                    int soLuongCu = existing.getSoLuong();
                    int soLuongMoi = spReq.getSoLuong();

                    ChiTietSanPham ctsp = existing.getChiTietSanPham();

                    if (soLuongMoi > soLuongCu) {
                        // Tăng số lượng trong hóa đơn → bán thêm → chiếm thêm kho → GIẢM tồn kho
                        int them = soLuongMoi - soLuongCu;
                        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - them);
                    } else if (soLuongMoi < soLuongCu) {
                        // Giảm số lượng trong hóa đơn → hủy bán → trả lại kho → TĂNG tồn kho
                        int bot = soLuongCu - soLuongMoi;
                        ctsp.setSoLuongTon(ctsp.getSoLuongTon() + bot);
                    }

                    chiTietSanPhamRepository.save(ctsp);
                    existing.setSoLuong(soLuongMoi);
                    existing.setGiaBan(spReq.getGiaBan());
                    existing.setGhiChu(spReq.getGhiChu());
                    hoaDonChiTietRepository.save(existing);

                    mapTonTai.remove(ctsp.getId());
                } else {
                    requestMap.merge(spReq.getIdChiTietSanPham(), spReq, (old, neu) -> {
                        neu.setSoLuong(old.getSoLuong() + neu.getSoLuong());
                        return neu;
                    });
                }
            }

            for (Map.Entry<Integer, UpdateHoaDonRequest.ChiTietSanPhamRequest> e : requestMap.entrySet()) {
                Integer idCtsp = e.getKey();
                UpdateHoaDonRequest.ChiTietSanPhamRequest spReq = e.getValue();

                ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idCtsp)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy ChiTietSanPham ID: " + idCtsp));

                HoaDonChiTiet chiTiet = mapTonTai.get(idCtsp);

                if (chiTiet != null) {
                    // Cộng dồn số lượng
                    int slCu = chiTiet.getSoLuong();
                    int slThem = spReq.getSoLuong();
                    if (ctsp.getSoLuongTon() < slThem) {
                        throw new RuntimeException("Không đủ tồn kho cho sản phẩm: " + ctsp.getSanPham().getTenSanPham());
                    }
                    ctsp.setSoLuongTon(ctsp.getSoLuongTon() - slThem);
                    chiTiet.setSoLuong(slCu + slThem);
                    chiTiet.setGiaBan(spReq.getGiaBan());
                    chiTiet.setGhiChu(spReq.getGhiChu());
                    hoaDonChiTietRepository.save(chiTiet);

                    luuLichSu(hoaDon, "Cập nhật số lượng sản phẩm",
                            String.format("Cộng thêm %d sản phẩm: %s (Tổng: %d)", slThem, ctsp.getSanPham().getTenSanPham(), slCu + slThem), null);
                } else {
                    // Thêm mới
                    if (ctsp.getSoLuongTon() < spReq.getSoLuong()) {
                        throw new RuntimeException("Không đủ tồn kho cho sản phẩm: " + ctsp.getSanPham().getTenSanPham());
                    }
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
                            String.format("Thêm %d %s", spReq.getSoLuong(), ctsp.getSanPham().getTenSanPham()), null);
                }
                mapTonTai.remove(idCtsp);
            }

            for (HoaDonChiTiet ct : mapTonTai.values()) {
                ChiTietSanPham ctsp = ct.getChiTietSanPham();
                ctsp.setSoLuongTon(ctsp.getSoLuongTon() + ct.getSoLuong());
                chiTietSanPhamRepository.save(ctsp);

                luuLichSu(hoaDon, "Xóa sản phẩm khỏi hóa đơn",
                        String.format("Xóa %d %s (Màu: %s, Size: %s) - Đã trả tồn kho",
                                ct.getSoLuong(),
                                ctsp.getSanPham().getTenSanPham(),
                                ctsp.getMauSac().getTenMauSac(),
                                ctsp.getKichThuoc().getTenKichThuoc()), null);

                hoaDonChiTietRepository.delete(ct);
            }
        }

        hoaDonRepository.save(hoaDon);
        return new UpdateHoaDonResponse(true, "Cập nhật hóa đơn thành công");
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

}




