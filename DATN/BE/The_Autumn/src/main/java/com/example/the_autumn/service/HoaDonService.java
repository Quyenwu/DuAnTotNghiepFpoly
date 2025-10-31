package com.example.the_autumn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.the_autumn.dto.KhachHangDTO;
import com.example.the_autumn.dto.NhanVienDTO;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.HinhThucThanhToan;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.HoaDonChiTiet;
import com.example.the_autumn.entity.LichSuHoaDon;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.model.request.PageHoaDonRequest;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
import com.example.the_autumn.model.response.HoaDonDetailResponse;
import com.example.the_autumn.model.response.HoaDonRespone;
import com.example.the_autumn.model.response.TrangThaiHoaDonRespone;
import com.example.the_autumn.model.response.UpdateHoaDonResponse;
import com.example.the_autumn.repository.HoaDonChiTietRepository;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
import com.example.the_autumn.repository.NhanVienRepository;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
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

//    @Autowired
//    private Cloudinary cloudinary;

    @Autowired
    private EmailService emailService;

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

    // ✅ Method chuyển đổi Entity → DTO (QUAN TRỌNG!)
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
            dto.setLoaiHoaDonText(hd.getLoaiHoaDon() != null && hd.getLoaiHoaDon() ? "Tại quầy" : "Online");

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


    private void addTableHeader(PdfPTable table, Font font, String... headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, font));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private void addTableRow(PdfPTable table, Font font, String... values) {
        for (String value : values) {
            PdfPCell cell = new PdfPCell(new Phrase(value, font));
            cell.setPadding(5);
            table.addCell(cell);
        }
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) return "0 d";
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        return formatter.format(amount) + " d";
    }







    public HoaDonDetailResponse getHoaDonDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + id));

        HoaDonDetailResponse dto = new HoaDonDetailResponse();

        // Thông tin cơ bản
        dto.setId(hoaDon.getId());
        dto.setMaHoaDon(hoaDon.getMaHoaDon());
        dto.setNgayTao(hoaDon.getNgayTao());
        dto.setNgayThanhToan(hoaDon.getNgayThanhToan());

        // Thông tin khách hàng
        if (hoaDon.getKhachHang() != null) {
            dto.setTenKhachHang(hoaDon.getKhachHang().getHoTen());
            dto.setSdtKhachHang(hoaDon.getKhachHang().getSdt());
            dto.setEmailKhachHang(hoaDon.getKhachHang().getEmail());
        } else {
            dto.setTenKhachHang("Khách lẻ");
            dto.setSdtKhachHang("N/A");
            dto.setEmailKhachHang("N/A");
        }
        dto.setDiaChiKhachHang(hoaDon.getDiaChiKhachHang());


        if (hoaDon.getNhanVien() != null) {
            dto.setTenNhanVien(hoaDon.getNhanVien().getHoTen());
            dto.setSdtNhanVien(hoaDon.getNhanVien().getSdt());
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
                    ctDTO.setIdChiTietSanPham(ct.getId());
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


        return hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 0;
    }


    @Transactional
    public UpdateHoaDonResponse updateHoaDon(Integer id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID " + id));
        StringBuilder thayDoiLog = new StringBuilder();

        // ✅ 1. Cập nhật thông tin khách hàng
        if (hoaDon.getKhachHang() != null) {
            boolean coThayDoi = false;
            if (request.getHoTenKhachHang() != null &&
                    !request.getHoTenKhachHang().equals(hoaDon.getKhachHang().getHoTen())) {
                String oldName = hoaDon.getKhachHang().getHoTen();
                hoaDon.getKhachHang().setHoTen(request.getHoTenKhachHang());
                thayDoiLog.append(String.format("Tên khách hàng: '%s' → '%s'. ",
                        oldName, request.getHoTenKhachHang()));
                coThayDoi = true;
            }
            if (request.getSdtKhachHang() != null &&
                    !request.getSdtKhachHang().equals(hoaDon.getKhachHang().getSdt())) {
                String oldPhone = hoaDon.getKhachHang().getSdt();
                hoaDon.getKhachHang().setSdt(request.getSdtKhachHang());
                thayDoiLog.append(String.format("SĐT: '%s' → '%s'. ",
                        oldPhone, request.getSdtKhachHang()));
                coThayDoi = true;
            }
            if (request.getEmailKhachHang() != null &&
                    !request.getEmailKhachHang().equals(hoaDon.getKhachHang().getEmail())) {
                String oldEmail = hoaDon.getKhachHang().getEmail();
                hoaDon.getKhachHang().setEmail(request.getEmailKhachHang());
                thayDoiLog.append(String.format("Email: '%s' → '%s'. ",
                        oldEmail, request.getEmailKhachHang()));
                coThayDoi = true;
            }
            if (coThayDoi) {
                khachHangRepository.save(hoaDon.getKhachHang());
                luuLichSu(hoaDon, "Cập nhật thông tin khách hàng", thayDoiLog.toString().trim(), null);
            }
        }
        // ✅ 2. Cập nhật địa chỉ giao hàng
        if (request.getDiaChiKhachHang() != null &&
                !request.getDiaChiKhachHang().equals(hoaDon.getDiaChiKhachHang())) {
            String oldAddress = hoaDon.getDiaChiKhachHang();
            hoaDon.setDiaChiKhachHang(request.getDiaChiKhachHang());
            String moTa = String.format("Địa chỉ: '%s' → '%s'",
                    oldAddress != null ? oldAddress : "(Trống)",
                    request.getDiaChiKhachHang());
            luuLichSu(hoaDon, "Cập nhật địa chỉ giao hàng", moTa, null);
        }
        // ✅ 3. Cập nhật ghi chú
        if (request.getGhiChu() != null &&
                !request.getGhiChu().equals(hoaDon.getGhiChu())) {
            String oldNote = hoaDon.getGhiChu();
            hoaDon.setGhiChu(request.getGhiChu());
            String moTa = String.format("Ghi chú: '%s' → '%s'",
                    oldNote != null ? oldNote : "(Trống)",
                    request.getGhiChu());
            luuLichSu(hoaDon, "Cập nhật ghi chú", moTa, null);
        }
        if (request.getTrangThai() != null && !request.getTrangThai().equals(hoaDon.getTrangThai())) {
            Integer oldStatus = hoaDon.getTrangThai();
            hoaDon.setTrangThai(request.getTrangThai());
            luuLichSu(hoaDon, "Cập nhật trạng thái hóa đơn",
                    String.format("Trạng thái: %s → %s", oldStatus, request.getTrangThai()), null);
        }


        // ✅ 4. Cập nhật ngày sửa
        hoaDon.setNgaySua(new Date());
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

            // Set nhân viên (người thực hiện)
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


    // ✅ THÊM METHOD LẤY LỊCH SỬ
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

        // Cập nhật dịch vụ
        hoaDon.setLoaiHoaDon(loaiHoaDon);
        hoaDon.setNgaySua(new Date());
        hoaDonRepository.save(hoaDon);

        // Lưu lịch sử
        String moTa = String.format("Dịch vụ: '%s' → '%s'", oldServiceText, newServiceText);
        luuLichSu(hoaDon, "Cập nhật dịch vụ", moTa, null);

        return "Cập nhật dịch vụ từ " + oldServiceText + " sang " + newServiceText + " thành công";
    }

}




