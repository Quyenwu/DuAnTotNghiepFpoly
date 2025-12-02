package com.example.the_autumn.service;

import com.example.the_autumn.entity.GiaoCa;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhanCa;
import com.example.the_autumn.entity.CaLamViec;
import com.example.the_autumn.model.request.GiaoCaStartRequest;
import com.example.the_autumn.model.response.GiaoCaResponse;
import com.example.the_autumn.repository.GiaoCaRepository;
import com.example.the_autumn.repository.HoaDonRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhanCaRepository;
import com.example.the_autumn.security.UserPrinciple;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class GiaoCaService {

    @Autowired
    private GiaoCaRepository giaoCaRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private PhanCaRepository phanCaRepository;

    /**
     * Lấy thông tin nhân viên hiện tại từ JWT token
     */
    private NhanVien getCurrentNhanVien() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new IllegalStateException("Người dùng chưa đăng nhập hoặc Token không hợp lệ");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrinciple) {
            UserPrinciple userPrinciple = (UserPrinciple) principal;
            return userPrinciple.getUser();
        }

        throw new IllegalStateException("Không thể xác định thông tin người dùng từ Token");
    }

    /**
     * Lấy danh sách giao ca CỦA TẤT CẢ NHÂN VIÊN TRONG NGÀY HÔM NAY (Bắt đầu trong ngày).
     */
    public List<GiaoCaResponse> getAll() {
        LocalDate today = LocalDate.now();
        // Bắt đầu 00:00:00 hôm nay
        LocalDateTime startOfDay = today.atStartOfDay();
        // Kết thúc 23:59:59.999... hôm nay
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay().minusNanos(1);

        return giaoCaRepository
                .findByThoiGianBatDauBetweenOrderByThoiGianBatDauDesc(
                        startOfDay,
                        endOfDay
                )
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        giaoCaRepository.deleteById(id);
    }

    public GiaoCaResponse startShift(@Valid GiaoCaStartRequest request) {
        if (request.getSoTienBatDau() == null) {
            throw new IllegalArgumentException("soTienBatDau là bắt buộc");
        }

        NhanVien nv = getCurrentNhanVien();

        if (giaoCaRepository.existsByNhanVien_IdAndThoiGianKetThucIsNull(nv.getId())) {
            throw new IllegalStateException("Nhân viên đang có ca chưa kết thúc");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // ----------------------------------------------------
        // LOGIC CHUYỂN GIAO TIỀN: CHỈ LẤY CA KẾT THÚC TRONG NGÀY HÔM NAY (STRICT)
        // ----------------------------------------------------
        giaoCaRepository
                .findFirstByThoiGianKetThucIsNotNullOrderByThoiGianKetThucDesc()
                .ifPresent(prev -> {
                    LocalDateTime prevEnd = prev.getThoiGianKetThuc();
                    // 00:00:00 ngày hôm nay
                    LocalDateTime todayStart = LocalDateTime.now().toLocalDate().atStartOfDay();

                    // Chỉ kiểm tra khớp tiền nếu ca trước KẾT THÚC trong ngày hôm nay
                    if (prevEnd != null && prevEnd.isAfter(todayStart)) {
                        BigDecimal expected = prev.getSoTienKetThuc() == null
                                ? BigDecimal.ZERO
                                : prev.getSoTienKetThuc();

                        // So sánh tiền bàn giao
                        if (expected.compareTo(request.getSoTienBatDau()) != 0) {
                            String prevNhanVien = prev.getNhanVien() != null ? prev.getNhanVien().getHoTen() : "Không rõ";

                            throw new IllegalArgumentException(
                                    "Số tiền bàn giao bắt đầu (" + request.getSoTienBatDau() +
                                            ") không khớp với số tiền kết thúc ca trước của NV **" + prevNhanVien +
                                            "** (" + expected + ") lúc " + prev.getThoiGianKetThuc() +
                                            ". Vui lòng kiểm tra lại."
                            );
                        }
                    }
                    // Nếu ca kết thúc hôm qua, nó sẽ không được kiểm tra (chấp nhận rủi ro).
                });
        // ----------------------------------------------------

        // Check lịch phân ca
        List<PhanCa> phanCaList = phanCaRepository
                .findByNhanVien_IdAndNgayPhanCaAndTrangThai(nv.getId(), today, true);

        if (phanCaList.isEmpty()) {
            throw new IllegalStateException("Hôm nay bạn chưa được phân ca làm việc.");
        }

        PhanCa phanCaHopLe = phanCaList.stream()
                .filter(pc -> isWithinShift(pc.getCaLamViec(), currentTime))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Thời điểm hiện tại (" + currentTime + ") không nằm trong ca làm việc được phân công."));

        String ghiChu = request.getGhiChu();
        if (ghiChu == null) ghiChu = "";
        CaLamViec ca = phanCaHopLe.getCaLamViec();
        String caInfo = "[Ca: " + ca.getTenCa() + " " + ca.getGioBatDau() + " - " + ca.getGioKetThuc() + "]";
        ghiChu = !ghiChu.isBlank() ? caInfo + " | " + ghiChu : caInfo;

        GiaoCa giaoCa = GiaoCa.builder()
                .nhanVien(nv)
                .thoiGianBatDau(now)
                .soTienBatDau(request.getSoTienBatDau())
                .tongDoanhThu(BigDecimal.ZERO)
                .soTienKetThuc(null)
                .soTienChenhLech(BigDecimal.ZERO)
                .ghiChu(ghiChu)
                .trangThai(true)
                .ngayTao(now)
                .build();

        GiaoCa saved = giaoCaRepository.save(giaoCa);
        return toResponse(saved);
    }

    public GiaoCaResponse endShift(@Valid Integer giaoCaId, String ghiChu) {
        NhanVien currentNhanVien = getCurrentNhanVien();

        GiaoCa giaoCa = giaoCaRepository.findById(giaoCaId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao ca"));

        if (!giaoCa.getNhanVien().getId().equals(currentNhanVien.getId())) {
            throw new IllegalStateException("Bạn chỉ có thể kết thúc ca của chính mình");
        }

        if (giaoCa.getThoiGianKetThuc() != null) {
            throw new IllegalStateException("Giao ca này đã được kết thúc trước đó");
        }

        LocalDateTime now = LocalDateTime.now();


        NhanVien nv = giaoCa.getNhanVien();
        LocalDate ngayCa = giaoCa.getThoiGianBatDau().toLocalDate();
        LocalTime gioBatDauGiaoCa = giaoCa.getThoiGianBatDau().toLocalTime();

        List<PhanCa> phanCaList = phanCaRepository.findByNhanVien_IdAndNgayPhanCaAndTrangThai(nv.getId(), ngayCa, true);
        PhanCa phanCaCaNay = phanCaList.stream()
                .filter(pc -> isWithinShift(pc.getCaLamViec(), gioBatDauGiaoCa))
                .findFirst().orElse(null);

        // Kiểm tra điều kiện kết thúc ca (CHỈ XỬ LÝ CA TRONG NGÀY)
        if (phanCaCaNay != null) {
            CaLamViec caLamViec = phanCaCaNay.getCaLamViec();
            LocalTime end = caLamViec.getGioKetThuc();

            // Ca phải kết thúc trong ngày bắt đầu
            LocalDateTime shiftEndDateTime = ngayCa.atTime(end);

            if (now.isBefore(shiftEndDateTime)) {
                throw new IllegalStateException("Chưa hết ca làm việc chưa thể kết thúc.");
            }
        }


        giaoCa.setThoiGianKetThuc(now);

        if (ghiChu != null && !ghiChu.isBlank()) {
            String currentNote = giaoCa.getGhiChu() == null ? "" : giaoCa.getGhiChu();
            giaoCa.setGhiChu(currentNote + (currentNote.isBlank() ? "" : " | ") + "[Kết thúc] " + ghiChu);
        }

        BigDecimal tongDoanhThu = tinhDoanhThuTrongCa(giaoCa);
        if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;
        giaoCa.setTongDoanhThu(tongDoanhThu);

        BigDecimal soTienBatDau = giaoCa.getSoTienBatDau() != null ? giaoCa.getSoTienBatDau() : BigDecimal.ZERO;
        // Số tiền kết thúc (tiền mặt bàn giao) = Tiền mặt đầu ca + Tổng Doanh thu
        giaoCa.setSoTienKetThuc(soTienBatDau.add(tongDoanhThu));
        giaoCa.setSoTienChenhLech(BigDecimal.ZERO);
        giaoCa.setTrangThai(false);

        GiaoCa saved = giaoCaRepository.save(giaoCa);
        return toResponse(saved);
    }

    private BigDecimal tinhDoanhThuTrongCa(GiaoCa giaoCa) {
        LocalDateTime startTime = giaoCa.getThoiGianBatDau();
        LocalDateTime endTime = giaoCa.getThoiGianKetThuc() != null
                ? giaoCa.getThoiGianKetThuc()
                : LocalDateTime.now();

        return hoaDonRepository.sumDoanhThuTrongCa(
                giaoCa.getNhanVien().getId(),
                startTime,
                endTime
        );
    }

    private boolean isWithinShift(CaLamViec caLamViec, LocalTime currentTime) {
        LocalTime start = caLamViec.getGioBatDau();
        LocalTime end = caLamViec.getGioKetThuc();

        // CHỈ XỬ LÝ CA TRONG NGÀY (start <= end)
        if (start.isAfter(end)) {
            // Nếu giờ bắt đầu > giờ kết thúc, ca không hợp lệ (vì đã xóa logic ca đêm)
            return false;
        }

        // Kiểm tra xem thời gian hiện tại có nằm trong khoảng [start, end] không
        return !currentTime.isBefore(start) && !currentTime.isAfter(end);
    }

    private GiaoCaResponse toResponse(GiaoCa gc) {
        return GiaoCaResponse.builder()
                .id(gc.getId())
                .idNhanVien(gc.getNhanVien() != null ? gc.getNhanVien().getId() : null)
                .hoTenNhanVien(gc.getNhanVien() != null ? gc.getNhanVien().getHoTen() : null)
                .thoiGianBatDau(gc.getThoiGianBatDau())
                .thoiGianKetThuc(gc.getThoiGianKetThuc())
                .soTienBatDau(gc.getSoTienBatDau())
                .soTienKetThuc(gc.getSoTienKetThuc())
                .tongDoanhThu(gc.getTongDoanhThu())
                .soTienChenhLech(gc.getSoTienChenhLech())
                .ghiChu(gc.getGhiChu())
                .trangThai(gc.getTrangThai())
                .build();
    }
}