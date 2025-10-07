package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhieuGiamGia;
import com.example.the_autumn.expection.ApiException;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.request.PhieuGiamGiaRequesst;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.model.response.PhieuGiamGiaRespone;
import com.example.the_autumn.repository.ChucVuRepository;
import com.example.the_autumn.repository.GiamGiaKhachHangRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.util.MapperUtils;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private ChucVuRepository chucVuRepository;


    public List<NhanVienResponse> getAllNhanVien() {
        return nhanVienRepository.findAll().stream().map(NhanVienResponse::new).collect(Collectors.toList());
    }

    public PageableObject<NhanVienResponse> phanTrang(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<NhanVien> page = nhanVienRepository.findAll(pageable);
        Page<NhanVienResponse> phieuGiamGiaRespones = page.map(NhanVienResponse::new);
        return new PageableObject<>(phieuGiamGiaRespones);
    }

    public NhanVienResponse getNhanVienById(Integer id) {
        NhanVien p = nhanVienRepository.findById(id).orElseThrow();
        return new NhanVienResponse(p);
    }

    public void delete(Integer id) {
        nhanVienRepository.findById(id).orElseThrow(
                () -> new ApiException("Khong tim thay Phieu Giam Gia", "404")
        );
        nhanVienRepository.deleteById(id);
    }

    public void add(NhanVienRequest nhanVienRequest) {
        NhanVien nv = MapperUtils.map(nhanVienRequest, NhanVien.class);
        ChucVu chucVu = chucVuRepository.findById(nhanVienRequest.getChucVuId()).orElse(null);
        nv.setTrangThai(true);
        nv.setChucVu(chucVu);
        nhanVienRepository.save(nv);
    }

    public void update(Integer id, NhanVienRequest nhanVienRequest) {
        NhanVien nv = nhanVienRepository.findById(id).get();
        MapperUtils.mapToExisting(nhanVienRequest, nv);
        ChucVu chucVu = chucVuRepository.findById(nhanVienRequest.getChucVuId()).orElse(null);
        nv.setChucVu(chucVu);
        nv.setId(id);
        nhanVienRepository.save(nv);
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        NhanVien nv = nhanVienRepository.findById(id).orElseThrow(
                () -> new ApiException("Không tìm thấy Phiếu Giảm Giá", "404")
        );
        nv.setTrangThai(trangThai);
        nhanVienRepository.save(nv);
    }
}





