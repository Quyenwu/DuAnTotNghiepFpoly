package com.example.the_autumn.service;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.expection.AppException;
import com.example.the_autumn.expection.ErrorCode;
import com.example.the_autumn.mapper.khachHangMapper;
import com.example.the_autumn.model.request.CreateKhachHang;
import com.example.the_autumn.model.request.UpdateDiaChi;
import com.example.the_autumn.model.request.UpdateKhachHang;
import com.example.the_autumn.model.response.KhachHangDTO;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.repository.DiaChiRepo;
import com.example.the_autumn.repository.KhachHangRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class KhachHangService {

    KhachHangRepository khRepo;
    khachHangMapper khMapper;
    PasswordEncoder passwordEncoder;
    DiaChiRepo diaChiRepo;
    public KhachHang createRequest(CreateKhachHang u) {

        if (khRepo.existsByTenTaiKhoan(u.getTenTaiKhoan())) {
            throw new AppException(ErrorCode.USER_TonTai);
        }
        KhachHang kh = new KhachHang();
        kh.setSdt(u.getSdt());
        kh.setHoTen(u.getHoTen());
        kh.setGioiTinh(u.getGioiTinh());
        kh.setEmail(u.getEmail());
        kh.setTenTaiKhoan(u.getTenTaiKhoan());
        kh.setMatKhau(passwordEncoder.encode(u.getMatKhau()));
        kh.setNgayTao(new Date());
        kh.setTrangThai(true);
// Lưu KhachHang kèm DiaChi
        KhachHang savedKh = khRepo.save(kh);

        // Tạo mã khách hàng
        savedKh.setMaKhachHang(String.format("KH%04d", savedKh.getId()));
        khRepo.save(savedKh);

/// 2 Map danh sách DiaChi sau khi có ID
        if (u.getDiaChi() != null && !u.getDiaChi().isEmpty()) {
            List<DiaChi> diaChiEntities = u.getDiaChi().stream().map(d -> {
                DiaChi dc = new DiaChi();
                dc.setDiaChiCuThe(d.getDiaChiCuThe());
                dc.setQuan(d.getQuan());
                dc.setThanhPho(d.getThanhPho());
                dc.setTenDiaChi(d.getTenDiaChi());
                dc.setKhachHang(savedKh); // <-- đã có ID
                dc.setMaDiaChi("DC" + UUID.randomUUID().toString().substring(0, 8));
                return dc;
            }).collect(Collectors.toList());
            diaChiRepo.saveAll(diaChiEntities);
            savedKh.setDiaChi(diaChiEntities);
        }

        return savedKh;
    }

    public KhachHang updateRequest(Integer id, UpdateKhachHang u) {
        KhachHang kh = khRepo.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_KhongTonTai));

        // --- Cập nhật thông tin cơ bản ---
        kh.setHoTen(u.getHoTen());
        kh.setSdt(u.getSdt());
        kh.setGioiTinh(u.getGioiTinh());
        kh.setTrangThai(u.getTrangThai());
        kh.setNgaySua(new Date());

        if (u.getMatKhau() != null && !u.getMatKhau().isEmpty()) {
            kh.setMatKhau(passwordEncoder.encode(u.getMatKhau()));
        }
        // --- Cập nhật địa chỉ ---
        if (u.getDiaChi() != null) {
            // Map để tìm nhanh địa chỉ cũ
            Map<Integer, DiaChi> diaChiCuMap = kh.getDiaChi().stream()
                    .collect(Collectors.toMap(DiaChi::getId, d -> d));

            List<DiaChi> diaChiMoi = new ArrayList<>();

            for (UpdateDiaChi d : u.getDiaChi()) {
                DiaChi dc;

                if (d.getId() != null && diaChiCuMap.containsKey(d.getId())) {

                    dc = diaChiCuMap.get(d.getId());
                    dc.setTenDiaChi(d.getTenDiaChi());
                    dc.setThanhPho(d.getThanhPho());
                    dc.setQuan(d.getQuan());
                    dc.setDiaChiCuThe(d.getDiaChiCuThe());
                } else {

                    dc = new DiaChi();
                    dc.setMaDiaChi("DC" + UUID.randomUUID().toString().substring(0, 8));
                    dc.setTenDiaChi(d.getTenDiaChi());
                    dc.setThanhPho(d.getThanhPho());
                    dc.setQuan(d.getQuan());
                    dc.setDiaChiCuThe(d.getDiaChiCuThe());
                    dc.setTrangThai(true);
                    dc.setKhachHang(kh);
                }

                // Bắt buộc phải gán lại quan hệ 2 chiều
                dc.setKhachHang(kh);
                diaChiMoi.add(dc);
            }

            // Xóa địa chỉ cũ không còn trong danh sách
            kh.getDiaChi().removeIf(dc -> diaChiMoi.stream()
                    .noneMatch(newDc -> Objects.equals(dc.getId(), newDc.getId())));

            // Giữ lại các địa chỉ mới hoặc đã cập nhật
            for (DiaChi dc : diaChiMoi) {
                if (dc.getId() == null) {
                    kh.getDiaChi().add(dc); // thêm mới
                }
            }
        }

        return khRepo.save(kh);
    }




    public List<KhachHangDTO> getAll() {
        return khRepo.findAll()
                .stream()
                .map(khMapper::toDTO)
                .toList();
    }

    public void delete(Integer id) {
        khRepo.deleteById(id);
    }

    public Optional<KhachHang> detail(Integer id) {
        return khRepo.findById(id);
    }

    //lay du lieu cua minh = token
    public KhachHang getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String name = context.getAuthentication().getName();
        KhachHang kh = khRepo.findByTenTaiKhoan(name).orElseThrow(() -> new AppException(ErrorCode.USER_KhongTonTai));
        return kh;
    }

}
