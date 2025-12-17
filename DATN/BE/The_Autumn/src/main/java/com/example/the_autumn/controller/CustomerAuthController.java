package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.KhachHangAuthRequest;
import com.example.the_autumn.model.response.KhachHangAuthResponse;
import com.example.the_autumn.service.CustomerAuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customer/auth")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174","http://172.20.10.2:5173"})
public class CustomerAuthController {

    @Autowired
    private CustomerAuthService customerAuthService;

    @PostMapping("/login")
    public ResponseEntity<KhachHangAuthResponse> login(@Valid @RequestBody KhachHangAuthRequest request) {
        try {
            KhachHangAuthResponse response = customerAuthService.login(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody KhachHangAuthRequest request) {
        try {
            customerAuthService.register(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Đăng ký thành công! Vui lòng đăng nhập.");
        } catch (RuntimeException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // JWT stateless, client sẽ xóa token
        // Nếu cần blacklist token, implement thêm Redis
        return ResponseEntity.ok("Đăng xuất thành công");
    }
}