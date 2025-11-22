package com.example.the_autumn.service;

import com.example.the_autumn.dto.HoaDonDTO;
import com.example.the_autumn.service.VnPayService;
import com.example.the_autumn.entity.*;
import com.example.the_autumn.model.request.OrderRequest;
import com.example.the_autumn.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderService {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired private HoaDonRepository hoaDonRepo;
    @Autowired private HoaDonChiTietRepository hoaDonChiTietRepo;
    @Autowired private ChiTietSanPhamRepository chiTietSanPhamRepo;
    @Autowired private KhachHangRepository khachHangRepo;
    @Autowired private PhuongThucThanhToanRepository ptttRepo;
    @Autowired private HinhThucThanhToanRepository htttRepo;
    @Autowired private DiaChiRepository diaChiRepo;
    @Autowired private LichSuThanhToanRepository lichSuThanhToanRepo;
    @Autowired private LichSuHoaDonRepository lichSuHoaDonRepo;
    @Autowired private NhanVienRepository nhanVienRepo;
    @Autowired private PhieuGiamGiaRepository phieuGiamGiaRepo;
    @Autowired private TinhThanhRepository tinhThanhRepository;
    @Autowired private QuanHuyenRepository quanHuyenRepository;
    @Autowired private VnPayService vnPayService;
    @Autowired private EmailTaoDonHangService emailTaoDonHangService;

    @Transactional(rollbackFor = Exception.class)
    public HoaDon placeOrder(OrderRequest request) {

        log.info("=== STARTING ORDER PLACEMENT ===");
        log.info("📦 Order request: {}", request);

        // === 1. XỬ LÝ KHÁCH HÀNG ===
        KhachHang khachHang;
        String customerEmail = request.getEmail();

        if (request.getKhachHangId() != null && request.getKhachHangId() > 0) {
            khachHang = khachHangRepo.findById(request.getKhachHangId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng ID: " + request.getKhachHangId()));
            log.info("👤 Customer found: ID={}, Name={}", khachHang.getId(), khachHang.getHoTen());
            customerEmail = khachHang.getEmail();
        } else {
            Optional<KhachHang> existingKhach = khachHangRepo.findBySdt(request.getSdt());
            if (existingKhach.isPresent()) {
                khachHang = existingKhach.get();
                log.info("👤 Existing guest found by phone: {}", request.getSdt());

                if (customerEmail != null && !customerEmail.isEmpty() && !customerEmail.equals(khachHang.getEmail())) {
                    khachHang.setEmail(customerEmail);
                    khachHangRepo.save(khachHang);
                    log.info("📧 Updated customer email: {}", customerEmail);
                } else {
                    customerEmail = khachHang.getEmail();
                }
            } else {
                KhachHang newKhach = new KhachHang();
                newKhach.setHoTen(request.getHoTen());
                newKhach.setSdt(request.getSdt());

                if (customerEmail != null && !customerEmail.isEmpty()) {
                    newKhach.setEmail(customerEmail);
                } else {
                    newKhach.setEmail("guest_" + request.getSdt() + "@shop.com");
                    customerEmail = newKhach.getEmail();
                }

                newKhach.setNgayTao(new Date());
                newKhach.setTrangThai(true);
                khachHang = khachHangRepo.save(newKhach);
                log.info("👤 New guest created: {} with email: {}", khachHang.getHoTen(), customerEmail);
            }
        }

        // === 2. LƯU ĐỊA CHỈ ===
        if (request.getDiaChiKhachHang() != null && !request.getDiaChiKhachHang().isEmpty()) {
            Optional<DiaChi> existingDiaChi = diaChiRepo.findByKhachHangAndDiaChiCuThe(
                    khachHang,
                    request.getDiaChiKhachHang()
            );

            if (existingDiaChi.isEmpty()) {
                DiaChi newDiaChi = new DiaChi();
                newDiaChi.setKhachHang(khachHang);

                if (request.getTinhId() != null) {
                    TinhThanh tinhThanh = tinhThanhRepository.findById(request.getTinhId())
                            .orElse(null);
                    newDiaChi.setTinhThanh(tinhThanh);
                }

                if (request.getQuanId() != null) {
                    QuanHuyen quanHuyen = quanHuyenRepository.findById(request.getQuanId())
                            .orElse(null);
                    newDiaChi.setQuanHuyen(quanHuyen);
                }

                newDiaChi.setDiaChiCuThe(request.getDiaChiKhachHang());
                newDiaChi.setTenDiaChi("Địa chỉ giao hàng");
                newDiaChi.setTrangThai(true);
                diaChiRepo.save(newDiaChi);
                log.info("📍 Address saved with Tinh ID: {}, Quan ID: {}",
                        request.getTinhId(), request.getQuanId());
            }
        }

        // === 3. XỬ LÝ MÃ GIẢM GIÁ ===
        PhieuGiamGia phieuGiamGia = null;
        BigDecimal tienGiam = BigDecimal.ZERO;
        if (request.getPhieuGiamGiaId() != null && request.getPhieuGiamGiaId() > 0) {
            log.info("🎫 Processing discount code ID: {}", request.getPhieuGiamGiaId());

            phieuGiamGia = phieuGiamGiaRepo.findById(request.getPhieuGiamGiaId())
                    .orElseThrow(() -> new RuntimeException("Mã giảm giá không tồn tại"));

            if (phieuGiamGia.getTrangThai() != 1) {
                throw new RuntimeException("Mã giảm giá không còn hiệu lực");
            }

            if (phieuGiamGia.getSoLuongDung() <= 0) {
                throw new RuntimeException("Mã giảm giá đã hết số lượng");
            }

            if (request.getTienGiam() != null && request.getTienGiam().compareTo(BigDecimal.ZERO) > 0) {
                tienGiam = request.getTienGiam();
                log.info("💰 Discount amount: {}", tienGiam);
            } else {
                log.warn("⚠️ Voucher ID provided but tienGiam is 0 or null - setting to 0");
                tienGiam = BigDecimal.ZERO;
            }

            log.info("✅ Discount code applied: {} (Current quantity: {})",
                    phieuGiamGia.getTenChuongTrinh(),
                    phieuGiamGia.getSoLuongDung());
        } else {
            tienGiam = BigDecimal.ZERO;
            log.info("ℹ️ No discount code applied - tienGiam set to 0");
        }

        // === 4. KIỂM TRA PHƯƠNG THỨC THANH TOÁN ===
        PhuongThucThanhToan pttt = ptttRepo.findByTenPhuongThucThanhToan(request.getPaymentMethod())
                .orElseThrow(() -> new RuntimeException("Phương thức thanh toán không hợp lệ"));

        // === 5. KIỂM TRA VÀ TRỪ TỒN KHO ===
        List<ChiTietSanPham> updatedCtspList = new ArrayList<>();
        for (OrderRequest.CartItemDTO item : request.getItems()) {
            ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(item.getId())
                    .orElseThrow(() -> new RuntimeException("Sản phẩm ID " + item.getId() + " không tồn tại"));

            if (ctsp.getSoLuongTon() < item.getQuantity()) {
                throw new RuntimeException("Sản phẩm '" + ctsp.getSanPham().getTenSanPham() + "' không đủ hàng (Tồn: " + ctsp.getSoLuongTon() + ")");
            }
            ctsp.setSoLuongTon(ctsp.getSoLuongTon() - item.getQuantity());
            updatedCtspList.add(ctsp);
        }

        // === 6. LẤY NHÂN VIÊN MẶC ĐỊNH ===
        NhanVien defaultEmployee = nhanVienRepo.findById(1)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên mặc định (ID=1)"));

        // === 7. TẠO HÓA ĐƠN ===
        HoaDon hd = new HoaDon();
        hd.setKhachHang(khachHang);
        hd.setNhanVien(defaultEmployee);
        hd.setDiaChiKhachHang(request.getDiaChiKhachHang());

        BigDecimal tongTienGoc = request.getTongTien();
        BigDecimal tongTienSauGiam = tongTienGoc.subtract(tienGiam);

        hd.setTongTien(tongTienGoc);
        hd.setTongTienSauGiam(tongTienSauGiam);
        hd.setPhiVanChuyen(request.getPhiVanChuyen());

        hd.setNgayTao(new Date());
        hd.setTrangThai(0);
        hd.setLoaiHoaDon(false);
        hd.setNguoiTao(defaultEmployee.getId());

        if (phieuGiamGia != null) {
            hd.setPhieuGiamGia(phieuGiamGia);
            log.info("🎫 Discount code saved: {} (ID: {})",
                    phieuGiamGia.getTenChuongTrinh(),
                    phieuGiamGia.getId());
        } else {
            hd.setPhieuGiamGia(null);
            log.info("ℹ️ No discount code - PhieuGiamGia set to null");
        }

        HoaDon savedHoaDon = hoaDonRepo.save(hd);
        hoaDonRepo.flush();
        entityManager.refresh(savedHoaDon);

        log.info("📋 Order created: {}", savedHoaDon.getMaHoaDon());
        log.info("💵 Original: {} | After discount: {} | Discount saved: {}",
                tongTienGoc, tongTienSauGiam, tienGiam);

        // === 8. TẠO HÓA ĐƠN CHI TIẾT ===
        for (int i = 0; i < request.getItems().size(); i++) {
            OrderRequest.CartItemDTO item = request.getItems().get(i);
            ChiTietSanPham ctsp = updatedCtspList.get(i);

            HoaDonChiTiet hdct = new HoaDonChiTiet();
            hdct.setHoaDon(savedHoaDon);
            hdct.setChiTietSanPham(ctsp);
            hdct.setSoLuong(item.getQuantity());
            hdct.setGiaBan(ctsp.getGiaBan());
            hdct.setThanhTien(ctsp.getGiaBan().multiply(BigDecimal.valueOf(item.getQuantity())));
            hdct.setTrangThai(true);
            hoaDonChiTietRepo.save(hdct);
        }

        // === 9. LƯU LẠI TỒN KHO ===
        chiTietSanPhamRepo.saveAll(updatedCtspList);

        // === 10. TẠO HÌNH THỨC THANH TOÁN ===
        HinhThucThanhToan httt = new HinhThucThanhToan();
        httt.setHoaDon(savedHoaDon);
        httt.setPhuongThucThanhToan(pttt);

        String tenPTTT = pttt.getTenPhuongThucThanhToan();

        boolean daThanhToan = !tenPTTT.equalsIgnoreCase("Tiền mặt");
        httt.setLoaiThanhToan(daThanhToan);
        httt.setTrangThai(true);
        htttRepo.save(httt);

        // === 11. TẠO LỊCH SỬ THANH TOÁN ===
        if (tenPTTT.equalsIgnoreCase("Tiền mặt")) {
            // COD - Chờ thanh toán khi nhận hàng
            LichSuThanhToan lstt = new LichSuThanhToan();
            lstt.setHoaDon(savedHoaDon);
            lstt.setPhuongThucThanhToan(pttt);
            lstt.setSoTien(savedHoaDon.getTongTienSauGiam());
            lstt.setNgayThanhToan(new Date());
            lstt.setGhiChu("Chờ thanh toán khi nhận hàng (COD)");
            lstt.setTrangThai(false); // false = chưa thanh toán
            lichSuThanhToanRepo.save(lstt);
            log.info("💳 Created PENDING payment history for COD");

        } else if (tenPTTT.equalsIgnoreCase("Chuyển khoản")) {
            // ⭐ VNPAY - TẠO LỊCH SỬ PENDING NGAY (giống COD)
            LichSuThanhToan lstt = new LichSuThanhToan();
            lstt.setHoaDon(savedHoaDon);
            lstt.setPhuongThucThanhToan(pttt);
            lstt.setSoTien(savedHoaDon.getTongTienSauGiam());
            lstt.setNgayThanhToan(new Date());
            lstt.setGhiChu("Đang chờ thanh toán VNPAY - Đơn hàng: " + savedHoaDon.getMaHoaDon());
            lstt.setTrangThai(false); // false = chưa thanh toán
            LichSuThanhToan savedLSTT = lichSuThanhToanRepo.save(lstt);
            lichSuThanhToanRepo.flush();

            // ⭐ LƯU ID VÀO VnPayService
            vnPayService.savePendingPaymentId(savedHoaDon.getMaHoaDon(), savedLSTT.getId());
            log.info("💳 Created PENDING payment history for VNPAY");

        } else {
            // Thanh toán trực tiếp (Quẹt thẻ tại quầy, v.v.)
            LichSuThanhToan lstt = new LichSuThanhToan();
            lstt.setHoaDon(savedHoaDon);
            lstt.setPhuongThucThanhToan(pttt);
            lstt.setSoTien(savedHoaDon.getTongTienSauGiam());
            lstt.setNgayThanhToan(new Date());
            lstt.setGhiChu("Thanh toán khi tạo đơn hàng (POS/Online)");
            lstt.setTrangThai(true); // true = đã thanh toán
            lichSuThanhToanRepo.save(lstt);
            log.info("✅ Created COMPLETED payment history for Pre-paid");

            savedHoaDon.setNgayThanhToan(new Date());
            hoaDonRepo.save(savedHoaDon);
        }

        // === 12. TẠO LỊCH SỬ HÓA ĐƠN ===
        LichSuHoaDon lshd = new LichSuHoaDon();
        lshd.setHoaDon(savedHoaDon);
        lshd.setKhachHang(khachHang);
        lshd.setNhanVien(defaultEmployee);
        lshd.setHanhDong("Tạo hóa đơn");

        String moTa = "Khách hàng tạo đơn hàng online.";
        if (phieuGiamGia != null && tienGiam.compareTo(BigDecimal.ZERO) > 0) {
            moTa += " Áp dụng mã: " + phieuGiamGia.getTenChuongTrinh() + " (-" + tienGiam + "đ)";
        }
        lshd.setMoTa(moTa);
        lshd.setNgayCapNhat(new Date());
        lshd.setTrangThai(true);
        lichSuHoaDonRepo.save(lshd);

        // === 13. GỬI EMAIL XÁC NHẬN ===
        // === 13. GỬI EMAIL XÁC NHẬN ===
        if (customerEmail != null && !customerEmail.isEmpty() && !customerEmail.startsWith("guest_")) {
            try {
                // ⭐ EAGER LOAD tất cả dữ liệu cần thiết TRƯỚC KHI gọi async
                entityManager.refresh(savedHoaDon);
                if (savedHoaDon.getHoaDonChiTiets() != null) {
                    savedHoaDon.getHoaDonChiTiets().forEach(hdct -> {
                        // Force load ChiTietSanPham
                        hdct.getChiTietSanPham().getId();
                        // Force load SanPham
                        hdct.getChiTietSanPham().getSanPham().getTenSanPham();
                        // Force load KichThuoc
                        hdct.getChiTietSanPham().getKichThuoc().getTenKichThuoc();
                    });
                }

                emailTaoDonHangService.sendOrderConfirmationEmail(savedHoaDon, customerEmail);
                log.info("📧 Order confirmation email queued for: {}", customerEmail);
            } catch (Exception e) {
                log.error("❌ Failed to send email, but order was created successfully: {}", e.getMessage());
            }
        } else {
            log.info("ℹ️ Email not sent - invalid or guest email: {}", customerEmail);
        }

        log.info("=== ORDER PLACEMENT COMPLETED ===");
        return savedHoaDon;
    }

    @Transactional(readOnly = true)
    public List<HoaDonDTO> getOrdersByCustomerId(Integer customerId) {
        if (customerId == null) {
            log.warn("⚠️ customerId null");
            return new ArrayList<>();
        }
        KhachHang kh = khachHangRepo.findById(customerId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng"));
        log.info("📦 Lấy đơn hàng cho khách hàng ID: {}", kh.getId());
        List<HoaDon> orders = hoaDonRepo.findByKhachHangIdOrderByNgayTaoDesc(kh.getId());
        log.info("✅ Tìm thấy {} đơn hàng", orders.size());
        return orders.stream()
                .map(HoaDonDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<HoaDonDTO> getOrdersByMaHoaDonList(List<String> maHoaDonList) {
        if (maHoaDonList == null || maHoaDonList.isEmpty()) {
            log.warn("⚠️ Danh sách mã hóa đơn trống");
            return new ArrayList<>();
        }

        log.info("📋 Lấy {} hóa đơn theo mã", maHoaDonList.size());
        List<HoaDon> orders = hoaDonRepo.findByMaHoaDonInOrderByNgayTaoDesc(maHoaDonList);

        log.info("✅ Tìm thấy {} hóa đơn", orders.size());

        return orders.stream()
                .map(HoaDonDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public HoaDonDTO getOrderByMaHoaDon(String maHoaDon) {
        log.info("🔍 Lấy chi tiết hóa đơn: {}", maHoaDon);
        HoaDon hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn: " + maHoaDon));
        if (hoaDon.getHoaDonChiTiets() != null) {
            hoaDon.getHoaDonChiTiets().forEach(hdct -> {
                if (hdct.getChiTietSanPham() != null) {
                    if (hdct.getChiTietSanPham().getAnhs() != null) {
                        hdct.getChiTietSanPham().getAnhs().size();
                    }
                }
            });
        }
        HoaDonDTO dto = new HoaDonDTO(hoaDon);
        log.info("✅ Tìm thấy {} sản phẩm",
                dto.getHoaDonChiTiets() != null ? dto.getHoaDonChiTiets().size() : 0);
        return dto;
    }

    @Transactional(readOnly = true)
    public List<HoaDonDTO> getOrdersByPhone(String sdt) {
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new RuntimeException("Số điện thoại không hợp lệ");
        }
        log.info("📞 Tìm kiếm đơn hàng theo SĐT: {}", sdt);
        Optional<KhachHang> khachHangOpt = khachHangRepo.findBySdt(sdt);
        if (khachHangOpt.isEmpty()) {
            log.warn("⚠️ Không tìm thấy khách hàng với SĐT: {}", sdt);
            return new ArrayList<>();
        }
        KhachHang khachHang = khachHangOpt.get();
        log.info("👤 Tìm thấy khách hàng: {} (ID: {})", khachHang.getHoTen(), khachHang.getId());
        List<HoaDon> orders = hoaDonRepo.findByKhachHangIdOrderByNgayTaoDesc(khachHang.getId());
        log.info("✅ Tìm thấy {} đơn hàng", orders.size());
        return orders.stream()
                .map(HoaDonDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public HoaDonDTO cancelOrder(String maHoaDon, String reason) {
        log.info("🚫 Bắt đầu hủy đơn hàng: {}", maHoaDon);

        HoaDon hoaDon = hoaDonRepo.findByMaHoaDon(maHoaDon)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn: " + maHoaDon));

        if (hoaDon.getTrangThai() != 0) {
            String statusText;
            switch (hoaDon.getTrangThai()) {
                case 1: statusText = "Chờ giao hàng"; break;
                case 2: statusText = "Đang vận chuyển"; break;
                case 3: statusText = "Đã hoàn thành"; break;
                case 4: statusText = "Đã hủy"; break;
                default: statusText = "Không xác định";
            }
            throw new RuntimeException("Không thể hủy đơn hàng ở trạng thái: " + statusText);
        }

        List<HoaDonChiTiet> chiTiets = hoaDonChiTietRepo.findByHoaDonId(hoaDon.getId());
        for (HoaDonChiTiet hdct : chiTiets) {
            ChiTietSanPham ctsp = hdct.getChiTietSanPham();
            int soLuongHoan = hdct.getSoLuong();
            ctsp.setSoLuongTon(ctsp.getSoLuongTon() + soLuongHoan);
            chiTietSanPhamRepo.save(ctsp);
            log.info("📦 Hoàn {} sản phẩm '{}' vào kho (Tồn mới: {})",
                    soLuongHoan,
                    ctsp.getSanPham().getTenSanPham(),
                    ctsp.getSoLuongTon());
        }

        if (hoaDon.getPhieuGiamGia() != null) {
            log.info("ℹ️ Voucher {} không cần hoàn lại (chưa bị trừ)",
                    hoaDon.getPhieuGiamGia().getTenChuongTrinh());
        }

        hoaDon.setTrangThai(4);
        hoaDon.setNgaySua(new Date());
        hoaDonRepo.save(hoaDon);

        NhanVien defaultEmployee = nhanVienRepo.findById(1).orElse(null);

        LichSuHoaDon lichSu = new LichSuHoaDon();
        lichSu.setHoaDon(hoaDon);
        lichSu.setKhachHang(hoaDon.getKhachHang());
        lichSu.setNhanVien(defaultEmployee);
        lichSu.setHanhDong("Hủy đơn hàng");
        lichSu.setMoTa("Khách hàng hủy đơn. Lý do: " + (reason != null ? reason : "Không có lý do"));
        lichSu.setNgayCapNhat(new Date());
        lichSu.setTrangThai(true);
        lichSuHoaDonRepo.save(lichSu);

        String customerEmail = hoaDon.getKhachHang().getEmail();
        if (customerEmail != null && !customerEmail.isEmpty() && !customerEmail.startsWith("guest_")) {
            try {
                emailTaoDonHangService.sendOrderCancellationEmail(hoaDon, customerEmail, reason);
                log.info("📧 Cancellation email queued for: {}", customerEmail);
            } catch (Exception e) {
                log.error("❌ Failed to send cancellation email: {}", e.getMessage());
            }
        }

        log.info("✅ Đã hủy đơn hàng thành công: {}", maHoaDon);
        return new HoaDonDTO(hoaDon);
    }
}