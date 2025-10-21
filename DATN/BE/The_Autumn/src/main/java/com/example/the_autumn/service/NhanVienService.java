package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.expection.ApiException;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.NhanVienResponse;

import com.example.the_autumn.repository.ChucVuRepository;

import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.util.MapperUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        ChucVu chucVu = chucVuRepository.findById(2)
                .orElseThrow(() -> new ApiException("Không tìm thấy chức vụ Nhân viên ", "404"));
        nv.setTrangThai(true);
        nv.setChucVu(chucVu);
        nv.setMatKhau("123456");
        nhanVienRepository.save(nv);
    }

    public void update(Integer id, NhanVienRequest nhanVienRequest) {
        NhanVien nv = nhanVienRepository.findById(id).get();
        MapperUtils.mapToExisting(nhanVienRequest, nv);
        ChucVu chucVu = chucVuRepository.findById(nhanVienRequest.getChucVuId()).orElse(null);
        nv.setChucVu(chucVu);
        nv.setId(id);
        nv.setNgaySua(new Date());
        nhanVienRepository.save(nv);
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        NhanVien nv = nhanVienRepository.findById(id).orElseThrow(
                () -> new ApiException("Không tìm thấy Phiếu Giảm Giá", "404")
        );
        nv.setTrangThai(trangThai);
        nhanVienRepository.save(nv);
    }

    public List<NhanVienResponse> searchNhanVien(
            String keyword,
            Boolean gioiTinh,
            String chucVuId,
            Boolean trangThai
    ) {
        Specification<NhanVien> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.isEmpty()) {
                String likePattern = "%" + keyword.toLowerCase() + "%";
                Predicate pHoTen = cb.like(cb.lower(root.get("hoTen")), likePattern);
                Predicate pSdt = cb.like(cb.lower(root.get("sdt")), likePattern);
                Predicate pDiaChi = cb.like(cb.lower(root.get("diaChi")), likePattern);
                Predicate pEmail = cb.like(cb.lower(root.get("email")), likePattern);
                predicates.add(cb.or(pHoTen, pSdt, pDiaChi, pEmail));
            }

            if (gioiTinh != null) {
                predicates.add(cb.equal(root.get("gioiTinh"), gioiTinh));
            }

            if (chucVuId != null && !chucVuId.isEmpty()) {
                predicates.add(cb.equal(root.get("chucVu").get("id"), Integer.parseInt(chucVuId)));
            }

            if (trangThai != null) {
                predicates.add(cb.equal(root.get("trangThai"), trangThai));
            }

            if (predicates.isEmpty()) return cb.conjunction();
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return nhanVienRepository.findAll(spec)
                .stream()
                .map(NhanVienResponse::new)
                .collect(Collectors.toList());
    }


}





