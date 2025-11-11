package com.example.the_autumn.security.jwt;

import com.example.the_autumn.security.UserPrinciple;
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
public class JwtProvider {

    @Value("${expired:86400000}")
    private Long EXPIRED;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
        log.info("JWT Secret Key initialized");
    }

    public String generateToken(UserPrinciple userPrinciple) {
        Date dateExpiration = new Date(System.currentTimeMillis() + EXPIRED);

        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userPrinciple.getUser().getId());
        claims.put("fullName", userPrinciple.getUser().getHoTen());
        claims.put("email", userPrinciple.getUser().getEmail());
        claims.put("phone", userPrinciple.getUser().getSdt());
        claims.put("address", userPrinciple.getUser().getDiaChi());
        claims.put("status", userPrinciple.getUser().getTrangThai());
        claims.put("createdDate", userPrinciple.getUser().getNgayTao());

        claims.put("maNhanVien", userPrinciple.getUser().getMaNhanVien());
        claims.put("gioiTinh", userPrinciple.getUser().getGioiTinh());
        claims.put("ngaySinh", userPrinciple.getUser().getNgaySinh());
        claims.put("cccd", userPrinciple.getUser().getCccd());
        claims.put("hinhAnh", userPrinciple.getUser().getHinhAnh());

        if (userPrinciple.getUser().getChucVu() != null) {
            claims.put("position", userPrinciple.getUser().getChucVu().getTenChucVu());
            claims.put("positionId", userPrinciple.getUser().getChucVu().getId());
        }

        claims.put("authorities", userPrinciple.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrinciple.getUsername())
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

    public UserInfo getUserInfoFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);

        UserInfo userInfo = new UserInfo();
        userInfo.setUserId(claims.get("userId", String.class));
        userInfo.setUsername(claims.getSubject());
        userInfo.setEmail(claims.get("email", String.class));
        userInfo.setFullName(claims.get("fullName", String.class));
        userInfo.setPhone(claims.get("phone", String.class));
        userInfo.setAddress(claims.get("address", String.class));
        userInfo.setStatus(claims.get("status", Boolean.class));
        userInfo.setCreatedDate(claims.get("createdDate", Date.class));
        userInfo.setPosition(claims.get("position", String.class));
        userInfo.setPositionId(claims.get("positionId", Integer.class));

        userInfo.setMaNhanVien(claims.get("maNhanVien", String.class));
        userInfo.setGioiTinh(claims.get("gioiTinh", Boolean.class));
        userInfo.setNgaySinh(claims.get("ngaySinh", Date.class));
        userInfo.setCccd(claims.get("cccd", String.class));
        userInfo.setHinhAnh(claims.get("hinhAnh", String.class));

        List<String> authorities = claims.get("authorities", List.class);
        userInfo.setAuthorities(authorities != null ? authorities : Collections.emptyList());

        return userInfo;
    }

    public String getUserIdFromToken(String token) {
        return getAllClaimsFromToken(token).get("userId", String.class);
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

    @Getter
    @Setter
    public static class UserInfo {
        private String userId;
        private String username;
        private String email;
        private String fullName;
        private String phone;
        private String address;
        private Boolean status;
        private Date createdDate;
        private String position;
        private Integer positionId;
        private List<String> authorities = new ArrayList<>();

        private String maNhanVien;
        private Boolean gioiTinh;
        private Date ngaySinh;
        private String cccd;
        private String hinhAnh;

        @Override
        public String toString() {
            return "UserInfo{" +
                    "userId='" + userId + '\'' +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", fullName='" + fullName + '\'' +
                    ", maNhanVien='" + maNhanVien + '\'' +
                    ", position='" + position + '\'' +
                    ", authorities=" + authorities +
                    '}';
        }
    }

    public String generateTokenWithCustomClaims(Map<String, Object> customClaims, UserPrinciple userPrinciple) {
        Date dateExpiration = new Date(System.currentTimeMillis() + EXPIRED);

        Map<String, Object> claims = new HashMap<>();

        claims.put("userId", userPrinciple.getUser().getId());
        claims.put("fullName", userPrinciple.getUser().getHoTen());
        claims.put("email", userPrinciple.getUser().getEmail());
        claims.put("authorities", userPrinciple.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        if (customClaims != null) {
            claims.putAll(customClaims);
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userPrinciple.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(dateExpiration)
                .signWith(secretKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public UserInfo verifyToken(String token) {
        if (!validateToken(token)) {
            throw new RuntimeException("Invalid token");
        }

        if (isTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }

        return getUserInfoFromToken(token);
    }

    public Map<String, Object> extractAllClaimsAsMap(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return new HashMap<>(claims);
    }
}