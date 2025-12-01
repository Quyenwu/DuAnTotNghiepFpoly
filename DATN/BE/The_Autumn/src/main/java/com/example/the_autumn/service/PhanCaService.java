package com.example.the_autumn.service;

import com.example.the_autumn.dto.PhanCaDTO;
import com.example.the_autumn.entity.CaLamViec;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhanCa;
import com.example.the_autumn.expection.ApiException;
import com.example.the_autumn.repository.CaLamViecRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhanCaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PhanCaService {

    @Autowired
    private PhanCaRepository repository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    public List<PhanCaDTO> getAllPhanCa() {
        return repository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PhanCaDTO> getPhanCaByNhanVien(Integer idNhanVien) {
        return repository.findByNhanVienId(idNhanVien).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PhanCaDTO> getPhanCaByDate(LocalDate date) {
        return repository.findByNgayPhanCa(date).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PhanCaDTO> getPhanCaByNhanVienAndDateRange(Integer idNhanVien,
                                                           LocalDate start,
                                                           LocalDate end) {
        return repository.findByNhanVienAndDateRange(idNhanVien, start, end).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PhanCaDTO createPhanCa(PhanCaDTO dto) {
        NhanVien nv = nhanVienRepository.findById(dto.getIdNhanVien()).orElse(null);
        CaLamViec ca = caLamViecRepository.findById(dto.getIdCaLamViec()).orElse(null);
        if (nv == null || ca == null) return null;

        LocalDate ngayPhanCa = LocalDate.parse(dto.getNgayPhanCa());

        boolean slotTaken = repository
                .findFirstByCaLamViec_IdAndNgayPhanCaAndTrangThaiTrue(ca.getId(), ngayPhanCa)
                .isPresent();
        if (slotTaken) {
            // Có thể sau này anh đổi thành throw exception custom để trả message rõ ràng
            throw new ApiException("Ca làm việc này đã được phân cho nhân viên khác trong ngày này!", "SHIFT_CONFLICT");
        }

        PhanCa pc = new PhanCa();
        pc.setNhanVien(nv);
        pc.setCaLamViec(ca);
        pc.setNgayPhanCa(ngayPhanCa);
        pc.setGhiChu(dto.getGhiChu());
        pc.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        pc.setNgayTao(LocalDateTime.now());

        return convertToDTO(repository.save(pc));
    }


    public PhanCaDTO updatePhanCa(Integer id, PhanCaDTO dto) {
        return repository.findById(id).map(pc -> {
            CaLamViec ca = caLamViecRepository.findById(dto.getIdCaLamViec()).orElse(null);
            NhanVien nv = nhanVienRepository.findById(dto.getIdNhanVien()).orElse(null); // 🟢 Lấy đối tượng NhanVien MỚI

            // 🔴 CHECK: Kiểm tra sự tồn tại của Ca và NhanVien
            if (ca == null || nv == null) return null;

            LocalDate ngayPhanCa = LocalDate.parse(dto.getNgayPhanCa());

            // 🔴 CHECK: ca + ngày này đã được phân cho người khác/cùng người chưa?
            var conflictOpt = repository
                    .findFirstByCaLamViec_IdAndNgayPhanCaAndTrangThaiTrue(ca.getId(), ngayPhanCa);

            if (conflictOpt.isPresent() && !conflictOpt.get().getId().equals(pc.getId())) {
                // Có phân ca khác (id khác) đang chiếm slot này rồi
                // 💡 Cân nhắc thêm check nếu ca & ngày không đổi nhưng NhanVien đổi -> cũng check conflict
                throw new ApiException("Ca làm việc này đã được phân cho nhân viên khác trong ngày này!", "SHIFT_CONFLICT");
            }

            // 🟢 Cập nhật cả NhanVien và Ca làm việc
            pc.setNhanVien(nv); // 🟢 THÊM: Cập nhật nhân viên mới
            pc.setCaLamViec(ca);
            pc.setNgayPhanCa(ngayPhanCa);
            pc.setGhiChu(dto.getGhiChu());
            pc.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : pc.getTrangThai());
            pc.setNgaySua(LocalDateTime.now());

            return convertToDTO(repository.save(pc));
        }).orElse(null);
    }


    public boolean deletePhanCa(Integer id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return true;
        }
        return false;
    }

    private PhanCaDTO convertToDTO(PhanCa e) {
        return new PhanCaDTO(
                e.getId(),
                e.getMaPhanCa(),
                e.getNhanVien().getId(),
                e.getNhanVien().getHoTen(),
                e.getCaLamViec().getId(),
                e.getCaLamViec().getTenCa(),
                e.getCaLamViec().getGioBatDau().toString(),
                e.getCaLamViec().getGioKetThuc().toString(),
                e.getNgayPhanCa().toString(),
                e.getGhiChu(),
                e.getTrangThai()
        );
    }
}
