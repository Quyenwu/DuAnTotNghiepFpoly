package com.example.the_autumn.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.the_autumn.dto.*;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.entity.HinhThucThanhToan;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.HoaDonChiTiet;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
import com.example.the_autumn.model.response.UpdateHoaDonResponse;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import com.example.the_autumn.repository.HoaDonChiTietRepository;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhieuGiamGiaRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
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
public  class HoaDonServiceImpl implements HoaDonService {
    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PhieuGiamGiaRepository phieuGiamGiaRepository;

    @Autowired
    private Cloudinary cloudinary;




    @Override
    public PageResponseDTO<HoaDonDTO> getAll(Pageable pageable) {
        Page<HoaDon> page = hoaDonRepository.findAll(pageable);

        // ✅ Convert Entity → DTO
        List<HoaDonDTO> dtoList = page.getContent().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        // ✅ Tạo response
        return new PageResponseDTO<>(
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
    private HoaDonDTO convertToDTO(HoaDon hd) {
        HoaDonDTO dto = new HoaDonDTO();

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

        // ✅ Chuyển đổi KhachHang (tránh vòng lặp)
        if (hd.getKhachHang() != null) {
            KhachHangDTO khDTO = new KhachHangDTO();
            khDTO.setId(hd.getKhachHang().getId());
            khDTO.setHoTen(hd.getKhachHang().getHoTen());
            khDTO.setSdt(hd.getKhachHang().getSdt());
            khDTO.setEmail(hd.getKhachHang().getEmail());
            dto.setKhachHang(khDTO);
        }

        // ✅ Chuyển đổi NhanVien (tránh vòng lặp)
        if (hd.getNhanVien() != null) {
            NhanVienDTO nvDTO = new NhanVienDTO();
            nvDTO.setId(hd.getNhanVien().getId());
            nvDTO.setHoTen(hd.getNhanVien().getHoTen());
            nvDTO.setSdt(hd.getNhanVien().getSdt());
            nvDTO.setEmail(hd.getNhanVien().getEmail());
            dto.setNhanVien(nvDTO);

            dto.setLoaiHoaDonText(hd.getLoaiHoaDon() != null && hd.getLoaiHoaDon() ? "Online" : "Tại quầy");

            // ⭐ Thêm hình thức thanh toán (nếu có quan hệ)
            // ⭐ Thêm hình thức thanh toán (nếu có quan hệ)
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

    @Override
    public Optional<HoaDon> getById(Integer id) {
        return hoaDonRepository.findById(id);
    }



    @Override
    public byte[] printInvoices(List<Integer> invoiceIds) throws Exception {
        List<HoaDon> hoaDons = hoaDonRepository.findAllById(invoiceIds);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, baos);
        document.open();

        // ⭐ Font tiếng Việt (quan trọng!)
        BaseFont bf = BaseFont.createFont("c:/windows/fonts/arial.ttf",
                BaseFont.IDENTITY_H,
                BaseFont.EMBEDDED);
        Font titleFont = new Font(bf, 18, Font.BOLD);
        Font headerFont = new Font(bf, 12, Font.BOLD);
        Font normalFont = new Font(bf, 10, Font.NORMAL);

        // Tiêu đề
        Paragraph title = new Paragraph("DANH SÁCH HÓA ĐƠN", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(10);
        document.add(title);

        // Tạo bảng
        PdfPTable table = new PdfPTable(9); // 7 cột
        table.setWidthPercentage(100);
        table.setSpacingBefore(10);
        table.setSpacingAfter(10);

        // Set độ rộng cột
        float[] columnWidths = {0.8f, 1.5f, 2f, 1.8f, 1.5f, 1.2f, 1.2f, 1.2f, 1.8f};
        table.setWidths(columnWidths);

        // Header bảng
        String[] headers = {"STT", "Mã HĐ", "Khách hàng", "Nhân viên", "Trạng thái", "Dịch vụ", "Hình thức TT",
                "Ngày tạo", "Tổng tiền"};

        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
            cell.setPadding(8);
            table.addCell(cell);
        }

        // Dữ liệu
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        int stt = 1;

        for (HoaDon hd : hoaDons) {
            // STT
            PdfPCell cell1 = new PdfPCell(new Phrase(String.valueOf(stt++), normalFont));
            cell1.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell1.setPadding(20);
            table.addCell(cell1);


            // Mã hóa đơn
            table.addCell(new Phrase(hd.getMaHoaDon(), normalFont));

// Khách hàng
            String tenKH = hd.getKhachHang() != null ? hd.getKhachHang().getHoTen() : "N/A";
            table.addCell(new Phrase(tenKH, normalFont));

// Nhân viên
            String tenNV = hd.getNhanVien() != null ? hd.getNhanVien().getHoTen() : "N/A";
            table.addCell(new Phrase(tenNV, normalFont));

// Trạng thái
            String trangThaiText = TrangThaiHoaDon.getText(hd.getTrangThai());
            table.addCell(new Phrase(trangThaiText, normalFont));

// Dịch vụ
            String dichVu = hd.getLoaiHoaDon() ? "Tại quầy" : "Online";
            table.addCell(new Phrase(dichVu, normalFont));

// Hình thức thanh toán
//            String hinhThuc = hd.getHinhThucThanhToan() != null ? hd.getHinhThucThanhToan() : "N/A";
//            table.addCell(new Phrase(hinhThuc, normalFont));


// Ngày tạo
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String ngayTao = dateFormat.format(hd.getNgayTao());
            table.addCell(new Phrase(ngayTao, normalFont));

// Tổng tiền
            PdfPCell cellTien = new PdfPCell(new Phrase(
                    currencyFormat.format(hd.getTongTien()), normalFont));
            cellTien.setHorizontalAlignment(Element.ALIGN_RIGHT);
            cellTien.setPadding(10);
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




    @Override
    public PageResponseDTO<HoaDonDTO> timkiemVaLoc(
            String maHoaDon,
            String tenKhachHang,
            String tenNhanVien,
            Boolean loaiHoaDon,
            Integer trangThai,
            LocalDate ngayTao,
            String priceRange,
            int page,
            int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<HoaDon> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (maHoaDon != null && !maHoaDon.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("maHoaDon")),
                        "%" + maHoaDon.toLowerCase().trim() + "%"));
            }
            if (tenKhachHang != null && !tenKhachHang.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("khachHang").get("hoTen")),
                        "%" + tenKhachHang.toLowerCase().trim() + "%"));
            }
            if (tenNhanVien != null && !tenNhanVien.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("nhanVien").get("hoTen")),
                        "%" + tenNhanVien.toLowerCase().trim() + "%"));
            }
            if (loaiHoaDon != null) {
                predicates.add(cb.equal(root.get("loaiHoaDon"), loaiHoaDon));
            }
            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }


            if (ngayTao != null) {
                LocalDateTime startOfDay = ngayTao.atStartOfDay(); // 00:00:00
                LocalDateTime endOfDay = ngayTao.atTime(23, 59, 59); // 23:59:5
                predicates.add(cb.between(root.get("ngayTao"), startOfDay, endOfDay));
            }

            if (priceRange != null && !priceRange.trim().isEmpty()) {
                switch (priceRange.toLowerCase()) {
                    case "under100":  // Dưới 100K
                        predicates.add(cb.lessThan(root.get("tongTien"), new BigDecimal("100000")));
                        break;
                    case "100to500":  // 100K - 500K
                        predicates.add(cb.greaterThanOrEqualTo(root.get("tongTien"), new BigDecimal("100000")));
                        predicates.add(cb.lessThanOrEqualTo(root.get("tongTien"), new BigDecimal("500000")));
                        break;
                    case "500to1m":   // 500K - 1 triệu
                        predicates.add(cb.greaterThanOrEqualTo(root.get("tongTien"), new BigDecimal("500000")));
                        predicates.add(cb.lessThanOrEqualTo(root.get("tongTien"), new BigDecimal("1000000")));
                        break;
                    case "over1m":    // Trên 1 triệu
                        predicates.add(cb.greaterThan(root.get("tongTien"), new BigDecimal("1000000")));
                        break;
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<HoaDon> pageResult = hoaDonRepository.findAll(spec, pageable);

        List<HoaDonDTO> dtoList = pageResult.getContent().stream()
                .map(hoaDon -> new HoaDonDTO(hoaDon))
                .toList();

        return new PageResponseDTO<>(
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






    @Override
    public HoaDonDetailDTO getHoaDonDetail(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + id));

        HoaDonDetailDTO dto = new HoaDonDetailDTO();

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
        List<HoaDonDetailDTO.ChiTietSanPhamDTO> chiTietDTOs = chiTietList.stream()
                .map(ct -> {
                    HoaDonDetailDTO.ChiTietSanPhamDTO ctDTO = new HoaDonDetailDTO.ChiTietSanPhamDTO();
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

    @Override
    public boolean canEdit(Integer idHoaDon) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn với ID: " + idHoaDon));


        return hoaDon.getTrangThai() != null && hoaDon.getTrangThai() == 0;
    }

    @Override
    @Transactional
    public UpdateHoaDonResponse updateHoaDon(Integer id, UpdateHoaDonRequest request) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn ID " + id));

        // ✅ 1. Cập nhật thông tin khách hàng (nếu có)
        if (hoaDon.getKhachHang() != null) {
            if (request.getHoTenKhachHang() != null && !request.getHoTenKhachHang().isEmpty()) {
                hoaDon.getKhachHang().setHoTen(request.getHoTenKhachHang());
            }
            if (request.getSdtKhachHang() != null && !request.getSdtKhachHang().isEmpty()) {
                hoaDon.getKhachHang().setSdt(request.getSdtKhachHang());
            }
            if (request.getEmailKhachHang() != null && !request.getEmailKhachHang().isEmpty()) {
                hoaDon.getKhachHang().setEmail(request.getEmailKhachHang());
            }

            if (request.getDiaChiKhachHang() != null) {
                hoaDon.setDiaChiKhachHang(request.getDiaChiKhachHang());
            }
            khachHangRepository.save(hoaDon.getKhachHang());
        }

        // ✅ 2. Cập nhật ghi chú hóa đơn
        if (request.getGhiChu() != null) {
            hoaDon.setGhiChu(request.getGhiChu());
        }

        // ✅ 3. Cập nhật ngày sửa
        hoaDon.setNgaySua(new Date());

        // 🚫 4. KHÔNG ĐỤNG TỚI giảm giá, chi tiết sản phẩm, phí vận chuyển, phiếu giảm giá
        // → Giữ nguyên các phần đó

        hoaDonRepository.save(hoaDon);

        return new UpdateHoaDonResponse(true, "Cập nhật khách hàng và ghi chú thành công");
    }





    @Override
    public Optional<HoaDon> findById(Integer id) {
        return hoaDonRepository.findById(id);
    }

    @Override
    public HoaDon save(HoaDon hoaDon) {
        return hoaDonRepository.save(hoaDon);
    }



    public String uploadFile(MultipartFile file) {
        try {
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap("resource_type", "auto"));
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Upload thất bại: " + e.getMessage());
        }
    }






}


