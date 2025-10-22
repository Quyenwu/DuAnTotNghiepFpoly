package com.example.the_autumn.service;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.AddKhachHangRequest;
import com.example.the_autumn.model.request.DiaChiRequest;
import com.example.the_autumn.model.request.UpdateKhachHangRequest;
import com.example.the_autumn.model.response.DiaChiResponse;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.repository.DiaChiRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
import com.example.the_autumn.repository.QuanHuyenRepository;
import com.example.the_autumn.repository.TinhThanhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    private TinhThanhRepository tinhThanhRepository;

    @Autowired
    private QuanHuyenRepository quanHuyenRepository;

    public List<KhachHangResponse> getAllKhachHang() {
        List<KhachHang> khachHangs = khachHangRepository.findAll();

        Map<Integer, Object[]> statsMap = lichSuHoaDonRepository.getSoLanVaNgayMuaGanNhatCuaKhachHang()
                .stream()
                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));

        List<KhachHangResponse> result = new ArrayList<>();

        for (KhachHang k : khachHangs) {
            Object[] stats = statsMap.get(k.getId());
            Long soLanMuaLong = stats != null ? (Long) stats[1] : 0L;
            Integer soLanMua = soLanMuaLong != null ? soLanMuaLong.intValue() : 0;
            Date ngayMuaGanNhat = stats != null ? (Date) stats[2] : null;

            result.add(new KhachHangResponse(k, soLanMua, ngayMuaGanNhat));
        }
        return result;
    }

    public List<KhachHangResponse> getData() {
        return khachHangRepository.findAll()
                .stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public KhachHangResponse createKhachHang(AddKhachHangRequest request) {
        KhachHang khachHang = new KhachHang();
        khachHang.setHoTen(request.getHoTen());
        khachHang.setEmail(request.getEmail());
        khachHang.setSdt(request.getSdt());
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        khachHang.setMatKhau(randomPassword);
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setTrangThai(true);
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setNgayTao(new Date());
        // Lưu vào database
        KhachHang savedKhachHang = khachHangRepository.save(khachHang);
        List<DiaChiRequest> diaChiRequests = request.getDiaChi();
        if (diaChiRequests != null) {
            boolean hasDefault = diaChiRequests.stream().anyMatch(DiaChiRequest::getTrangThai);
            for (int i = 0; i < diaChiRequests.size(); i++) {
                DiaChiRequest d = diaChiRequests.get(i);
                DiaChi dc = new DiaChi();
                dc.setKhachHang(savedKhachHang);
                dc.setTenDiaChi(d.getTenDiaChi());
                dc.setDiaChiCuThe(d.getDiaChiCuThe());
                dc.setTinhThanh(tinhThanhRepository.findById(d.getTinhThanhId())
                        .orElseThrow(() -> new RuntimeException("Tỉnh không tồn tại")));
                dc.setQuanHuyen(quanHuyenRepository.findById(d.getQuanHuyenId())
                        .orElseThrow(() -> new RuntimeException("Quận/Huyện không tồn tại")));
                dc.setTrangThai(hasDefault ? Boolean.TRUE.equals(d.getTrangThai()) : i == 0);
                diaChiRepository.save(dc);
            }
        }

        KhachHang savedKhachHangFull = khachHangRepository.findById(savedKhachHang.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng sau khi lưu"));

        return new KhachHangResponse(savedKhachHangFull);
    }

    public KhachHangResponse updateKhachHang(Integer id, UpdateKhachHangRequest request) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        kh.setHoTen(request.getHoTen());
        kh.setEmail(request.getEmail());
        kh.setSdt(request.getSdt());
        kh.setGioiTinh(request.getGioiTinh());
        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            kh.setMatKhau(request.getMatKhau());
        }
        kh.setTrangThai(request.getTrangThai());
        kh.setNgaySinh(request.getNgaySinh());
        kh.setNgaySua(new Date());

        List<DiaChiRequest> diaChiRequests = request.getDiaChi();
        if (diaChiRequests != null) {
            boolean hasDefault = diaChiRequests.stream().anyMatch(DiaChiRequest::getTrangThai);

            List<Integer> requestIds = diaChiRequests.stream()
                    .map(DiaChiRequest::getId)
                    .filter(dcId -> dcId != null)
                    .toList();

            List<DiaChi> existing = diaChiRepository.findByKhachHangId(kh.getId());
            for (DiaChi old : existing) {
                if (!requestIds.contains(old.getId())) {
                    diaChiRepository.delete(old);
                }
            }

            // ✅ Chỉ một địa chỉ mặc định
            for (int i = 0; i < diaChiRequests.size(); i++) {
                DiaChiRequest d = diaChiRequests.get(i);
                DiaChi dc;
                if (d.getId() != null) {
                    dc = diaChiRepository.findById(d.getId())
                            .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
                } else {
                    dc = new DiaChi();
                    dc.setKhachHang(kh);
                }

                dc.setTenDiaChi(d.getTenDiaChi());
                dc.setDiaChiCuThe(d.getDiaChiCuThe());
                dc.setTinhThanh(tinhThanhRepository.findById(d.getTinhThanhId())
                        .orElseThrow(() -> new RuntimeException("Tỉnh không tồn tại")));
                dc.setQuanHuyen(quanHuyenRepository.findById(d.getQuanHuyenId())
                        .orElseThrow(() -> new RuntimeException("Quận/Huyện không tồn tại")));

                dc.setTrangThai(hasDefault ? Boolean.TRUE.equals(d.getTrangThai()) : i == 0);
                diaChiRepository.save(dc);
            }
        }


        KhachHang savedKh = khachHangRepository.findById(kh.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng sau khi cập nhật"));
        return new KhachHangResponse(savedKh);
    }

    public void deleteKhachHang(Integer id) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Xóa thành công"));
        khachHangRepository.delete(kh);
    }

    public KhachHangResponse detailKhachHang(Integer id) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        KhachHangResponse response = new KhachHangResponse(kh);

        if (kh.getDiaChi() != null) {
            response.setDiaChi(
                    kh.getDiaChi().stream()
                            .map(DiaChiResponse::new)
                            .collect(Collectors.toList())
            );
        }

        response.setSoLanMua(kh.getHoaDons() != null ? kh.getHoaDons().size() : 0);
        response.setNgayMuaGanNhat(
                kh.getHoaDons() != null && !kh.getHoaDons().isEmpty()
                        ? kh.getHoaDons().get(kh.getHoaDons().size() - 1).getNgayTao()
                        : null
        );
        return response;
    }
    public List<KhachHangResponse> searchKhachHang(String keyword) {
        List<KhachHang> list = khachHangRepository.searchByKeyword(keyword);
        return list.stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public List<KhachHangResponse> filterKhachHang(Boolean gioiTinh, Boolean trangThai) {
        List<KhachHang> list = khachHangRepository.filterByGenderAndStatus(gioiTinh, trangThai);
        return list.stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public boolean checkExistsByEmailAndSdt(String email, String sdt) {
        if (email != null && sdt != null) {
            return khachHangRepository.existsByEmailAndSdt(email, sdt);
        } else if (email != null) {
            return khachHangRepository.existsByEmail(email);
        } else if (sdt != null) {
            return khachHangRepository.existsBySdt(sdt);
        }
        return false;
    }

}
