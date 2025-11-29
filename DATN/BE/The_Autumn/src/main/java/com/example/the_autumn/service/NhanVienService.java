package com.example.the_autumn.service;

import com.example.the_autumn.dto.NhanVienSelectDTO;
import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.GiamGiaKhachHang;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.expection.ApiException;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.NhanVienResponse;

import com.example.the_autumn.model.response.PhieuGiamGiaRespone;
import com.example.the_autumn.repository.ChucVuRepository;

import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.util.MapperUtils;
import jakarta.persistence.criteria.Predicate;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;
    @Autowired
    private ChucVuRepository chucVuRepository;

    @Autowired
    private EmailService mailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<NhanVienResponse> getAllNhanVien() {
        return nhanVienRepository.findAll().stream()
                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(NhanVienResponse::new)
                .collect(Collectors.toList());
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
        // Kiểm tra email đã tồn tại chưa
        if (nhanVienRepository.existsByEmail(nhanVienRequest.getEmail())) {
            throw new ApiException("Email đã tồn tại trong hệ thống", "400");
        }

        NhanVien nv = MapperUtils.map(nhanVienRequest, NhanVien.class);
        String randomPassword = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
        nv.setMatKhau(passwordEncoder.encode(randomPassword));
        ChucVu chucVu = chucVuRepository.findById(2)
                .orElseThrow(() -> new ApiException("Không tìm thấy chức vụ Nhân viên", "404"));

        nv.setTrangThai(true);
        nv.setChucVu(chucVu);

        nhanVienRepository.save(nv);

        String subject = "Thông báo tài khoản nhân viên mới";
        String body = """
                <div style="font-family:Arial, sans-serif;">
                    <h3>Xin chào %s,</h3>
                    <p>Bạn đã được thêm vào hệ thống với vai trò <b>Nhân viên</b>.</p>
                    <p>Dưới đây là thông tin đăng nhập của bạn:</p>
                    <ul>
                        <li><b>Tài khoản:</b> %s</li>
                        <li><b>Mật khẩu:</b> %s</li>
                    </ul>
                    <p>Vui lòng đăng nhập và thay đổi mật khẩu sau khi truy cập hệ thống lần đầu.</p>
                    <p style="color:#E67E22;">Trân trọng,<br/>Hệ thống quản lý The Autumn</p>
                </div>
                """.formatted(nv.getHoTen(), nv.getEmail(), randomPassword);;

        try {
            mailService.sendMailNhanVien(nv.getEmail(), subject, body);
        } catch (Exception e) {
            System.err.println("Gửi email thất bại: " + e.getMessage());
        }
    }


    public void update(Integer id, NhanVienRequest request) {

        NhanVien nv = nhanVienRepository.findById(id)
                .orElseThrow(() -> new ApiException("Không tìm thấy nhân viên", "404"));

        ChucVu chucVu = chucVuRepository.findById(request.getChucVuId())
                .orElseThrow(() -> new ApiException("Không tìm thấy chức vụ", "404"));

        nv.setChucVu(chucVu);
        nv.setHoTen(request.getHoTen());
        nv.setGioiTinh(request.getGioiTinh());
        nv.setSdt(request.getSdt());
        nv.setDiaChi(request.getDiaChi());
        nv.setEmail(request.getEmail());
        nv.setCccd(request.getCccd());
        nv.setNgaySinh(request.getNgaySinh());
        nv.setHinhAnh(request.getHinhAnh());
        nv.setTrangThai(request.getTrangThai());
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
                Predicate pMaNhanVien = cb.like(cb.lower(root.get("maNhanVien")), likePattern);
                Predicate pHoTen = cb.like(cb.lower(root.get("hoTen")), likePattern);
                Predicate pSdt = cb.like(cb.lower(root.get("sdt")), likePattern);
                Predicate pDiaChi = cb.like(cb.lower(root.get("diaChi")), likePattern);
                Predicate pEmail = cb.like(cb.lower(root.get("email")), likePattern);
                predicates.add(cb.or(pMaNhanVien,pHoTen, pSdt, pDiaChi, pEmail));
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

    public boolean checkEmailExists(String email) {
        return nhanVienRepository.existsByEmail(email);
    }

    public boolean checkSdtExists(String sdt) {
        return nhanVienRepository.existsBySdt(sdt);
    }


    public List<NhanVienSelectDTO> getAllForSelect() {
        // chỉ lấy NV đang hoạt động, nếu muốn lấy hết thì đổi thành findAll()
        List<NhanVien> list = nhanVienRepository.findByTrangThai(true);

        return list.stream()
                .map(nv -> new NhanVienSelectDTO(
                        nv.getId(),
                        nv.getHoTen()
                ))
                .collect(Collectors.toList());
    }

}





