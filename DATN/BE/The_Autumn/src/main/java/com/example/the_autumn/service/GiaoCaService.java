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
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GiaoCaService {

    @Autowired
    private GiaoCaRepository giaoCaRepository;
    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private PhanCaRepository phanCaRepository;

    public List<GiaoCaResponse> getAll() {
        return giaoCaRepository.findAllByOrderByThoiGianBatDauDesc()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteById(Integer id) {
        giaoCaRepository.deleteById(id);
    }

    @Transactional
    public GiaoCaResponse startShift(GiaoCaStartRequest request) {
        if (request.getIdNhanVien() == null || request.getSoTienBatDau() == null) {
            throw new IllegalArgumentException("idNhanVien và soTienBatDau là bắt buộc");
        }

        // TODO: Nếu có Spring Security, nên check id nhân viên đăng nhập hiện tại
        // Ví dụ: Integer currentNhanVienId = securityService.getCurrentNhanVienId();
        // if (!currentNhanVienId.equals(request.getIdNhanVien())) {
        //     throw new IllegalStateException("Bạn chỉ được phép bắt đầu ca cho chính mình");
        // }

        NhanVien nv = nhanVienRepository.findById(request.getIdNhanVien())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        // Không cho mở ca mới khi ca cũ chưa kết thúc
        if (giaoCaRepository.existsByNhanVien_IdAndThoiGianKetThucIsNull(nv.getId())) {
            throw new IllegalStateException("Nhân viên đang có ca chưa kết thúc");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDate today = now.toLocalDate();
        LocalTime currentTime = now.toLocalTime();

        // ====== CHECK TIỀN ĐẦU CA THEO NGÀY ======
        // Ca đầu tiên trong NGÀY hôm nay: có thể bắt đầu với 0 (hoặc số khác tuỳ bạn).
        // Các ca tiếp theo trong cùng ngày: bắt buộc soTienBatDau = soTienKetThuc của ca trước trong NGÀY đó.
        giaoCaRepository
                .findFirstByNhanVien_IdAndThoiGianKetThucIsNotNullOrderByThoiGianKetThucDesc(nv.getId())
                .ifPresent(prev -> {
                    LocalDate prevDate = prev.getThoiGianBatDau() != null
                            ? prev.getThoiGianBatDau().toLocalDate()
                            : null;

                    // Chỉ check nếu ca trước cũng là trong HÔM NAY
                    if (prevDate != null && prevDate.isEqual(today)) {
                        BigDecimal expected = prev.getSoTienKetThuc() == null
                                ? BigDecimal.ZERO
                                : prev.getSoTienKetThuc();

                        if (expected.compareTo(request.getSoTienBatDau()) != 0) {
                            throw new IllegalArgumentException(
                                    "Số tiền bắt đầu ca mới (" + request.getSoTienBatDau() +
                                            ") không khớp với số tiền kết thúc ca trước trong ngày (" + expected + ")"
                            );
                        }
                    }
                    // Nếu prevDate != today: coi như ca đầu tiên trong ngày -> không ép bằng ca hôm qua.
                });

        // ====== CHECK LỊCH PHÂN CA ======
        List<PhanCa> phanCaList = phanCaRepository
                .findByNhanVien_IdAndNgayPhanCaAndTrangThai(nv.getId(), today, true);

        if (phanCaList.isEmpty()) {
            throw new IllegalStateException("Nhân viên hôm nay chưa được phân ca, không thể bắt đầu ca.");
        }

        // Tìm ca làm việc mà giờ hiện tại đang nằm trong khung giờ
        PhanCa phanCaHopLe = phanCaList.stream()
                .filter(pc -> isWithinShift(pc.getCaLamViec(), currentTime))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Thời điểm hiện tại không nằm trong bất kỳ ca làm việc nào của nhân viên."));

        // (Option) Ghi thêm info ca làm việc vào ghi chú
        String ghiChu = request.getGhiChu();
        if (ghiChu == null) ghiChu = "";
        CaLamViec ca = phanCaHopLe.getCaLamViec();
        String caInfo = "[Ca: " + ca.getTenCa() + " " + ca.getGioBatDau() + " - " + ca.getGioKetThuc() + "]";
        if (!ghiChu.isBlank()) {
            ghiChu = caInfo + " | " + ghiChu;
        } else {
            ghiChu = caInfo;
        }

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

    @Transactional
    public GiaoCaResponse endShift(Integer giaoCaId, String ghiChu) {
        GiaoCa giaoCa = giaoCaRepository.findById(giaoCaId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giao ca"));

        if (giaoCa.getThoiGianKetThuc() != null) {
            throw new IllegalStateException("Giao ca này đã được kết thúc trước đó");
        }

        // ====== CHECK ĐÃ HẾT CA LÀM VIỆC CHƯA (dựa vào ca_lam_viec + phan_ca) ======
        LocalDateTime now = LocalDateTime.now();

        NhanVien nv = giaoCa.getNhanVien();
        if (nv == null || nv.getId() == null) {
            throw new IllegalStateException("Giao ca không có thông tin nhân viên, không thể kiểm tra ca làm việc.");
        }

        // Ngày phân ca được hiểu là ngày bắt đầu ca của giao ca
        LocalDate ngayCa = giaoCa.getThoiGianBatDau().toLocalDate();
        LocalTime gioBatDauGiaoCa = giaoCa.getThoiGianBatDau().toLocalTime();

        // Lấy danh sách phân ca của nhân viên trong ngày đó
        List<PhanCa> phanCaList = phanCaRepository
                .findByNhanVien_IdAndNgayPhanCaAndTrangThai(nv.getId(), ngayCa, true);

        if (phanCaList.isEmpty()) {
            throw new IllegalStateException("Không tìm thấy lịch phân ca tương ứng để kiểm tra thời gian kết thúc ca.");
        }

        // Tìm ca làm việc mà thời gian BẮT ĐẦU GIAO CA nằm trong ca
        PhanCa phanCaCaNay = phanCaList.stream()
                .filter(pc -> isWithinShift(pc.getCaLamViec(), gioBatDauGiaoCa))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException(
                        "Không tìm thấy ca làm việc tương ứng với thời gian bắt đầu giao ca."));

        CaLamViec caLamViec = phanCaCaNay.getCaLamViec();
        LocalTime start = caLamViec.getGioBatDau();
        LocalTime end = caLamViec.getGioKetThuc();

        // Xác định "thời điểm ca kết thúc" theo dạng LocalDateTime
        LocalDateTime shiftEndDateTime;
        if (!start.isAfter(end)) {
            // Ca bình thường trong ngày: ví dụ 07:00 - 12:00
            shiftEndDateTime = ngayCa.atTime(end);
        } else {
            // Ca qua đêm: ví dụ 23:00 - 07:00 -> kết thúc vào ngày hôm sau lúc 07:00
            shiftEndDateTime = ngayCa.plusDays(1).atTime(end);
        }

        // Nếu bây giờ vẫn CHƯA QUA thời điểm kết thúc ca -> không cho kết thúc giao ca
        if (now.isBefore(shiftEndDateTime)) {
            throw new IllegalStateException("Chưa hết ca làm việc, không thể kết thúc giao ca.");
        }

        // 1) set thời gian kết thúc ca
        giaoCa.setThoiGianKetThuc(now);

        // 2) nối ghi chú nếu có
        if (ghiChu != null && !ghiChu.isBlank()) {
            if (giaoCa.getGhiChu() != null && !giaoCa.getGhiChu().isBlank()) {
                giaoCa.setGhiChu(giaoCa.getGhiChu() + " | " + ghiChu);
            } else {
                giaoCa.setGhiChu(ghiChu);
            }
        }

        // 3) Tính tổng doanh thu trong ca
        BigDecimal tongDoanhThu = tinhDoanhThuTrongCa(giaoCa);
        if (tongDoanhThu == null) {
            tongDoanhThu = BigDecimal.ZERO;
        }
        giaoCa.setTongDoanhThu(tongDoanhThu);

        // 4) Tính số tiền kết thúc = tiền đầu ca + doanh thu
        BigDecimal soTienBatDau = giaoCa.getSoTienBatDau() != null
                ? giaoCa.getSoTienBatDau()
                : BigDecimal.ZERO;

        BigDecimal soTienKetThuc = soTienBatDau.add(tongDoanhThu);
        giaoCa.setSoTienKetThuc(soTienKetThuc);

        // 5) Chênh lệch tạm = 0 (sau này nếu có tiền thực tế nhập thêm thì tính lại)
        giaoCa.setSoTienChenhLech(BigDecimal.ZERO);

        // 6) Đánh dấu ca đã kết thúc
        giaoCa.setTrangThai(false);

        GiaoCa saved = giaoCaRepository.save(giaoCa);
        return toResponse(saved);
    }

    /**
     * Tính tổng doanh thu trong ca của 1 nhân viên.
     * Ở đây đang tính theo NGÀY (dùng ngay_thanh_toan là DATE).
     * Nếu sau này muốn chính xác theo giờ, có thể đổi sang lọc by datetime.
     */
    private BigDecimal tinhDoanhThuTrongCa(GiaoCa giaoCa) {
        LocalDate fromDate = giaoCa.getThoiGianBatDau().toLocalDate();
        LocalDate toDate = giaoCa.getThoiGianKetThuc() != null
                ? giaoCa.getThoiGianKetThuc().toLocalDate()
                : fromDate;

        return hoaDonRepository.sumDoanhThuTrongCa(
                giaoCa.getNhanVien().getId(),
                fromDate,
                toDate
        );
    }

    /**
     * Check giờ hiện tại có nằm trong ca làm việc không.
     * Có handle cả ca qua đêm (vd: 23:00 - 07:00).
     */
    private boolean isWithinShift(CaLamViec caLamViec, LocalTime currentTime) {
        LocalTime start = caLamViec.getGioBatDau();
        LocalTime end = caLamViec.getGioKetThuc();

        // Ca bình thường trong ngày: start <= end
        if (!start.isAfter(end)) {
            return !currentTime.isBefore(start) && !currentTime.isAfter(end);
        }
        // Ca qua đêm: vd 23:00 - 07:00
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
