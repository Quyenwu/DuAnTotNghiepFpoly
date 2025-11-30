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
@Transactional // Quan trọng: Giúp quản lý transaction cho toàn bộ service
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
     * Lấy danh sách giao ca CỦA RIÊNG NHÂN VIÊN ĐANG ĐĂNG NHẬP
     */
    public List<GiaoCaResponse> getAll() {
        NhanVien nv = getCurrentNhanVien();
        // Chỉ tìm giao ca của nhân viên này
        return giaoCaRepository.findAllByNhanVien_IdOrderByThoiGianBatDauDesc(nv.getId())
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

        // Check tiền đầu ca khớp ca trước (nếu có)
        giaoCaRepository
                .findFirstByNhanVien_IdAndThoiGianKetThucIsNotNullOrderByThoiGianKetThucDesc(nv.getId())
                .ifPresent(prev -> {
                    LocalDate prevDate = prev.getThoiGianBatDau() != null
                            ? prev.getThoiGianBatDau().toLocalDate()
                            : null;

                    if (prevDate != null && prevDate.isEqual(today)) {
                        BigDecimal expected = prev.getSoTienKetThuc() == null
                                ? BigDecimal.ZERO
                                : prev.getSoTienKetThuc();

                        if (expected.compareTo(request.getSoTienBatDau()) != 0) {
                            throw new IllegalArgumentException(
                                    "Số tiền bắt đầu (" + request.getSoTienBatDau() +
                                            ") không khớp với số tiền kết thúc ca trước (" + expected + ")"
                            );
                        }
                    }
                });

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

        if (phanCaCaNay != null) {
            CaLamViec caLamViec = phanCaCaNay.getCaLamViec();
            LocalTime end = caLamViec.getGioKetThuc();
            LocalDateTime shiftEndDateTime = (!caLamViec.getGioBatDau().isAfter(end))
                    ? ngayCa.atTime(end)
                    : ngayCa.plusDays(1).atTime(end);

            if (now.isBefore(shiftEndDateTime)) {
                throw new IllegalStateException("Chưa hết ca làm việc chưa thể kết thúc.");
            }
        }


        giaoCa.setThoiGianKetThuc(now);

        if (ghiChu != null && !ghiChu.isBlank()) {
            String currentNote = giaoCa.getGhiChu() == null ? "" : giaoCa.getGhiChu();
            giaoCa.setGhiChu(currentNote + (currentNote.isBlank() ? "" : " | ") + ghiChu);
        }

        BigDecimal tongDoanhThu = tinhDoanhThuTrongCa(giaoCa);
        if (tongDoanhThu == null) tongDoanhThu = BigDecimal.ZERO;
        giaoCa.setTongDoanhThu(tongDoanhThu);

        BigDecimal soTienBatDau = giaoCa.getSoTienBatDau() != null ? giaoCa.getSoTienBatDau() : BigDecimal.ZERO;
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
                : LocalDateTime.now(); // Sử dụng thời điểm hiện tại
        return hoaDonRepository.sumDoanhThuTrongCa(
                giaoCa.getNhanVien().getId(),
                startTime, // Truyền LocalDateTime
                endTime    // Truyền LocalDateTime
        );
    }

    private boolean isWithinShift(CaLamViec caLamViec, LocalTime currentTime) {
        LocalTime start = caLamViec.getGioBatDau();
        LocalTime end = caLamViec.getGioKetThuc();
        // Xử lý ca qua đêm (ví dụ 22:00 -> 06:00)
        if (!start.isAfter(end)) {
            return !currentTime.isBefore(start) && !currentTime.isAfter(end);
        }
        return !currentTime.isBefore(start) || !currentTime.isAfter(end);
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