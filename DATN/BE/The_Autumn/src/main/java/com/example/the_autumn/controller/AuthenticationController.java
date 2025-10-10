package com.example.the_autumn.controller;

import com.example.the_autumn.model.request.ApiResponse;
import com.example.the_autumn.model.request.CreateKhachHang;
import com.example.the_autumn.model.request.LoginRequest;
import com.example.the_autumn.model.response.AuthenticationResponse;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.service.AuthenticationService;
import com.example.the_autumn.service.KhachHangService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticateService;
    KhachHangService serviceUser;

    @PostMapping("/login")
    ApiResponse<AuthenticationResponse>Login(@RequestBody @Valid LoginRequest request){
     var result= authenticateService.authenticate(request);
        ApiResponse<AuthenticationResponse> response = new ApiResponse<>();
        response.setResult(result);
        return response;

    }
    @PostMapping("/register")
    ApiResponse<KhachHang> createUser(@RequestBody @Valid CreateKhachHang userCreation) {
        ApiResponse<KhachHang> userApiResponse = new ApiResponse<>();
        userApiResponse.setResult(serviceUser.createRequest(userCreation));
        return userApiResponse;
    }

}
