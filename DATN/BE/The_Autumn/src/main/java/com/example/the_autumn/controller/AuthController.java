package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.LoginRequest;
import com.example.the_autumn.model.request.RegisterRequest;
import com.example.the_autumn.model.response.LoginResponse;
import com.example.the_autumn.model.response.UserInfoResponse;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request.getEmail(), request.getPassword());

            if (token != null) {
                String[] tokenParts = token.split(":");
                String userType = tokenParts[0];
                String hoTen = tokenParts[2];
                String roleInfo = tokenParts.length > 3 ? tokenParts[3] : "";

                return ResponseEntity.ok(LoginResponse.success(
                        token,
                        userType,
                        hoTen,
                        "Đăng nhập thành công"
                ));
            } else {
                return ResponseEntity.badRequest().body(
                        LoginResponse.error("Sai email hoặc mật khẩu")
                );
            }
        } catch (Exception e) {
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

            khachHang.setSdt(request.getSdt() != null ? request.getSdt() : "");
            khachHang.setGioiTinh(request.getGioiTinh() != null ? request.getGioiTinh() : true);
            khachHang.setNgaySinh(request.getNgaySinh() != null ? request.getNgaySinh() : new Date());

            khachHang.setTrangThai(true);
            khachHang.setNgayTao(new Date());

            KhachHang newCustomer = authService.registerKhachHang(khachHang);

            String token = authService.login(newCustomer.getEmail(), newCustomer.getMatKhau());
            String[] tokenParts = token.split(":");

            return ResponseEntity.ok(LoginResponse.success(
                    token,
                    "CUSTOMER",
                    newCustomer.getHoTen(),
                    "Đăng ký thành công"
            ));

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

            } else if (user instanceof com.example.the_autumn.entity.NhanVien nv) {
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