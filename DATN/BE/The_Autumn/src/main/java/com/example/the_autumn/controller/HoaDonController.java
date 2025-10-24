package com.example.the_autumn.controller;

import com.example.the_autumn.dto.*;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
import com.example.the_autumn.model.response.UpdateHoaDonResponse;
import com.example.the_autumn.repository.HoaDonChiTietRepository;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.service.HoaDonService;
import com.example.the_autumn.service.HoaDonServiceImpl;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
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

@RestController
@RequestMapping("/api/hoa-don")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class HoaDonController {
    @Autowired
    private HoaDonService hoaDonService;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hoaDonChiTietRepository;

    @Autowired
    private HoaDonServiceImpl hoaDonServiceimpl;





    @GetMapping("/{id}")
    public ResponseEntity<HoaDon> getById(@PathVariable Integer id) {
        Optional<HoaDon> hoaDon = hoaDonService.getById(id);
        return hoaDon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }




    @GetMapping
    public ResponseEntity<PageResponseDTO<HoaDonDTO>> getAllOrSearch(
            @RequestParam(required = false) String maHoaDon,
            @RequestParam(required = false) String tenKhachHang,
            @RequestParam(required = false) String tenNhanVien,
            @RequestParam(required = false) Boolean loaiHoaDon,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTao, // ⭐ THÊM
            @RequestParam(required = false) String priceRange,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {

        if (maHoaDon != null || tenKhachHang != null || tenNhanVien != null ||
                loaiHoaDon != null ||  trangThai != null || ngayTao != null ||  priceRange != null ) {

            PageResponseDTO<HoaDonDTO> response = hoaDonService.timkiemVaLoc(
                    maHoaDon, tenKhachHang, tenNhanVien,
                    loaiHoaDon, trangThai, ngayTao, priceRange,
                    page, size
            );
            return ResponseEntity.ok(response);
        }

        PageResponseDTO<HoaDonDTO> response = hoaDonService.getAll(PageRequest.of(page, size));
        return ResponseEntity.ok(response);
    }



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

                // ✅ DÙNG TÊN ĐẦY ĐỦ CHO EXCEL
                org.apache.poi.ss.usermodel.Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.DARK_BLUE.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                CellStyle currencyStyle = workbook.createCellStyle();
                currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

                Row headerRow = sheet.createRow(0);
                String[] columns = {"STT", "Mã hóa đơn", "Tên khách hàng", "Nhan vien","Trạng thái", "Dich vu",
                        "Hình thức thanh toán",  "Ngày tạo","Tổng tiền" };

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
                    row.createCell(3).setCellValue(
                            hd.getLoaiHoaDon() != null ? String.valueOf(hd.getLoaiHoaDon()) : ""
                    );


                    Cell cellTien = row.createCell(4);
                    if (hd.getTongTien() != null) {
                        cellTien.setCellValue(hd.getTongTien().doubleValue());
                        cellTien.setCellStyle(currencyStyle);
                    } else {
                        cellTien.setCellValue(0);
                    }

                    String trangThaiText = TrangThaiHoaDon.getText(hd.getTrangThai());
                    row.createCell(5).setCellValue(trangThaiText);

                    // ✅ SỬA LẠI PHẦN NGÀY TẠO
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
            HoaDonDetailDTO detail = hoaDonService.getHoaDonDetail(id);
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

    @PutMapping("/{id}/trang-thai")
    public ResponseEntity<?> updateTrangThai(
            @PathVariable Integer id,
            @RequestParam("trangThai") Integer trangThai) {
        try {
            HoaDon hoaDon = hoaDonService.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
            if (hoaDon == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "Không tìm thấy hóa đơn"));
            }

            hoaDon.setTrangThai(trangThai);
            hoaDonService.save(hoaDon);

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

    @PostMapping
    public String upload(@RequestParam("file") MultipartFile file) {
        return hoaDonServiceimpl.uploadFile(file);
    }



}





//
