package com.example.the_autumn.security.jwt;

import com.example.the_autumn.security.UserPrinciple;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.expression.ExpressionException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class JwtProvider {

    @Value("${expired}")
    private Long EXPIRED;


    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    }

    public String generateToken(UserPrinciple userPrinciple) {
        Date dateExpiration = new Date(System.currentTimeMillis() + EXPIRED);
        return Jwts.builder()
                .setSubject(userPrinciple.getUsername())
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
        } catch (Exception exception) {
            log.error("Exception Authentication {}", exception.getMessage());
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
}