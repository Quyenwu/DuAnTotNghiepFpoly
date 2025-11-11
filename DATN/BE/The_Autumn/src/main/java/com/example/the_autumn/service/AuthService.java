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
        NhanVien nv = new NhanVien();
        nv.setHoTen(req.getHoTen());
        nv.setEmail(req.getEmail());
        nv.setMatKhau(passwordEncoder.encode(req.getMatKhau()));
        nv.setDiaChi(req.getDiaChi());
        nv.setSdt(req.getSdt());
        nv.setTrangThai(true);
        nv.setNgayTao(new Date());
        ChucVu chucVu = new ChucVu();
        chucVu.setId(2);
        nv.setChucVu(chucVu);

        nhanVienRepository.save(nv);
    }

    public NhanVienResponse login(NhanVienRequest req) {
        Authentication authentication = authenticationProvider.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getMatKhau())
        );

        UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
        String token = jwtProvider.generateToken(userPrinciple);

        NhanVienResponse response = new NhanVienResponse();
        response.setEmail(userPrinciple.getUsername());
        response.setChucVuName(userPrinciple.getUser().getChucVu().getTenChucVu());
        response.setHoTen(userPrinciple.getUser().getHoTen());

        response.setAccessToken(token);
        response.setTypeToken("Bearer");

        return response;
    }
}
