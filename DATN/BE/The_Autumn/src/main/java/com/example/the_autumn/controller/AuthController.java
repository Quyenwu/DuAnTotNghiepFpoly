package com.example.the_autumn.controller;

import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.service.AuthService;

import com.example.the_autumn.service.NhanVienService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @Autowired
    private NhanVienService nhanVienService;


    @PostMapping("/login")
    public ResponseEntity<NhanVienResponse> login(@RequestBody NhanVienRequest userLoginRequestDTO){
        return new ResponseEntity<>(authService.login(userLoginRequestDTO), HttpStatus.OK);
    }
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody NhanVienRequest userRequestDTO){
        authService.register(userRequestDTO);
        return new ResponseEntity<>("Register Successfully", HttpStatus.CREATED);
    }
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        return new ResponseEntity<>("Logout Successfully", HttpStatus.OK);
    }

}
