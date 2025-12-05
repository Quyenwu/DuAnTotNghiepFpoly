package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.NhanVienRequest;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.service.AuthService;

import com.example.the_autumn.service.NhanVienService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000","http://192.203.4.118:5173"})
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
