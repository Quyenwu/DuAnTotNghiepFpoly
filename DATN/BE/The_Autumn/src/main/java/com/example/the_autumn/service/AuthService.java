package com.example.the_autumn.service;

import com.example.the_autumn.entity.ChucVu;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.security.UserPrinciple;
import com.example.the_autumn.security.jwt.JwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    public void register(NhanVienRequest req) {
        if (nhanVienRepository.existsByEmail(req.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        NhanVien nv = new NhanVien();
        nv.setMaNhanVien(generateMaNhanVien());
        nv.setHoTen(req.getHoTen());
        nv.setEmail(req.getEmail());
        nv.setMatKhau(passwordEncoder.encode(req.getMatKhau()));
        nv.setDiaChi(req.getDiaChi());
        nv.setSdt(req.getSdt());
        nv.setGioiTinh(req.getGioiTinh());
        nv.setNgaySinh(req.getNgaySinh());
        nv.setHinhAnh(req.getHinhAnh());
        nv.setTrangThai(true);
        nv.setNgayTao(new Date());

        ChucVu chucVu = new ChucVu();
        chucVu.setId(2);
        nv.setChucVu(chucVu);

        nhanVienRepository.save(nv);
    }

    public NhanVienResponse login(NhanVienRequest req) {
        try {
            System.out.println("=== LOGIN DEBUG START ===");
            System.out.println("Input email: " + req.getEmail());

            NhanVien user = nhanVienRepository.getNhanVienByEmail(req.getEmail());
            if (user == null) {
                System.err.println("❌ User not found in database");
                throw new RuntimeException("Email không tồn tại");
            }

            System.out.println("✅ User found:");
            System.out.println("   - ID: " + user.getId());
            System.out.println("   - Email: " + user.getEmail());
            System.out.println("   - Status: " + user.getTrangThai());

            boolean passwordMatches = passwordEncoder.matches(req.getMatKhau(), user.getMatKhau());
            System.out.println("🔑 Password check:");
            System.out.println("   - Matches: " + passwordMatches);

            if (!passwordMatches) {
                System.err.println("❌ Password does not match");
                throw new RuntimeException("Mật khẩu không đúng");
            }

            System.out.println("✅ Password validation passed");

            Authentication authentication = authenticationProvider.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getEmail(), req.getMatKhau())
            );

            UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
            String token = jwtProvider.generateToken(userPrinciple);

            System.out.println("✅ JWT Token generated successfully");
            System.out.println("=== LOGIN DEBUG END - SUCCESS ===");

            return createNhanVienResponse(user, token);

        } catch (Exception e) {
            System.err.println("=== LOGIN DEBUG END - ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    private NhanVienResponse createNhanVienResponse(NhanVien nhanVien, String token) {
        NhanVienResponse response = new NhanVienResponse();
        response.setId(nhanVien.getId());

        if (nhanVien.getChucVu() != null) {
            response.setChucVuId(nhanVien.getChucVu().getId());
            response.setChucVuName(nhanVien.getChucVu().getTenChucVu());
            System.out.println("Role info: " + nhanVien.getChucVu().getTenChucVu());
        } else {
            response.setChucVuId(2);
            response.setChucVuName("Nhân viên");
        }

        response.setMaNhanVien(nhanVien.getMaNhanVien());
        response.setHoTen(nhanVien.getHoTen());
        response.setGioiTinh(nhanVien.getGioiTinh());
        response.setNgaySinh(nhanVien.getNgaySinh());
        response.setEmail(nhanVien.getEmail());
        response.setSdt(nhanVien.getSdt());
        response.setDiaChi(nhanVien.getDiaChi());
        response.setHinhAnh(nhanVien.getHinhAnh());
        response.setMatKhau("***");
        response.setNgayTao(nhanVien.getNgayTao());
        response.setNgaySua(nhanVien.getNgaySua());
        response.setTrangThai(nhanVien.getTrangThai());
        response.setAccessToken(token);
        response.setTypeToken("Bearer");

        return response;
    }

    private String generateMaNhanVien() {
        return "NV" + System.currentTimeMillis();
    }
}