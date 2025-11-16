package com.example.the_autumn.security;


import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.security.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.context.annotation.Primary;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Primary
public class UserDetailService implements UserDetailsService {

    @Autowired
    private NhanVienRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("Loading user by email: " + email);

        NhanVien user = userRepository.getNhanVienByEmail(email);
        if (user == null) {
            System.err.println("User not found with email: " + email);
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        System.out.println("User found: " + user.getEmail());
        System.out.println("User status: " + user.getTrangThai());

        UserPrinciple userPrinciple = new UserPrinciple();
        userPrinciple.setUser(user);

        if (user.getChucVu() != null) {
            userPrinciple.setAuthorities(
                    Set.of(new SimpleGrantedAuthority(user.getChucVu().getTenChucVu()))
            );
            System.out.println("User role: " + user.getChucVu().getTenChucVu());
        } else {
            userPrinciple.setAuthorities(Set.of(new SimpleGrantedAuthority("USER")));
            System.out.println("No role assigned, using default: USER");
        }

        return userPrinciple;
    }
}