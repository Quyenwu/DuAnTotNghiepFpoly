package com.example.the_autumn.service;

import com.example.the_autumn.config.VnPayConfig;
import com.example.the_autumn.entity.*;
import com.example.the_autumn.repository.*;
import com.example.the_autumn.service.EmailTaoDonHangService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class VnPayService {

    @Autowired
    private VnPayConfig vnPayConfig;

    @Autowired
    private HoaDonRepository hoaDonRepo;

    @Autowired
    private PhuongThucThanhToanRepository ptttRepo;

    @Autowired
    private LichSuThanhToanRepository lichSuThanhToanRepo;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepo;

    @Autowired
    private NhanVienRepository nhanVienRepo;

    @Autowired
    private EmailTaoDonHangService emailTaoDonHangService;

    // ⭐ MAP LƯU TẠM ID LỊCH SỬ THANH TOÁN THEO MÃ HÓA ĐƠN
    private final Map<String, Integer> pendingPaymentMap = new ConcurrentHashMap<>();
    public void savePendingPaymentId(String maHoaDon, Integer paymentHistoryId) {
        pendingPaymentMap.put(maHoaDon, paymentHistoryId);
        log.info("💾 Saved pending payment ID: {} for order: {}", paymentHistoryId, maHoaDon);
    }
    /**
     * TẠO PAYMENT URL
     */
    public String createPaymentUrl(HoaDon hoaDon, HttpServletRequest req) throws Exception {

        log.info("=== Creating VNPAY Payment URL ===");

        // ⭐ SỬ DỤNG TỔNG TIỀN SAU GIẢM
        long amount = hoaDon.getTongTienSauGiam()
                .multiply(BigDecimal.valueOf(100))
                .longValue();

        String vnp_TxnRef = hoaDon.getMaHoaDon() + "_" + System.currentTimeMillis();
        String vnp_OrderInfo = "Thanh toan don hang:" + hoaDon.getMaHoaDon();
        String vnp_IpAddr = VnPayConfig.getIpAddress(req);

        Calendar cld = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnp_CreateDate = formatter.format(cld.getTime());

        cld.add(Calendar.MINUTE, 15);
        String vnp_ExpireDate = formatter.format(cld.getTime());

        Map<String, String> vnp_Params = new HashMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", VnPayConfig.vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", vnp_OrderInfo);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnPayConfig.getVnpReturnUrl());
        vnp_Params.put("vnp_IpAddr", vnp_IpAddr);
        vnp_Params.put("vnp_CreateDate", vnp_CreateDate);
        vnp_Params.put("vnp_ExpireDate", vnp_ExpireDate);

        log.info("📦 Order: {}", hoaDon.getMaHoaDon());
        log.info("💰 Amount (after discount): {}", amount);

        // Sort params
        List<String> fieldNames = new ArrayList<>(vnp_Params.keySet());
        Collections.sort(fieldNames);

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = itr.next();
            String fieldValue = vnp_Params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));

                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }

        String hashDataStr = hashData.toString();
        String queryUrl = query.toString();

        log.info("📝 HashData (ENCODED): {}", hashDataStr);

        String vnp_SecureHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashDataStr);

        log.info("🔑 Secret Key: {}", VnPayConfig.vnp_HashSecret);
        log.info("🔐 SecureHash: {}", vnp_SecureHash);

        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        String paymentUrl = VnPayConfig.vnp_Url + "?" + queryUrl;

        log.info("🔗 Payment URL created");
        return paymentUrl;
    }

    /**
     * VALIDATE CHECKSUM
     */
    private boolean validateChecksum(Map<String, String> params, String secureHash) {
        try {
            if (secureHash == null || secureHash.isEmpty()) {
                log.error("❌ SecureHash is null");
                return false;
            }

            List<String> fieldNames = new ArrayList<>(params.keySet());
            Collections.sort(fieldNames);

            StringBuilder hashData = new StringBuilder();
            Iterator<String> itr = fieldNames.iterator();

            while (itr.hasNext()) {
                String fieldName = itr.next();
                String fieldValue = params.get(fieldName);

                if (fieldValue != null && !fieldValue.isEmpty()) {
                    hashData.append(fieldName);
                    hashData.append('=');
                    hashData.append(fieldValue);

                    if (itr.hasNext()) {
                        hashData.append('&');
                    }
                }
            }

            String hashDataStr = hashData.toString();
            log.info("📝 HashData for validation (RAW): {}", hashDataStr);

            String calculatedHash = VnPayConfig.hmacSHA512(VnPayConfig.vnp_HashSecret, hashDataStr);

            log.info("🔐 Hash from VNPAY: {}", secureHash);
            log.info("🔐 Hash calculated: {}", calculatedHash);

            boolean isValid = calculatedHash.equalsIgnoreCase(secureHash);

            if (isValid) {
                log.info("✅ Checksum VALID!");
            } else {
                log.error("❌ Checksum INVALID!");
            }

            return isValid;

        } catch (Exception e) {
            log.error("❌ Error: ", e);
            return false;
        }
    }

    /**
     * XỬ LÝ IPN CALLBACK
     */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, String> handleIpnResponse(Map<String, String> vnp_Params) {
        log.info("=== Processing VNPAY IPN ===");

        Map<String, String> response = new HashMap<>();

        try {
            String vnp_SecureHash = vnp_Params.remove("vnp_SecureHash");

            if (vnp_SecureHash == null) {
                log.error("❌ SecureHash is null");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            if (!validateChecksum(vnp_Params, vnp_SecureHash)) {
                log.error("❌ CHECKSUM VALIDATION FAILED!");
                response.put("RspCode", "97");
                response.put("Message", "Invalid Checksum");
                return response;
            }

            String vnp_TxnRef = vnp_Params.get("vnp_TxnRef");
            String vnp_ResponseCode = vnp_Params.get("vnp_ResponseCode");
            String vnp_TransactionStatus = vnp_Params.get("vnp_TransactionStatus");
            String vnp_TransactionNo = vnp_Params.get("vnp_TransactionNo");
            String vnp_Amount = vnp_Params.get("vnp_Amount");
            String vnp_BankCode = vnp_Params.get("vnp_BankCode");
            String vnp_PayDate = vnp_Params.get("vnp_PayDate");

            String maHoaDon = vnp_TxnRef;
            if (vnp_TxnRef != null && vnp_TxnRef.contains("_")) {
                maHoaDon = vnp_TxnRef.split("_")[0];
            }

            log.info("📦 Order: {}", maHoaDon);
            log.info("🔢 Response: {}", vnp_ResponseCode);

            Optional<HoaDon> hoaDonOpt = hoaDonRepo.findByMaHoaDon(maHoaDon);
            if (!hoaDonOpt.isPresent()) {
                log.error("❌ Order not found");
                response.put("RspCode", "01");
                response.put("Message", "Order not Found");
                return response;
            }

            HoaDon hoaDon = hoaDonOpt.get();

            // Kiểm tra đơn đã xử lý chưa
            if (hoaDon.getTrangThai() != null && hoaDon.getTrangThai() != 0 && hoaDon.getTrangThai() != 5) {
                log.warn("⚠️ Order already confirmed (status: {})", hoaDon.getTrangThai());
                response.put("RspCode", "02");
                response.put("Message", "Order already confirmed");
                return response;
            }

            // Kiểm tra số tiền
            if (vnp_Amount != null) {
                long amountFromVnpay = Long.parseLong(vnp_Amount) / 100;
                long amountFromOrder = hoaDon.getTongTienSauGiam().longValue();

                if (amountFromVnpay != amountFromOrder) {
                    log.error("❌ Invalid amount - Expected: {}, Got: {}", amountFromOrder, amountFromVnpay);
                    response.put("RspCode", "04");
                    response.put("Message", "Invalid Amount");
                    return response;
                }
            }

            if ("00".equals(vnp_ResponseCode) && "00".equals(vnp_TransactionStatus)) {
                // ✅ THANH TOÁN THÀNH CÔNG
                log.info("✅ Payment SUCCESS");

                // Cập nhật trạng thái đơn hàng
                hoaDon.setTrangThai(1);
                hoaDon.setNgayThanhToan(new Date());
                hoaDonRepo.save(hoaDon);

                // ⭐ CẬP NHẬT LỊCH SỬ THANH TOÁN TỪ PENDING → SUCCESS
                updatePaymentHistoryToSuccess(hoaDon, vnp_TransactionNo, vnp_BankCode, vnp_PayDate);

                // Lưu lịch sử hóa đơn
                createOrderHistory(hoaDon, "Thanh toán VNPAY thành công",
                        "Mã GD: " + vnp_TransactionNo + " - Ngân hàng: " + vnp_BankCode);

                // Gửi email
                sendPaymentSuccessEmail(hoaDon);

            } else {
                // ❌ THANH TOÁN THẤT BẠI
                log.warn("❌ Payment FAILED - Code: {}", vnp_ResponseCode);

                // Cập nhật trạng thái thất bại
                hoaDon.setTrangThai(5);
                hoaDonRepo.save(hoaDon);

                // ⭐ CẬP NHẬT LỊCH SỬ THANH TOÁN VẪN LÀ FALSE NHƯNG GHI CHÚ THẤT BẠI
                updatePaymentHistoryToFailed(hoaDon, vnp_ResponseCode, vnp_TransactionNo);

                // Lưu lịch sử hóa đơn
                String errorMsg = getVnpayErrorMessage(vnp_ResponseCode);
                createOrderHistory(hoaDon, "Thanh toán VNPAY thất bại",
                        "Mã lỗi: " + vnp_ResponseCode + " - " + errorMsg);
            }

            response.put("RspCode", "00");
            response.put("Message", "Confirm Success");

            log.info("=== End IPN ===");
            return response;

        } catch (Exception e) {
            log.error("❌ Error: ", e);
            response.put("RspCode", "99");
            response.put("Message", "Unknown error");
            return response;
        }
    }

    /**
     * ⭐ CẬP NHẬT LỊCH SỬ THANH TOÁN THÀNH CÔNG
     * LẤY TRỰC TIẾP ID TỪ MAP - KHÔNG CẦN TÌM KIẾM
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentHistoryToSuccess(HoaDon hoaDon, String transactionNo,
                                              String bankCode, String payDate) {
        try {
            String maHoaDon = hoaDon.getMaHoaDon();

            // ⭐ LẤY ID TỪ MAP
            Integer pendingPaymentId = pendingPaymentMap.get(maHoaDon);

            if (pendingPaymentId == null) {
                log.warn("⚠️ No pending payment ID found in map for order: {}", maHoaDon);
                // Fallback: tìm theo hoaDonId
                List<LichSuThanhToan> pendingList = lichSuThanhToanRepo.findByHoaDonIdAndTrangThai(hoaDon.getId(), false);
                if (!pendingList.isEmpty()) {
                    pendingPaymentId = pendingList.get(0).getId();
                    log.info("📋 Found pending payment by fallback search: ID = {}", pendingPaymentId);
                } else {
                    log.error("❌ Cannot find pending payment history");
                    createNewSuccessPaymentHistory(hoaDon, transactionNo, bankCode, payDate);
                    return;
                }
            }

            // ⭐ LẤY TRỰC TIẾP THEO ID
            Optional<LichSuThanhToan> lichSuOpt = lichSuThanhToanRepo.findById(pendingPaymentId);

            if (!lichSuOpt.isPresent()) {
                log.error("❌ Payment history not found with ID: {}", pendingPaymentId);
                return;
            }

            LichSuThanhToan lichSu = lichSuOpt.get();

            log.info("📝 Updating payment history ID: {} from status {} → true", lichSu.getId(), lichSu.getTrangThai());

            // ⭐ CẬP NHẬT TRẠNG THÁI VÀ GHI CHÚ
            lichSu.setTrangThai(true); // false → true
            lichSu.setNgayThanhToan(new Date());

            // Cập nhật ghi chú với thông tin chi tiết từ VNPAY
            StringBuilder ghiChu = new StringBuilder("VNPAY THÀNH CÔNG");
            if (transactionNo != null && !transactionNo.isEmpty()) {
                ghiChu.append(" - Mã GD: ").append(transactionNo);
            }
            if (bankCode != null && !bankCode.isEmpty()) {
                ghiChu.append(" - NH: ").append(bankCode);
            }
            if (payDate != null && !payDate.isEmpty()) {
                ghiChu.append(" - Ngày: ").append(formatPayDate(payDate));
            }

            lichSu.setGhiChu(ghiChu.toString());

            LichSuThanhToan saved = lichSuThanhToanRepo.save(lichSu);
            lichSuThanhToanRepo.flush(); // ⭐ FORCE SAVE TO DB

            log.info("✅ SUCCESSFULLY updated payment history - ID: {}, Status: {}", saved.getId(), saved.getTrangThai());

            // ⭐ XÓA KHỎI MAP SAU KHI CẬP NHẬT THÀNH CÔNG
            pendingPaymentMap.remove(maHoaDon);
            log.info("🗑️ Removed pending payment ID from map for order: {}", maHoaDon);

        } catch (Exception e) {
            log.error("❌ Error updating payment history: ", e);
            throw e;
        }
    }

    /**
     * ⭐ TẠO MỚI LỊCH SỬ THANH TOÁN THÀNH CÔNG (nếu không tìm thấy pending)
     */
    private void createNewSuccessPaymentHistory(HoaDon hoaDon, String transactionNo,
                                                String bankCode, String payDate) {
        try {
            Optional<PhuongThucThanhToan> ptttOpt = ptttRepo.findByTenPhuongThucThanhToan("Chuyển khoản");
            if (!ptttOpt.isPresent()) {
                log.warn("⚠️ Payment method 'Chuyển khoản' not found");
                return;
            }

            LichSuThanhToan lichSu = new LichSuThanhToan();
            lichSu.setHoaDon(hoaDon);
            lichSu.setPhuongThucThanhToan(ptttOpt.get());
            lichSu.setSoTien(hoaDon.getTongTienSauGiam());
            lichSu.setNgayThanhToan(new Date());
            lichSu.setTrangThai(true); // ✅ THÀNH CÔNG

            StringBuilder ghiChu = new StringBuilder("VNPAY THÀNH CÔNG");
            if (transactionNo != null && !transactionNo.isEmpty()) {
                ghiChu.append(" - Mã GD: ").append(transactionNo);
            }
            if (bankCode != null && !bankCode.isEmpty()) {
                ghiChu.append(" - NH: ").append(bankCode);
            }

            lichSu.setGhiChu(ghiChu.toString());
            lichSuThanhToanRepo.save(lichSu);

            log.info("💳 Created new SUCCESS payment history");

        } catch (Exception e) {
            log.error("❌ Error creating payment history: ", e);
        }
    }

    /**
     * ⭐ CẬP NHẬT LỊCH SỬ THANH TOÁN THẤT BẠI
     * LẤY TRỰC TIẾP ID TỪ MAP
     */
    @Transactional(rollbackFor = Exception.class)
    public void updatePaymentHistoryToFailed(HoaDon hoaDon, String errorCode, String transactionNo) {
        try {
            String maHoaDon = hoaDon.getMaHoaDon();

            // ⭐ LẤY ID TỪ MAP
            Integer pendingPaymentId = pendingPaymentMap.get(maHoaDon);

            if (pendingPaymentId == null) {
                log.warn("⚠️ No pending payment ID found in map for order: {}", maHoaDon);
                // Fallback: tìm theo hoaDonId
                List<LichSuThanhToan> pendingList = lichSuThanhToanRepo.findByHoaDonIdAndTrangThai(hoaDon.getId(), false);
                if (!pendingList.isEmpty()) {
                    pendingPaymentId = pendingList.get(0).getId();
                    log.info("📋 Found pending payment by fallback search: ID = {}", pendingPaymentId);
                } else {
                    log.error("❌ Cannot find pending payment history");
                    return;
                }
            }

            // ⭐ LẤY TRỰC TIẾP THEO ID
            Optional<LichSuThanhToan> lichSuOpt = lichSuThanhToanRepo.findById(pendingPaymentId);

            if (!lichSuOpt.isPresent()) {
                log.error("❌ Payment history not found with ID: {}", pendingPaymentId);
                return;
            }

            LichSuThanhToan lichSu = lichSuOpt.get();

            log.info("📝 Updating payment history ID: {} to FAILED", lichSu.getId());

            // ⭐ CHỈ CẬP NHẬT GHI CHÚ - GIỮ TRẠNG THÁI FALSE
            lichSu.setNgayThanhToan(new Date());

            String errorMsg = getVnpayErrorMessage(errorCode);
            StringBuilder ghiChu = new StringBuilder("VNPAY THẤT BẠI - ");
            ghiChu.append(errorMsg);

            if (transactionNo != null && !transactionNo.isEmpty()) {
                ghiChu.append(" - Mã GD: ").append(transactionNo);
            }

            lichSu.setGhiChu(ghiChu.toString());

            lichSuThanhToanRepo.save(lichSu);
            lichSuThanhToanRepo.flush();

            log.info("❌ Updated payment history note to FAILED (status remains false)");

            // ⭐ XÓA KHỎI MAP
            pendingPaymentMap.remove(maHoaDon);
            log.info("🗑️ Removed pending payment ID from map for order: {}", maHoaDon);

        } catch (Exception e) {
            log.error("❌ Error updating failed payment history: ", e);
            throw e;
        }
    }

    /**
     * Format ngày từ VNPAY
     */
    private String formatPayDate(String payDate) {
        try {
            if (payDate == null || payDate.length() != 14) {
                return payDate;
            }

            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            Date date = inputFormat.parse(payDate);
            return outputFormat.format(date);

        } catch (Exception e) {
            log.error("Error formatting pay date: {}", e.getMessage());
            return payDate;
        }
    }

    /**
     * Lấy thông điệp lỗi từ mã lỗi VNPAY
     */
    private String getVnpayErrorMessage(String errorCode) {
        if (errorCode == null) return "Lỗi không xác định";

        switch (errorCode) {
            case "07": return "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo)";
            case "09": return "Thẻ/Tài khoản chưa đăng ký InternetBanking";
            case "10": return "Xác thực thông tin không đúng quá 3 lần";
            case "11": return "Đã hết hạn chờ thanh toán";
            case "12": return "Thẻ/Tài khoản bị khóa";
            case "13": return "Sai mật khẩu OTP";
            case "24": return "Khách hàng hủy giao dịch";
            case "51": return "Tài khoản không đủ số dư";
            case "65": return "Vượt quá hạn mức giao dịch trong ngày";
            case "75": return "Ngân hàng đang bảo trì";
            case "79": return "Nhập sai mật khẩu quá số lần quy định";
            default: return "Lỗi thanh toán (Mã: " + errorCode + ")";
        }
    }

    private void createOrderHistory(HoaDon hoaDon, String action, String description) {
        try {
            Optional<NhanVien> nhanVienOpt = nhanVienRepo.findById(1);
            if (!nhanVienOpt.isPresent()) {
                log.warn("⚠️ Default employee not found");
                return;
            }

            LichSuHoaDon lichSu = new LichSuHoaDon();
            lichSu.setHoaDon(hoaDon);
            lichSu.setKhachHang(hoaDon.getKhachHang());
            lichSu.setNhanVien(nhanVienOpt.get());
            lichSu.setHanhDong(action);
            lichSu.setMoTa(description);
            lichSu.setNgayCapNhat(new Date());
            lichSu.setTrangThai(true);

            lichSuHoaDonRepo.save(lichSu);
            log.info("📋 Order history saved");

        } catch (Exception e) {
            log.error("❌ Error saving order history: ", e);
        }
    }

    private void sendPaymentSuccessEmail(HoaDon hoaDon) {
        try {
            String customerEmail = hoaDon.getKhachHang().getEmail();

            if (customerEmail == null || customerEmail.isEmpty() || customerEmail.startsWith("guest_")) {
                log.info("ℹ️ Email not sent - invalid or guest email: {}", customerEmail);
                return;
            }

            if (hoaDon.getHoaDonChiTiets() != null) {
                hoaDon.getHoaDonChiTiets().size();
            }

            emailTaoDonHangService.sendOrderConfirmationEmail(hoaDon, customerEmail);
            log.info("📧 VNPAY payment confirmation email queued for: {}", customerEmail);

        } catch (Exception e) {
            log.error("❌ Failed to send payment confirmation email: {}", e.getMessage());
        }
    }
}