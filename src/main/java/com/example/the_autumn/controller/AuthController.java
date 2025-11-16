package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.LoginRequest;
import com.example.the_autumn.model.request.RegisterRequest;
import com.example.the_autumn.model.response.LoginResponse;
import com.example.the_autumn.model.response.UserInfoResponse;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
@Slf4j
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            log.info("📧 Login request - Email: {}", request.getEmail());

            // ✅ Dùng method mới để lấy user object
            Object user = authService.authenticateUser(request.getEmail(), request.getPassword());

            if (user instanceof NhanVien nv) {
                // ✅ Nhân viên
                String token = "STAFF:" + nv.getId() + ":" + nv.getHoTen() + ":" + nv.getChucVu().getId();

                LoginResponse response = LoginResponse.success(
                        token,
                        "STAFF",
                        nv.getHoTen(),
                        "Đăng nhập thành công"
                );

                // ✅ Set thêm các field mới
                response.setId(nv.getId());
                response.setEmail(nv.getEmail());
                response.setSdt(nv.getSdt());

                log.info("✅ Staff login success - ID: {}, Email: {}", nv.getId(), nv.getEmail());
                return ResponseEntity.ok(response);

            } else if (user instanceof KhachHang kh) {
                // ✅ Khách hàng
                String token = "CUSTOMER:" + kh.getId() + ":" + kh.getHoTen();

                LoginResponse response = LoginResponse.success(
                        token,
                        "CUSTOMER",
                        kh.getHoTen(),
                        "Đăng nhập thành công"
                );

                // ✅ Set thêm các field mới
                response.setId(kh.getId());
                response.setEmail(kh.getEmail());
                response.setSdt(kh.getSdt());

                log.info("✅ Customer login success - ID: {}, Email: {}", kh.getId(), kh.getEmail());
                return ResponseEntity.ok(response);

            } else {
                log.warn("❌ Login failed - Invalid credentials");
                return ResponseEntity.badRequest().body(
                        LoginResponse.error("Sai email hoặc mật khẩu")
                );
            }

        } catch (Exception e) {
            log.error("❌ Login error: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(
                    LoginResponse.error("Lỗi đăng nhập: " + e.getMessage())
            );
        }
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            KhachHang khachHang = new KhachHang();
            khachHang.setHoTen(request.getHoTen());
            khachHang.setEmail(request.getEmail());
            khachHang.setMatKhau(request.getPassword());
            khachHang.setSdt(request.getSdt());
            khachHang.setGioiTinh(request.getGioiTinh());
            khachHang.setNgaySinh(request.getNgaySinh());
            khachHang.setTrangThai(true);
            khachHang.setNgayTao(new Date());

            KhachHang newCustomer = authService.registerKhachHang(khachHang);

            String token = "CUSTOMER:" + newCustomer.getId() + ":" + newCustomer.getHoTen();

            LoginResponse response = LoginResponse.success(
                    token,
                    "CUSTOMER",
                    newCustomer.getHoTen(),
                    "Đăng ký thành công"
            );

            // ✅ Set thêm thông tin
            response.setId(newCustomer.getId());
            response.setEmail(newCustomer.getEmail());
            response.setSdt(newCustomer.getSdt());

            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(
                    LoginResponse.error(e.getMessage())
            );
        }
    }

    @GetMapping("/check-email")
    public ResponseEntity<Map<String, Object>> checkEmail(@RequestParam String email) {
        boolean exists = authService.isEmailExists(email);
        Map<String, Object> response = new HashMap<>();
        response.put("exists", exists);
        response.put("message", exists ? "Email đã tồn tại" : "Email có thể sử dụng");
        response.put("success", !exists);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo(@RequestParam String token) {
        try {
            Object user = authService.getUserFromToken(token);

            if (user instanceof KhachHang kh) {
                UserInfoResponse response = new UserInfoResponse(
                        kh.getId(),
                        kh.getHoTen(),
                        kh.getEmail(),
                        "CUSTOMER",
                        kh.getSdt(),
                        kh.getGioiTinh(),
                        kh.getMaKhachHang()
                );
                return ResponseEntity.ok(response);

            } else if (user instanceof NhanVien nv) {
                String chucVu = nv.getChucVu() != null ? nv.getChucVu().getTenChucVu() : "Nhân viên";
                UserInfoResponse response = new UserInfoResponse(
                        nv.getId(),
                        nv.getHoTen(),
                        nv.getEmail(),
                        "STAFF",
                        nv.getSdt(),
                        nv.getGioiTinh(),
                        nv.getMaNhanVien()
                );
                response.setChucVu(chucVu);
                return ResponseEntity.ok(response);
            }

            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Token không hợp lệ")
            );

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("success", false, "message", "Lỗi lấy thông tin user: " + e.getMessage())
            );
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Đăng xuất thành công");
        return ResponseEntity.ok(response);
    }
}