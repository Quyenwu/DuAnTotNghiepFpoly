package com.example.the_autumn.config;

import com.example.the_autumn.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.crypto.spec.SecretKeySpec;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final com.example.the_autumn.config.CustomAccessDeniedHandler accessDeniedHandler;
    private final com.example.the_autumn.config.JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Value("${jwt.key}")
    private String key;

    private final String[] PUBLIC_ENDPOINT = {
            "/api/auth/login",
            "/api/auth/register"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                //  Cho phép CORS giữa FE và BE
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                //  Cấu hình quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, PUBLIC_ENDPOINT).permitAll()

                        // KHÁCH HÀNG
                        .requestMatchers(HttpMethod.GET, "/api/khachhang").hasAnyRole(Role.ADMIN.name(), Role.CUSTOMER.name())
                        .requestMatchers(HttpMethod.GET, "/api/khachhang/**").hasAnyRole(Role.ADMIN.name(), Role.CUSTOMER.name())
                        .requestMatchers(HttpMethod.PUT, "/api/khachhang/**").hasAnyRole(Role.ADMIN.name(), Role.CUSTOMER.name())
                        .requestMatchers(HttpMethod.POST, "/api/khachhang").hasAnyRole(Role.ADMIN.name(), Role.CUSTOMER.name())
                        .requestMatchers(HttpMethod.DELETE, "/api/khachhang/**").hasAnyRole(Role.ADMIN.name(),Role.CUSTOMER.name())
                        // NHAN VIEN
//                        .requestMatchers(HttpMethod.GET, "/api/nhanvien").hasAnyRole(Role.ADMIN.name(), Role.STAFF.name())
//                        .requestMatchers(HttpMethod.GET, "/api/nhanvien/**").hasAnyRole(Role.ADMIN.name(), Role.STAFF.name())
//                        .requestMatchers(HttpMethod.PUT, "/api/nhanvien/**").hasAnyRole(Role.ADMIN.name(), Role.STAFF.name())
//                        .requestMatchers(HttpMethod.POST, "/api/nhanvien").hasAnyRole(Role.ADMIN.name(), Role.STAFF.name())
//                        .requestMatchers(HttpMethod.DELETE, "/api/nhanvien/**").hasRole(Role.STAFF.name())

                        // Các API khác cần đăng nhập
                        .anyRequest().authenticated()
                )

                //  Xác thực JWT
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt
                                .decoder(jwtDecoder())
                                .jwtAuthenticationConverter(jwtAuthenticationConverter())
                        )
                        .authenticationEntryPoint(authenticationEntryPoint)
                )

                //  Xử lý lỗi quyền truy cập
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler)
                )

                // Tắt CSRF cho API
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    //  Cấu hình chuyển scope -> role
    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new JwtGrantedAuthoritiesConverter();
        converter.setAuthorityPrefix("ROLE_");
        converter.setAuthoritiesClaimName("scope");

        var jwtConverter = new JwtAuthenticationConverter();
        jwtConverter.setJwtGrantedAuthoritiesConverter(converter);
        return jwtConverter;
    }

    //  Giải mã JWT
    @Bean
    JwtDecoder jwtDecoder() {
        SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HS256");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
    }

    //  Cho phép CORS giữa FE (Vite) và BE
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:5173")); // FE port 5173
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
