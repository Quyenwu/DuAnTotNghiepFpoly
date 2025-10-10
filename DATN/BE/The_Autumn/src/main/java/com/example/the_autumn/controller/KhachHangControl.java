package com.example.the_autumn.controller;


import com.example.the_autumn.model.request.ApiResponse;
import com.example.the_autumn.model.request.CreateKhachHang;
import com.example.the_autumn.model.request.UpdateKhachHang;
import com.example.the_autumn.model.response.KhachHangDTO;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.enums.Role;
import com.example.the_autumn.expection.AppException;
import com.example.the_autumn.expection.ErrorCode;
import com.example.the_autumn.mapper.khachHangMapper;
import com.example.the_autumn.service.KhachHangService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/khachhang")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@EnableWebSecurity
public class KhachHangControl {
    KhachHangService serviceUser;
    PasswordEncoder passwordEncoder;
    khachHangMapper userMapper;

    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @GetMapping
    public ResponseEntity<List<KhachHangDTO>> show() {
        return ResponseEntity.ok(serviceUser.getAll());
    }
    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @PostMapping
    ApiResponse<KhachHang> createUser(@RequestBody @Valid CreateKhachHang userCreation) {
        ApiResponse<KhachHang> userApiResponse = new ApiResponse<>();
        userApiResponse.setResult(serviceUser.createRequest(userCreation));
        return userApiResponse;
    }

// @PostAuthorize("returnObject.userName== authentication.name") //<- chạy hàm trước rồi kiểm tra xem id có đúng không ,chỉ có id của chính mình mới xem được
   @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN','STAFF')")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHang>> detail(@PathVariable("id") Integer id) {
        ApiResponse<KhachHang> response = new ApiResponse<>();
        // user
        String taiKhoan=SecurityContextHolder.getContext().getAuthentication().getName();
        // admin
        boolean isAdmin=SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a ->a.getAuthority().equals("ROLE_"+ Role.ADMIN.name()) );


        Optional<KhachHang> userOptional = serviceUser.detail(id);
        if (userOptional.isEmpty()) {
            response.setCode(ErrorCode.USER_KhongTonTai.getCode());
            response.setMessage(ErrorCode.USER_KhongTonTai.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        KhachHang kh=userOptional.get();
        if (!isAdmin && !kh.getTenTaiKhoan().equals(taiKhoan)){
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        response.setResult(userOptional.get());
        return ResponseEntity.ok(response);
    }

    // lay du lieu cua minh = token
    @GetMapping("/myInfo")
    public ResponseEntity<ApiResponse<KhachHang>> getMyInfo() {
        KhachHang currentUser = serviceUser.getMyInfo();
        ApiResponse<KhachHang> response = new ApiResponse<>();
        ApiResponse<KhachHang> Response = ApiResponse.<KhachHang>builder()
                .code(response.getCode())
                .result(currentUser)
                .build();
        return ResponseEntity.ok(Response);
    }


    @PreAuthorize("hasAnyRole('ADMIN','STAFF')")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHang>> delete(@PathVariable("id") Integer id) {
        ApiResponse<KhachHang> response = new ApiResponse<>();

        Optional<KhachHang> userOptional = serviceUser.detail(id);
        if (userOptional.isEmpty()) {
            response.setCode(ErrorCode.USER_KhongTonTai.getCode());
            response.setMessage(ErrorCode.USER_KhongTonTai.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        serviceUser.delete(id);
        return ResponseEntity.ok(
                ApiResponse.<KhachHang>builder()
                        .code(200)
                        .message("Xóa thành công")
                        .build()
        );
    }


    @PreAuthorize("hasAnyRole('ADMIN','STAFF','CUSTOMER')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<KhachHangDTO>> updateUser(
            @PathVariable("id") Integer id,
            @Valid @RequestBody UpdateKhachHang updateKh) {

        ApiResponse<KhachHangDTO> response = new ApiResponse<>();

        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + Role.ADMIN.name())
                        || a.getAuthority().equals("ROLE_" + Role.STAFF.name()));

        Optional<KhachHang> userOptional = serviceUser.detail(id);
        if (userOptional.isEmpty()) {
            response.setCode(ErrorCode.USER_KhongTonTai.getCode());
            response.setMessage(ErrorCode.USER_KhongTonTai.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        KhachHang user = userOptional.get();
        if (!isAdmin && !user.getTenTaiKhoan().equals(userName)) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }

        KhachHang updated = serviceUser.updateRequest(id, updateKh);
        KhachHangDTO dto = userMapper.toDTO(updated);

        return ResponseEntity.ok(
                ApiResponse.<KhachHangDTO>builder()
                        .code(response.getCode())
                        .message("Sửa thành công")
                        .result(dto)
                        .build()
        );
    }



}
