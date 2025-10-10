package com.example.the_autumn.config;

import com.example.the_autumn.model.request.ApiResponse;
import com.example.the_autumn.expection.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

// xây dựng lỗi cho user khi đăng nhập vơi token sai :test api/user/myInfo
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {
        // gọi lỗi ở errorcode
        ErrorCode errorCode=ErrorCode.UNAUTHENTICATED;

        // trả về nội dung và kiểu
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        // build lỗi trả về
        ApiResponse<?>apiResponse=ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();


        // chuyển để đối tương java thành chuỗi json và ghi nó vào phần body
        ObjectMapper objectMapper=new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
        response.flushBuffer();
    }
}
