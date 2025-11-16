package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.KhachHangAuthRequest;
import com.example.the_autumn.model.response.KhachHangAuthResponse;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.security.CustomerPrinciple;
import com.example.the_autumn.security.jwt.CustomerJwtProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Date;

@Service
public class CustomerAuthService {

    @Autowired
    private CustomerJwtProvider customerJwtProvider;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Transactional
    public void register(KhachHangAuthRequest req) {
        System.out.println("=== CUSTOMER REGISTER DEBUG START ===");

        // Validate email exists
        if (khachHangRepository.existsByEmail(req.getEmail())) {
            System.err.println("❌ Email already exists: " + req.getEmail());
            throw new RuntimeException("Email đã tồn tại");
        }

        // Validate phone exists (if provided)
        if (req.getSdt() != null && khachHangRepository.existsBySdt(req.getSdt())) {
            System.err.println("❌ Phone number already exists: " + req.getSdt());
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        System.out.println("✅ Validation passed");

        KhachHang kh = new KhachHang();
        kh.setMaKhachHang(generateMaKhachHang());
        kh.setHoTen(req.getHoTen());
        kh.setEmail(req.getEmail());
        kh.setMatKhau(passwordEncoder.encode(req.getMatKhau()));
        kh.setSdt(req.getSdt());
        kh.setGioiTinh(req.getGioiTinh());
        kh.setNgaySinh(req.getNgaySinh());
        kh.setTrangThai(true);
        kh.setNgayTao(new Date());

        khachHangRepository.save(kh);

        System.out.println("✅ Customer registered successfully with ID: " + kh.getId());
        System.out.println("=== CUSTOMER REGISTER DEBUG END ===");
    }

    @Transactional(readOnly = true)
    public KhachHangAuthResponse login(KhachHangAuthRequest req) {
        try {
            System.out.println("=== CUSTOMER LOGIN DEBUG START ===");
            System.out.println("Input email: " + req.getEmail());

            // Find customer by email
            KhachHang customer = khachHangRepository.findByEmail(req.getEmail())
                    .orElseThrow(() -> {
                        System.err.println("❌ Customer not found in database");
                        return new RuntimeException("Email không tồn tại");
                    });

            System.out.println("✅ Customer found:");
            System.out.println("   - ID: " + customer.getId());
            System.out.println("   - Email: " + customer.getEmail());
            System.out.println("   - Status: " + customer.getTrangThai());

            // Check if customer is active
            if (!customer.getTrangThai()) {
                System.err.println("❌ Customer account is inactive");
                throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
            }

            // Verify password
            boolean passwordMatches = passwordEncoder.matches(req.getMatKhau(), customer.getMatKhau());
            System.out.println("🔑 Password check:");
            System.out.println("   - Matches: " + passwordMatches);

            if (!passwordMatches) {
                System.err.println("❌ Password does not match");
                throw new RuntimeException("Mật khẩu không đúng");
            }

            System.out.println("✅ Password validation passed");

            // Create CustomerPrinciple
            CustomerPrinciple customerPrinciple = CustomerPrinciple.builder()
                    .customer(customer)
                    .authorities(Collections.singletonList(new SimpleGrantedAuthority("ROLE_CUSTOMER")))
                    .build();

            // Generate JWT token
            String token = customerJwtProvider.generateToken(customerPrinciple);

            System.out.println("✅ JWT Token generated successfully");
            System.out.println("=== CUSTOMER LOGIN DEBUG END - SUCCESS ===");

            return createKhachHangAuthResponse(customer, token);

        } catch (Exception e) {
            System.err.println("=== CUSTOMER LOGIN DEBUG END - ERROR ===");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Đăng nhập thất bại: " + e.getMessage());
        }
    }

    private KhachHangAuthResponse createKhachHangAuthResponse(KhachHang khachHang, String token) {
        KhachHangAuthResponse response = new KhachHangAuthResponse();
        response.setId(khachHang.getId());
        response.setMaKhachHang(khachHang.getMaKhachHang());
        response.setHoTen(khachHang.getHoTen());
        response.setGioiTinh(khachHang.getGioiTinh());
        response.setNgaySinh(khachHang.getNgaySinh());
        response.setEmail(khachHang.getEmail());
        response.setSdt(khachHang.getSdt());
        response.setTrangThai(khachHang.getTrangThai());
        response.setNgayTao(khachHang.getNgayTao());
        response.setNgaySua(khachHang.getNgaySua());
        response.setAccessToken(token);
        response.setTypeToken("Bearer");
        response.setUserType("CUSTOMER");

        return response;
    }

    private String generateMaKhachHang() {
        return "KH" + System.currentTimeMillis();
    }
}