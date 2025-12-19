package com.example.the_autumn.controller;




import com.example.the_autumn.entity.*;
import com.example.the_autumn.model.request.HoaDonRequest;
import com.example.the_autumn.model.request.HoanTienRequest;
import com.example.the_autumn.model.request.PageHoaDonRequest;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
import com.example.the_autumn.model.response.*;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.LichSuThanhToanRepository;
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
import org.springframework.messaging.simp.SimpMessagingTemplate; // [THÊM] Import WebSocket
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/hoa-don")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174","http://172.20.10.2:5173"})
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
    private LichSuThanhToanRepository lichSuThanhToanRepository;


    @Autowired
    private  AnhService anhService;


    // [THÊM] Inject WebSocket Template để gửi thông báo
    @Autowired
    private SimpMessagingTemplate messagingTemplate;




    /**
     * API này nhận dữ liệu JSON từ Frontend (LocalStorage) và bắn thẳng xuống WebSocket.
     * Không lưu vào DB, giúp hiển thị realtime ngay cả khi chưa tạo hóa đơn trong DB.
     */
    @PostMapping("/sync-display")
    public ResponseEntity<?> syncDisplayFromPOS(@RequestBody Map<String, Object> displayData) {
        try {
            // Log để debug xem có nhận được dữ liệu không
            // System.out.println("📡 POS Relay: Đồng bộ dữ liệu tạm tính từ Frontend");


            // Bắn tín hiệu xuống topic chung
            messagingTemplate.convertAndSend("/topic/display", displayData);


            return ResponseEntity.ok(Map.of("success", true));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.ok(Map.of("success", false));
        }
    }


    // Hàm Sync cho các thao tác CÓ lưu DB (Sau khi thanh toán/lưu)
    private void syncToCustomerDisplay(Integer idHoaDon) {
        try {
            if (idHoaDon == null) return;
            HoaDonDetailResponse detail = hoaDonService.getHoaDonDetail(idHoaDon);
            if (detail != null) {
                messagingTemplate.convertAndSend("/topic/display", detail);
            }
        } catch (Exception e) {
            System.err.println("❌ Lỗi socket DB: " + e.getMessage());
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<HoaDon> getById(@PathVariable Integer id) {
        Optional<HoaDon> hoaDon = hoaDonService.getById(id);
        return hoaDon.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }


    @GetMapping
    public ResponseEntity<PageHoaDonRequest<HoaDonRespone>> getAllOrSearch(
            @RequestParam(required = false) String searchText,
            @RequestParam(required = false) List<Boolean> loaiHoaDon,
            @RequestParam(required = false) Integer trangThai,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate ngayTao,
            @RequestParam(required = false) String hinhThucThanhToan,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        // GỌI DUY NHẤT MỘT PHƯƠNG THỨC timkiemVaLoc
        PageHoaDonRequest<HoaDonRespone> response = hoaDonService.timkiemVaLoc(
                searchText,
                loaiHoaDon,
                trangThai,
                ngayTao,
                hinhThucThanhToan,
                page,
                size
        );
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
            System.out.println("🔍 Đang tìm hóa đơn ID: " + id);
            HoaDonDetailResponse detail = hoaDonService.getHoaDonDetail(id);
            System.out.println("✅ Tìm thấy hóa đơn: " + detail.getMaHoaDon());
            return ResponseEntity.ok(detail);
        } catch (RuntimeException e) {
            System.err.println("❌ Lỗi: " + e.getMessage());
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
                // [THÊM] Gửi socket khi update thành công (Thêm khách, ghi chú...)
                syncToCustomerDisplay(id);
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


            // [THÊM] Gửi socket khi trạng thái thay đổi
            syncToCustomerDisplay(id);


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


            // [THÊM] Gửi socket khi đổi loại dịch vụ (Ship/Tại quầy -> Tiền có thể đổi)
            syncToCustomerDisplay(id);


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
        // [THÊM] Gửi socket khi tạo mới
        syncToCustomerDisplay(savedHoaDon.getId());


        return new ResponseObject<>(savedHoaDon,"Thêm thành công");
    }


    @GetMapping("/vnpay-return")
    public ResponseEntity<?> vnpayReturn(
            @RequestParam Map<String, String> params,
            HttpServletResponse response) {
        try {
            String result = hoaDonService.handleVNPayReturn(params);


            String redirectUrl = "http://localhost:3000/payment-result?status=" +
                    ("00".equals(params.get("vnp_ResponseCode")) ? "success" : "fail") +
                    "&message=" + URLEncoder.encode(result, StandardCharsets.UTF_8) +
                    "&orderId=" + params.get("vnp_TxnRef");


            response.sendRedirect(redirectUrl);
            return ResponseEntity.ok().build();


        } catch (Exception e) {
            try {
                String errorRedirectUrl = "http://localhost:3000/payment-result?status=error&message=" +
                        URLEncoder.encode("Lỗi xử lý thanh toán: " + e.getMessage(), StandardCharsets.UTF_8);
                response.sendRedirect(errorRedirectUrl);
            } catch (IOException ex) {
                return ResponseEntity.badRequest().body("Lỗi xử lý thanh toán");
            }
            return ResponseEntity.ok().build();
        }
    }


    @GetMapping("/{id}/lich-su-thanh-toan")
    public ResponseEntity<?> getLichSuThanhToan(@PathVariable Integer id) {
        try {
            List<LichSuThanhToan> lichSuThanhToan = lichSuThanhToanRepository.findByHoaDonIdOrderByNgayThanhToanDesc(id);

            List<Map<String, Object>> response = lichSuThanhToan.stream()
                    .map(ls -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", ls.getId());
                        map.put("soTien", ls.getSoTien());
                        map.put("ghiChu", ls.getGhiChu());
                        map.put("ngayThanhToan", ls.getNgayThanhToan());
                        map.put("trangThai", ls.getTrangThai());
                        map.put("maGiaoDich", ls.getMaGiaoDich());

                        if (ls.getPhuongThucThanhToan() != null) {
                            Map<String, Object> ptMap = new HashMap<>();
                            ptMap.put("id", ls.getPhuongThucThanhToan().getId());
                            ptMap.put("ten", ls.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
                            ptMap.put("ma", ls.getPhuongThucThanhToan().getMaPhuongThucThanhToan());
                            map.put("phuongThucThanhToan", ptMap);
                        }

                        return map;
                    })
                    .collect(Collectors.toList());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi lấy lịch sử thanh toán: " + e.getMessage());
        }
    }


    @DeleteMapping("/{idHoaDon}/chi-tiet/{idChiTietSanPham}")
    public ResponseEntity<?> xoaChiTietSanPhamKhoiHoaDon(
            @PathVariable Integer idHoaDon,
            @PathVariable Integer idChiTietSanPham) {
        try {
            hoaDonService.xoaChiTietSanPhamKhoiHoaDon(idHoaDon, idChiTietSanPham);


            // [THÊM] Gửi socket khi xóa món
            syncToCustomerDisplay(idHoaDon);


            return ResponseEntity.ok().body(Map.of(
                    "success", true,
                    "message", "Đã xóa sản phẩm khỏi hóa đơn thành công"
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống khi xóa sản phẩm"
            ));
        }
    }
    @PostMapping("/{id}/hoan-tien")
    public ResponseEntity<?> hoanTienHoaDon(
            @PathVariable Integer id,
            @RequestBody HoanTienRequest request) {
        try {
            HoanTienResponse response = hoaDonService.hoanTienHoaDon(id, request);

            // Gửi socket thông báo
            messagingTemplate.convertAndSend("/topic/hoa-don-hoan-tien", Map.of(
                    "hoaDonId", id,
                    "soTienHoan", response.getSoTienHoan(),
                    "ngayHoanTien", response.getNgayHoanTien(),
                    "message", "Hóa đơn #" + id + " đã được hoàn tiền"
            ));

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống khi xử lý hoàn tiền"
            ));
        }
    }

    /**
     * API kiểm tra điều kiện hoàn tiền
     * GET /api/hoa-don/{id}/kiem-tra-hoan-tien
     */
    @GetMapping("/{id}/kiem-tra-hoan-tien")
    public ResponseEntity<?> kiemTraHoanTien(@PathVariable Integer id) {
        try {
            // Lấy thông tin hóa đơn
            HoaDon hoaDon = hoaDonRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

            // Lấy lịch sử thanh toán
            List<LichSuThanhToan> lichSuThanhToan = lichSuThanhToanRepository
                    .findByHoaDonIdOrderByNgayThanhToanDesc(id);

            // Kiểm tra đã hoàn tiền chưa
            boolean daHoanTien = lichSuThanhToan.stream()
                    .anyMatch(ls -> ls.getGhiChu() != null && ls.getGhiChu().contains("[HOÀN TIỀN]"));

            // Tính thời gian từ ngày thanh toán
            long soNgayTruocKhiHoanTien = 0;
            if (hoaDon.getNgayThanhToan() != null) {
                try {
                    LocalDate ngayThanhToan = hoaDon.getNgayThanhToan().toInstant()
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate();
                    soNgayTruocKhiHoanTien = ChronoUnit.DAYS.between(
                            ngayThanhToan, LocalDate.now()
                    );
                } catch (Exception e) {
                    System.err.println("⚠️ Lỗi tính ngày hoàn tiền: " + e.getMessage());
                    // Nếu có lỗi tính ngày, coi như chưa quá hạn
                    soNgayTruocKhiHoanTien = 0;
                }
            }

            // Điều kiện hoàn tiền: chỉ khi trạng thái là 4 (đã hủy)
            boolean coTheHoanTien = !daHoanTien
                    && hoaDon.getTrangThai() != null
                    && hoaDon.getTrangThai() == 4
                    && soNgayTruocKhiHoanTien <= 30;

            // Lấy lịch sử thanh toán gần nhất để biết số tiền có thể hoàn
            BigDecimal soTienCoTheHoan = BigDecimal.ZERO;
            if (!lichSuThanhToan.isEmpty()) {
                for (LichSuThanhToan ls : lichSuThanhToan) {
                    // Chỉ lấy lịch sử thanh toán không phải hoàn tiền
                    if (ls.getGhiChu() != null && !ls.getGhiChu().contains("[HOÀN TIỀN]") && ls.getSoTien() != null) {
                        soTienCoTheHoan = ls.getSoTien();
                        break;
                    }
                }
            }

            // Nếu không tìm thấy lịch sử thanh toán hợp lệ, thử lấy từ hóa đơn
            if (soTienCoTheHoan.compareTo(BigDecimal.ZERO) == 0 && hoaDon.getTongTienSauGiam() != null) {
                soTienCoTheHoan = hoaDon.getTongTienSauGiam();
            }

            // Chuẩn bị thông tin hóa đơn
            Map<String, Object> hoaDonInfo = new HashMap<>();
            hoaDonInfo.put("id", hoaDon.getId());
            hoaDonInfo.put("maHoaDon", hoaDon.getMaHoaDon());
            hoaDonInfo.put("tongTienSauGiam", hoaDon.getTongTienSauGiam());
            hoaDonInfo.put("trangThai", hoaDon.getTrangThai());
            hoaDonInfo.put("ngayThanhToan", hoaDon.getNgayThanhToan());
            hoaDonInfo.put("trangThaiText", getTrangThaiText(hoaDon.getTrangThai()));

            // Chuẩn bị danh sách lịch sử thanh toán
            List<Map<String, Object>> lichSuThanhToanResponse = lichSuThanhToan.stream()
                    .map(ls -> {
                        Map<String, Object> lsMap = new HashMap<>();
                        lsMap.put("id", ls.getId());
                        lsMap.put("soTien", ls.getSoTien());
                        lsMap.put("ghiChu", ls.getGhiChu());
                        lsMap.put("ngayThanhToan", ls.getNgayThanhToan());
                        lsMap.put("trangThai", ls.getTrangThai());

                        // Thông tin phương thức thanh toán
                        if (ls.getPhuongThucThanhToan() != null) {
                            lsMap.put("phuongThuc", ls.getPhuongThucThanhToan().getTenPhuongThucThanhToan());
                        } else {
                            lsMap.put("phuongThuc", "Không xác định");
                        }

                        lsMap.put("laHoanTien", ls.getGhiChu() != null && ls.getGhiChu().contains("[HOÀN TIỀN]"));
                        return lsMap;
                    })
                    .collect(Collectors.toList());

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("coTheHoanTien", coTheHoanTien);
            response.put("daHoanTien", daHoanTien);
            response.put("hoaDon", hoaDonInfo);
            response.put("soTienCoTheHoan", soTienCoTheHoan);
            response.put("soNgayTruocKhiHoanTien", soNgayTruocKhiHoanTien);
            response.put("thoiGianConLai", 30 - soNgayTruocKhiHoanTien);
            response.put("lyDoKhongTheHoanTien", !coTheHoanTien ?
                    getLyDoKhongTheHoanTien(daHoanTien, hoaDon.getTrangThai(), soNgayTruocKhiHoanTien) : null);
            response.put("lichSuThanhToan", lichSuThanhToanResponse);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace(); // In ra lỗi để debug
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống khi kiểm tra điều kiện hoàn tiền: " + e.getMessage()
            ));
        }
    }

    /**
     * API lấy lịch sử hoàn tiền của hóa đơn
     * GET /api/hoa-don/{id}/lich-su-hoan-tien
     */
    @GetMapping("/{id}/lich-su-hoan-tien")
    public ResponseEntity<?> getLichSuHoanTien(@PathVariable Integer id) {
        try {
            // Lấy tất cả lịch sử thanh toán
            List<LichSuThanhToan> allLichSu = lichSuThanhToanRepository.findByHoaDonIdOrderByNgayThanhToanDesc(id);

            // Lọc ra các lần hoàn tiền
            List<Map<String, Object>> lichSuHoanTien = allLichSu.stream()
                    .filter(ls -> ls.getGhiChu() != null && ls.getGhiChu().contains("[HOÀN TIỀN]"))
                    .map(ls -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", ls.getId());
                        map.put("soTien", ls.getSoTien());
                        map.put("ghiChu", ls.getGhiChu());
                        map.put("ngayThanhToan", ls.getNgayThanhToan());
                        map.put("trangThai", ls.getTrangThai());

                        // Parse thông tin từ ghi chú
                        String ghiChu = ls.getGhiChu();
                        map.put("laHoanTien", true);

                        // Trích xuất lý do từ ghi chú
                        if (ghiChu.contains(" - ")) {
                            String[] parts = ghiChu.split(" - ");
                            if (parts.length > 0) {
                                String lyDo = parts[0].replace("[HOÀN TIỀN] ", "");
                                map.put("lyDo", lyDo);
                            }
                            if (parts.length > 1) {
                                map.put("chiTiet", parts[1]);
                            }
                        }

                        // Thông tin phương thức thanh toán
                        if (ls.getPhuongThucThanhToan() != null) {
                            map.put("phuongThucThanhToan", Map.of(
                                    "id", ls.getPhuongThucThanhToan().getId(),
                                    "ten", ls.getPhuongThucThanhToan().getTenPhuongThucThanhToan(),
                                    "ma", ls.getPhuongThucThanhToan().getMaPhuongThucThanhToan()
                            ));
                        }

                        return map;
                    })
                    .collect(Collectors.toList());

            // Thông tin tổng hợp
            BigDecimal tongSoTienDaHoan = lichSuHoanTien.stream()
                    .map(ls -> (BigDecimal) ls.get("soTien"))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            Map<String, Object> response = Map.of(
                    "success", true,
                    "tongSoLanHoanTien", lichSuHoanTien.size(),
                    "tongSoTienDaHoan", tongSoTienDaHoan,
                    "lichSuHoanTien", lichSuHoanTien,
                    "coLichSuHoanTien", !lichSuHoanTien.isEmpty()
            );

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống khi lấy lịch sử hoàn tiền"
            ));
        }
    }

    /**
     * API lấy danh sách lý do hoàn tiền mẫu
     * GET /api/hoa-don/ly-do-hoan-tien-mau
     */
    @GetMapping("/ly-do-hoan-tien-mau")
    public ResponseEntity<?> getLyDoHoanTienMau() {
        try {
            List<Map<String, String>> lyDoMau = new ArrayList<>();

            lyDoMau.add(Map.of(
                    "id", "1",
                    "ten", "Khách hàng hủy đơn hàng",
                    "moTa", "Khách hàng yêu cầu hủy đơn hàng sau khi thanh toán"
            ));

            lyDoMau.add(Map.of(
                    "id", "2",
                    "ten", "Sản phẩm không đúng mô tả",
                    "moTa", "Khách hàng nhận hàng không đúng với mô tả trên website"
            ));

            lyDoMau.add(Map.of(
                    "id", "3",
                    "ten", "Sản phẩm bị lỗi/hỏng",
                    "moTa", "Sản phẩm bị lỗi kỹ thuật hoặc hư hỏng trong quá trình vận chuyển"
            ));

            lyDoMau.add(Map.of(
                    "id", "4",
                    "ten", "Không giao hàng được",
                    "moTa", "Đơn hàng không thể giao đến địa chỉ của khách hàng"
            ));

            lyDoMau.add(Map.of(
                    "id", "5",
                    "ten", "Khách hàng đổi ý",
                    "moTa", "Khách hàng thay đổi quyết định mua hàng"
            ));

            lyDoMau.add(Map.of(
                    "id", "6",
                    "ten", "Sai số lượng/loại sản phẩm",
                    "moTa", "Giao sai số lượng hoặc loại sản phẩm so với đơn đặt hàng"
            ));

            lyDoMau.add(Map.of(
                    "id", "7",
                    "ten", "Thanh toán nhầm/sai số tiền",
                    "moTa", "Khách hàng thanh toán nhầm hoặc sai số tiền"
            ));

            lyDoMau.add(Map.of(
                    "id", "8",
                    "ten", "Lý do khác",
                    "moTa", "Các lý do hoàn tiền khác"
            ));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "data", lyDoMau
            ));

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "success", false,
                    "message", "Lỗi hệ thống khi lấy danh sách lý do mẫu"
            ));
        }
    }

    /**
     * API xuất báo cáo hoàn tiền
     * GET /api/hoa-don/bao-cao-hoan-tien
     */
    @GetMapping("/bao-cao-hoan-tien")
    public void exportBaoCaoHoanTien(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate tuNgay,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate denNgay,
            HttpServletResponse response) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");

            String fileName = "BaoCaoHoanTien_" + System.currentTimeMillis() + ".xlsx";
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            // Lấy danh sách hóa đơn đã hoàn tiền
            List<HoaDon> hoaDonList = hoaDonRepository.findAll();
            List<HoaDon> hoaDonDaHoanTien = new ArrayList<>();

            for (HoaDon hd : hoaDonList) {
                boolean daHoanTien = lichSuThanhToanRepository.existsByHoaDonIdAndGhiChuContaining(
                        hd.getId(), "[HOÀN TIỀN]"
                );
                if (daHoanTien) {
                    // Lọc theo ngày nếu có
                    if (tuNgay != null && denNgay != null) {
                        LocalDate ngayHoaDon = hd.getNgayTao().toInstant()
                                .atZone(java.time.ZoneId.systemDefault())
                                .toLocalDate();
                        if (!(ngayHoaDon.isEqual(tuNgay) || ngayHoaDon.isAfter(tuNgay)) ||
                                !(ngayHoaDon.isEqual(denNgay) || ngayHoaDon.isBefore(denNgay))) {
                            continue;
                        }
                    }
                    hoaDonDaHoanTien.add(hd);
                }
            }

            try (Workbook workbook = new XSSFWorkbook()) {
                Sheet sheet = workbook.createSheet("Báo cáo hoàn tiền");

                // Tạo style cho header
                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerFont.setColor(IndexedColors.WHITE.getIndex());
                headerStyle.setFont(headerFont);
                headerStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
                headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                headerStyle.setAlignment(HorizontalAlignment.CENTER);

                // Tạo style cho currency
                CellStyle currencyStyle = workbook.createCellStyle();
                currencyStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0"));

                // Tạo header
                Row headerRow = sheet.createRow(0);
                String[] columns = {
                        "STT", "Mã hóa đơn", "Khách hàng", "Nhân viên",
                        "Ngày hoàn tiền", "Số tiền hoàn", "Phương thức hoàn tiền",
                        "Lý do hoàn tiền", "Trạng thái hóa đơn"
                };

                for (int i = 0; i < columns.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(columns[i]);
                    cell.setCellStyle(headerStyle);
                }

                // Điền dữ liệu
                int rowNum = 1;
                BigDecimal tongTienHoan = BigDecimal.ZERO;

                for (HoaDon hd : hoaDonDaHoanTien) {
                    // Lấy lịch sử hoàn tiền của hóa đơn này
                    List<LichSuThanhToan> lichSuHoanTien = lichSuThanhToanRepository
                            .findByHoaDonIdAndGhiChuContainingOrderByNgayThanhToanDesc(
                                    hd.getId(), "[HOÀN TIỀN]"
                            );

                    for (LichSuThanhToan ls : lichSuHoanTien) {
                        Row row = sheet.createRow(rowNum++);

                        // STT
                        row.createCell(0).setCellValue(rowNum - 1);

                        // Mã hóa đơn
                        row.createCell(1).setCellValue(hd.getMaHoaDon());

                        // Khách hàng
                        row.createCell(2).setCellValue(
                                hd.getKhachHang() != null ? hd.getKhachHang().getHoTen() : "Khách lẻ"
                        );

                        // Nhân viên
                        row.createCell(3).setCellValue(
                                hd.getNhanVien() != null ? hd.getNhanVien().getHoTen() : ""
                        );

                        // Ngày hoàn tiền
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        row.createCell(4).setCellValue(sdf.format(ls.getNgayThanhToan()));

                        // Số tiền hoàn
                        Cell cellTien = row.createCell(5);
                        cellTien.setCellValue(ls.getSoTien().doubleValue());
                        cellTien.setCellStyle(currencyStyle);
                        tongTienHoan = tongTienHoan.add(ls.getSoTien());

                        // Phương thức hoàn tiền
                        String phuongThuc = ls.getPhuongThucThanhToan() != null ?
                                ls.getPhuongThucThanhToan().getTenPhuongThucThanhToan() : "Không xác định";
                        row.createCell(6).setCellValue(phuongThuc);

                        // Lý do hoàn tiền (lấy từ ghi chú)
                        String ghiChu = ls.getGhiChu();
                        String lyDo = "";
                        if (ghiChu != null && ghiChu.contains(" - ")) {
                            String[] parts = ghiChu.split(" - ");
                            if (parts.length > 0) {
                                lyDo = parts[0].replace("[HOÀN TIỀN] ", "");
                            }
                        }
                        row.createCell(7).setCellValue(lyDo);

                        // Trạng thái hóa đơn
                        row.createCell(8).setCellValue(getTrangThaiText(hd.getTrangThai()));
                    }
                }

                // Thêm dòng tổng kết
                Row totalRow = sheet.createRow(rowNum);
                totalRow.createCell(0).setCellValue("TỔNG CỘNG");
                for (int i = 1; i < 5; i++) {
                    totalRow.createCell(i).setCellValue("");
                }
                Cell cellTongTien = totalRow.createCell(5);
                cellTongTien.setCellValue(tongTienHoan.doubleValue());
                cellTongTien.setCellStyle(currencyStyle);

                // Auto size columns
                for (int i = 0; i < columns.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(response.getOutputStream());
                response.getOutputStream().flush();

            } catch (Exception e) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Lỗi xuất báo cáo: " + e.getMessage());
            }

        } catch (Exception e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                        "Lỗi xuất file Excel: " + e.getMessage());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
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

    private String getLyDoKhongTheHoanTien(boolean daHoanTien, Integer trangThai, long soNgay) {
        if (daHoanTien) {
            return "Hóa đơn này đã được hoàn tiền trước đó";
        }
        if (trangThai != 4) {
            return "Chỉ có thể hoàn tiền cho hóa đơn đã hủy";
        }
        if (soNgay > 30) {
            return "Đã quá 30 ngày kể từ ngày thanh toán, không thể hoàn tiền";
        }
        // Kiểm tra đã thanh toán chưa
        boolean daThanhToan = trangThai != null && (trangThai == 3 || trangThai == 4);
        if (!daThanhToan) {
            return "Chỉ có thể hoàn tiền cho đơn hàng đã thanh toán";
        }
        return "Có thể hoàn tiền";
    }

    @PostMapping("/tao-hoa-don-rong")
        public ResponseObject<?> addHoaDonRong(@RequestBody HoaDonRequest request) {
            HoaDon hoaDon = hoaDonService.addHoaDon(request);
            return new ResponseObject<>(hoaDon, "Thêm thành công");
    }

    @GetMapping(value = "/hoa-don-cho", params = "trangThai")
    public ResponseObject<List<HoaDon>> getHoaDonCho(@RequestParam Integer trangThai) {
        List<HoaDon> list = hoaDonService.getHoaDonTheoTrangThai(trangThai);
        return new ResponseObject<>(list, "Lấy danh sách hóa đơn thành công");
    }

    @DeleteMapping("/{id}")
    public ResponseObject<Void> deleteHoaDon(@PathVariable Integer id) {
        hoaDonService.deleteHoaDon(id);
        return new ResponseObject<>(null, "Xóa hóa đơn thành công");
    }

    @PutMapping("/update-hoa-don/{id}")
    public ResponseObject<?> updateHoaDon(@PathVariable Integer id, @RequestBody HoaDonRequest request) {
        hoaDonService.updateHoaDon(id, request);
        return new ResponseObject<>(null, "Update hóa đơn thành công");
    }

}

