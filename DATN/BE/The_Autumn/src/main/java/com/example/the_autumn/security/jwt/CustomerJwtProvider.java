package com.example.the_autumn.security.jwt;

import com.example.the_autumn.security.CustomerPrinciple;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
public class CustomerJwtProvider {

    @Value("${customer.jwt.expired:86400000}")
    private Long EXPIRED;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        log.info("Customer JWT Secret Key initialized");
    }

    public String generateToken(CustomerPrinciple customerPrinciple) {
        Date dateExpiration = new Date(System.currentTimeMillis() + EXPIRED);

        Map<String, Object> claims = new HashMap<>();
        claims.put("customerId", customerPrinciple.getCustomer().getId());
        claims.put("maKhachHang", customerPrinciple.getCustomer().getMaKhachHang());
        claims.put("fullName", customerPrinciple.getCustomer().getHoTen());
        claims.put("email", customerPrinciple.getCustomer().getEmail());
        claims.put("phone", customerPrinciple.getCustomer().getSdt());
        claims.put("gioiTinh", customerPrinciple.getCustomer().getGioiTinh());
        claims.put("ngaySinh", customerPrinciple.getCustomer().getNgaySinh());
        claims.put("status", customerPrinciple.getCustomer().getTrangThai());
        claims.put("createdDate", customerPrinciple.getCustomer().getNgayTao());
        claims.put("userType", "CUSTOMER");

        claims.put("authorities", customerPrinciple.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(customerPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(dateExpiration)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public Boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (SecurityException e) {
            log.error("JWT signature validation failed: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("JWT validation error: {}", e.getMessage());
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public Date getExpirationFromToken(String token) {
        return getAllClaimsFromToken(token).getExpiration();
    }

    public CustomerInfo getCustomerInfoFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        CustomerInfo customerInfo = new CustomerInfo();
        customerInfo.setCustomerId(claims.get("customerId", Integer.class));
        customerInfo.setMaKhachHang(claims.get("maKhachHang", String.class));
        customerInfo.setUsername(claims.getSubject());
        customerInfo.setEmail(claims.get("email", String.class));
        customerInfo.setFullName(claims.get("fullName", String.class));
        customerInfo.setPhone(claims.get("phone", String.class));
        customerInfo.setGioiTinh(claims.get("gioiTinh", Boolean.class));
        customerInfo.setNgaySinh(claims.get("ngaySinh", Date.class));
        customerInfo.setStatus(claims.get("status", Boolean.class));
        customerInfo.setCreatedDate(claims.get("createdDate", Date.class));
        customerInfo.setUserType(claims.get("userType", String.class));

        List<String> authorities = claims.get("authorities", List.class);
        customerInfo.setAuthorities(authorities != null ? authorities : Collections.emptyList());

        return customerInfo;
    }

    public Integer getCustomerIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("customerId", Integer.class);
    }

    public List<String> getAuthoritiesFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        List<String> authorities = claims.get("authorities", List.class);
        return authorities != null ? authorities : Collections.emptyList();
    }

    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.before(new Date());
        } catch (Exception e) {
            log.error("Error checking token expiration: {}", e.getMessage());
            return true;
        }
    }

    public Long getRemainingTime(String token) {
        try {
            Date expiration = getExpirationFromToken(token);
            return expiration.getTime() - System.currentTimeMillis();
        } catch (Exception e) {
            log.error("Error getting remaining time: {}", e.getMessage());
            return 0L;
        }
    }

    public String refreshToken(String oldToken) {
        try {
            Claims claims = getAllClaimsFromToken(oldToken);
            Date expiration = new Date(System.currentTimeMillis() + EXPIRED);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(claims.getSubject())
                    .setIssuedAt(new Date())
                    .setExpiration(expiration)
                    .signWith(secretKey, SignatureAlgorithm.HS512)
                    .compact();
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new RuntimeException("Cannot refresh token");
        }
    }

    public CustomerInfo verifyToken(String token) {
        if (!validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        if (isTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }

        return getCustomerInfoFromToken(token);
    }

    @Getter
    @Setter
    public static class CustomerInfo {
        private Integer customerId;
        private String maKhachHang;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private Boolean gioiTinh;
        private Date ngaySinh;
        private Boolean status;
        private Date createdDate;
        private String userType;
        private List<String> authorities = new ArrayList<>();

        @Override
        public String toString() {
            return "CustomerInfo{" +
                    "customerId=" + customerId +
                    ", maKhachHang='" + maKhachHang + '\'' +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", phone='" + phone + '\'' +
                    ", userType='" + userType + '\'' +
                    ", authorities=" + authorities +
                    '}';
        }
    }
}