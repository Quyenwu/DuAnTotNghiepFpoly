package com.example.the_autumn.service;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.expection.AppException;
import com.example.the_autumn.expection.ErrorCode;
import com.example.the_autumn.model.request.LoginRequest;
import com.example.the_autumn.model.response.AuthenticationResponse;
import com.example.the_autumn.model.response.NhanVienResponse;
import com.example.the_autumn.repository.ChucVuRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.Payload;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthenticationService {
    final KhachHangRepository khRepo;
    final NhanVienRepository nvRepo;
    final ChucVuRepository cvRepo;

    //    private static final String key = "01234567890123456789012345678901";
    @Value("${jwt.key}")
    private String key;

    public AuthenticationResponse authenticate(LoginRequest request) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        // kiểm tra tài khoản nhan viên khi đăng nhập
        Optional<NhanVien> nvOpt = nvRepo.findByTaiKhoan(request.getTenTaiKhoan());
        if (nvOpt.isPresent()) {
            NhanVien nv = nvOpt.get();

            boolean authenticated = passwordEncoder.matches(request.getMatKhau(), nv.getMatKhau());
            if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

            String role = nv.getChucVu().getTenChucVu().toUpperCase(); //  "Admin", "Nhân viên"
            String token = generateToken(nv.getTaiKhoan(), role);

            return AuthenticationResponse.builder()
                    .token(token)
                    .role(role)
                    .authenticated(true)
                    .build();
        }
        // kiểm tra tài khoản khách hàng
        Optional<KhachHang> khOpt = khRepo.findByTenTaiKhoan(request.getTenTaiKhoan());
        if (khOpt.isPresent()) {
            KhachHang kh = khOpt.get();

            boolean authenticated = passwordEncoder.matches(request.getMatKhau(), kh.getMatKhau());
            if (!authenticated) throw new AppException(ErrorCode.UNAUTHENTICATED);

            String token = generateToken(kh.getTenTaiKhoan(), "ADMIN");

            return AuthenticationResponse.builder()
                    .token(token)
                    .role("ADMIN")
                    .authenticated(true)
                    .build();
        }
        throw new AppException(ErrorCode.USER_KhongTonTai);
    }


    // ham cach tao  token chung cho kh và nhan vien
    private String generateToken(String username, String role) {
        try {
            // 1. Header
            JWSHeader jwsHeader = new JWSHeader(JWSAlgorithm.HS256);

            // 2. Payload (claims)
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(username)
                    .claim("scope",role)
                    .issuer("TheAutumn.com")
                    .issueTime(new Date())// lay ngay gio hien tai
                    .expirationTime(Date.from(Instant.now().plus(Duration.ofHours(2))))// thoi gian het han cua token
                    .build();

            // 3. Kết hợp header + payload
            Payload payload = new Payload(claimsSet.toJSONObject());
            JWSObject jwsObject = new JWSObject(jwsHeader, payload);

            // 4. Ký bằng key bí mật
            jwsObject.sign(new MACSigner(key.getBytes()));

            // 5. Trả về chuỗi JWT
            return jwsObject.serialize();

        } catch (JOSEException e) {
           throw new RuntimeException("error create token",e);
        }
    }

}
