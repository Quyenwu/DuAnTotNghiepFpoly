package com.example.the_autumn.service;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.AddKhachHangRequest;
import com.example.the_autumn.model.request.UpdateKhachHangRequest;
import com.example.the_autumn.model.response.DiaChiResponse;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
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

        List<DiaChi> diaChis = request.getDiaChi();
        if (diaChis != null) {
            diaChis.forEach(d -> d.setKhachHang(khachHang));
            khachHang.setDiaChi(new ArrayList<>(diaChis));
        }

        // Lưu vào database
        KhachHang savedKhachHang = khachHangRepository.save(khachHang);

        // Trả về response DTO
        return new KhachHangResponse(savedKhachHang);
    }

    public KhachHangResponse updateKhachHang(Integer id, UpdateKhachHangRequest request) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khach hàng không tồn tại"));
        kh.setNgaySua(new Date());
        kh.setMatKhau(request.getMatKhau());
        kh.setEmail(request.getEmail());
        kh.setTrangThai(request.getTrangThai());
        kh.setGioiTinh(request.getGioiTinh());
        kh.setHoTen(request.getHoTen());
        kh.setNgaySinh(request.getNgaySinh());

        List<DiaChi> diaChis = request.getDiaChi();
        if (diaChis != null) {
            kh.getDiaChi().clear();
            diaChis.forEach(d -> d.setKhachHang(kh));
            kh.getDiaChi().addAll(diaChis);
        }
        KhachHang saveUpdate = khachHangRepository.save(kh);
        return new KhachHangResponse(saveUpdate);

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

}
