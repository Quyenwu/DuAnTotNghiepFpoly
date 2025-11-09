package com.example.the_autumn.controller;


import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.entity.LichSuHoaDon;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhuongThucThanhToan;
import com.example.the_autumn.model.request.HoaDonRequest;
import com.example.the_autumn.model.request.PageHoaDonRequest;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
import com.example.the_autumn.model.response.*;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhuongThucThanhToanRepository;
import com.example.the_autumn.service.AnhService;
import com.example.the_autumn.service.HoaDonService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/hoa-don")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class HoaDonController {
    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucRepository;

    @Autowired
    private  AnhService anhService;

    @GetMapping("/{id}")
    public ResponseEntity<HoaDon> getById(@PathVariable Integer id) {
        Optional<HoaDon> hoaDon = hoaDonService.getById(id);
        return hoaDon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<PageHoaDonRequest<HoaDonRespone>> getAllOrSearch(
            @RequestParam(required = false) String searchText,  // ⭐ THAY: gộp 3 ô thành 1
            @RequestParam(required = false) List<Boolean> loaiHoaDon,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTao,
            @RequestParam(required = false) String hinhThucThanhToan,  // ⭐ THÊM: lọc hình thức thanh toán
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        if (searchText != null || loaiHoaDon != null && !loaiHoaDon.isEmpty() || trangThai != null ||
                ngayTao != null || hinhThucThanhToan != null) {
            PageHoaDonRequest<HoaDonRespone> response = hoaDonService.timkiemVaLoc(
                    searchText,  // ⭐ THAY
                    loaiHoaDon, trangThai, ngayTao, hinhThucThanhToan,  // ⭐ THAY & THÊM
                    page, size
            );
            return ResponseEntity.ok(response);
        }
        PageHoaDonRequest<HoaDonRespone> response = hoaDonService.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }

    // Các method khác giữ nguyên...
    @GetMapping("/export")
    public void exportExcel(HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");

            String fileName = "HoaDon_" + System.currentTimeMillis() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            List<HoaDon> list = hoaDonRepository.findAllWithDetails();
            System.out.println("Tìm thấy: " + list.size() + " hóa đơn");

            if (list == null || list.isEmpty()) {
                throw new RuntimeException("Không có dữ liệu để xuất");
            }
            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Danh sách hóa đơn");
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);
                CellStyle currencyStyle = workbook.createCellStyle();
                currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));
                Row headerRow = sheet.createRow(0);
                String[] columns = {"STT", "Mã hóa đơn", "Tên khách hàng", "Nhân viên", "Trạng thái", "Dịch vụ",
                        "Hình thức thanh toán", "Ngày tạo", "Tổng tiền"};
                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                int rowNum = 1;
                for (HoaDon hd : list) {
                    Row row = sheet.createRow(rowNum++);
                    row.createCell(0).setCellValue(rowNum - 1);
                    row.createCell(1).setCellValue(hd.getMaHoaDon() != null ? hd.getMaHoaDon() : "");
                    row.createCell(2).setCellValue(hd.getKhachHang() != null ? hd.getKhachHang().getHoTen() : "Khách vãng lai");
                    row.createCell(3).setCellValue(hd.getNhanVien() != null ? hd.getNhanVien().getHoTen() : "");

                    String trangThaiText = TrangThaiHoaDonRespone.getText(hd.getTrangThai());
                    row.createCell(4).setCellValue(trangThaiText);

                    String dichVu = hd.getLoaiHoaDon() != null && hd.getLoaiHoaDon() ? "Tại quầy" : "Online";
                    row.createCell(5).setCellValue(dichVu);

                    // ⭐ THÊM: Hình thức thanh toán
                    String hinhThuc = "";
                    if (hd.getHinhThucThanhToans() != null && !hd.getHinhThucThanhToans().isEmpty()) {
                        hinhThuc = hd.getHinhThucThanhToans().get(0)
                                .getPhuongThucThanhToan()
                                .getTenPhuongThucThanhToan();
                    }
                    row.createCell(6).setCellValue(hinhThuc);

                    String ngayTaoStr = "";
                    if (hd.getNgayTao() != null) {
                        try {
                            Object ngayTao = hd.getNgayTao();
                            if (ngayTao instanceof LocalDate) {
                                ngayTaoStr = ((LocalDate) ngayTao).format(dtf);
                            } else if (ngayTao instanceof LocalDateTime) {
                                ngayTaoStr = ((LocalDateTime) ngayTao).format(dtf);
                            } else if (ngayTao instanceof Date) {
                                ngayTaoStr = sdf.format((Date) ngayTao);
                            }
                        } catch (Exception ex) {
                            ngayTaoStr = "";
                        }
                    }
                    row.createCell(7).setCellValue(ngayTaoStr);

                    Cell cellTien = row.createCell(8);
                    if (hd.getTongTien() != null) {
                        cellTien.setCellValue(hd.getTongTien().doubleValue());
                        cellTien.setCellStyle(currencyStyle);
                    } else {
                        cellTien.setCellValue(0);
                    }
                }

                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(response.getOutputStream());
                response.getOutputStream().flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Lỗi xuất file Excel: " + e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }

    @PostMapping("/print")
    public ResponseEntity<byte[]> printInvoices(@RequestBody List<Integer> invoiceIds) {
        try {
            byte[] pdfBytes = hoaDonService.printInvoices(invoiceIds);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDisposition(
                    ContentDisposition.inline()
                            .filename("HoaDon_" + System.currentTimeMillis() + ".pdf")
                            .build()
            );

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpServletResponse.SC_INTERNAL_SERVER_ERROR).build();
        }
    }


    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getHoaDonDetail(@PathVariable Integer id) {
        try {
            System.out.println("🔍 Đang tìm hóa đơn ID: " + id);  // ⭐ Log để debug
            HoaDonDetailResponse detail = hoaDonService.getHoaDonDetail(id);
            System.out.println("✅ Tìm thấy hóa đơn: " + detail.getMaHoaDon());
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi: " + e.getMessage());  // ⭐ In ra lỗi chi tiết
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }


    @GetMapping("/{id}/can-edit")
    public ResponseEntity<?> canEdit(@PathVariable Integer id) {
        try {
            boolean canEdit = hoaDonService.canEdit(id);
            Map<String, Object> response = new HashMap<>();
            response.put("canEdit", canEdit);
            response.put("message", canEdit ? "Có thể sửa hóa đơn" : "Không thể sửa hóa đơn này");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/can-edit-shipping")
    public ResponseEntity<?> canEditShippingStatus(@PathVariable Integer id) {
        try {
            boolean canEdit = hoaDonService.canEditShippingStatus(id);
            Map<String, Object> response = new HashMap<>();
            response.put("canEdit", canEdit);
            response.put("message", canEdit ? "Có thể sửa trạng thái giao hàng" : "Không thể sửa trạng thái giao hàng");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateHoaDon(
            @PathVariable Integer id,
            @RequestBody UpdateHoaDonRequest request) {
        try {
            UpdateHoaDonResponse response = hoaDonService.updateHoaDon(id, request);

            if (response.isSuccess()) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }



    @GetMapping("/{id}/lich-su")
    public ResponseEntity<?> getLichSuHoaDon(@PathVariable Integer id) {
        try {
            List<LichSuHoaDon> lichSu = hoaDonService.getLichSuHoaDon(id);

            // Convert sang DTO để tránh vòng lặp JSON
            List<Map<String, Object>> response = lichSu.stream()
                    .map(ls -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", ls.getId());
                        map.put("hanhDong", ls.getHanhDong());
                        map.put("moTa", ls.getMoTa());
                        map.put("ngayCapNhat", ls.getNgayCapNhat());
                        map.put("nguoiThucHien", ls.getNhanVien() != null ? ls.getNhanVien().getHoTen() : "Hệ thống");
                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Không thể tải lịch sử: " + e.getMessage()));
        }
    }


    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> updateTrangThai(
            @PathVariable Integer id,
            @RequestParam("trangThai") Integer trangThai) {
        try {
            HoaDon hoaDon = hoaDonService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

            Integer oldStatus = hoaDon.getTrangThai();
            hoaDon.setTrangThai(trangThai);
            hoaDonService.save(hoaDon);

            // ⭐ LƯU LỊCH SỬ THAY ĐỔI TRẠNG THÁI
            String oldStatusText = TrangThaiHoaDonRespone.getText(oldStatus);
            String newStatusText = TrangThaiHoaDonRespone.getText(trangThai);
            String moTa = String.format("Trạng thái: '%s' → '%s'", oldStatusText, newStatusText);

            // ✅ SỬA: Gọi method luuLichSu với đầy đủ 4 tham số
            hoaDonService.luuLichSu(hoaDon, "Cập nhật trạng thái đơn hàng", moTa, null);

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật trạng thái thành công!"
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "Lỗi khi cập nhật trạng thái"));
        }
    }


    @PutMapping("/{id}/service")
    public ResponseEntity<?> updateService(
            @PathVariable Integer id,
            @RequestBody Map<String, Boolean> request) {
        try {
            Boolean loaiHoaDon = request.get("loaiHoaDon");
            String result = hoaDonService.updateService(id, loaiHoaDon);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", result
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "success", false,
                            "error", e.getMessage()
                    ));
        }


    }

    @GetMapping("/nhan-vien")
    public ResponseEntity<?> getAllNhanVien() {
        try {
            List<NhanVien> list = nhanVienRepository.findAll();
            List<Map<String, Object>> result = list.stream()
                    .map(nv -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", nv.getId());
                        map.put("hoTen", nv.getHoTen());
                        map.put("maNhanVien", nv.getMaNhanVien());
                        map.put("email", nv.getEmail());
                        map.put("sdt", nv.getSdt());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }

    // ⭐ THÊM: API lấy danh sách phương thức thanh toán
    @GetMapping("/phuong-thuc-thanh-toan")
    public ResponseEntity<?> getAllPhuongThucThanhToan() {
        try {
            List<PhuongThucThanhToan> list = phuongThucRepository.findAll();
            List<Map<String, Object>> result = list.stream()
                    .map(pt -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", pt.getId());
                        map.put("tenPhuongThucThanhToan", pt.getTenPhuongThucThanhToan());
                        map.put("maPhuongThucThanhToan", pt.getMaPhuongThucThanhToan());
                        return map;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi: " + e.getMessage());
        }
    }



        @PostMapping("/add")
        public ResponseObject<?> addHoaDon(@RequestBody HoaDonRequest hoaDonRequest){
            HoaDon savedHoaDon = hoaDonService.add(hoaDonRequest);
            return new ResponseObject<>(savedHoaDon,"Thêm thành công");
        }
}













