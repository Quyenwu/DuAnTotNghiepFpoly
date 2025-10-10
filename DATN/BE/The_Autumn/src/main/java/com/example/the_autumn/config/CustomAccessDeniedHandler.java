package com.example.the_autumn.config;

import com.example.the_autumn.model.request.ApiResponse;
import com.example.the_autumn.expection.ErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
// đăng nhập vơi token user trả về lỗi không truy cập được test: api/user
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
// gọi lỗi
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED;
// trả về trạng thái kiểu
        response.setStatus(errorCode.getStatusCode().value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//build trả về
        ApiResponse<?> apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
//chuyển đồi đối tượng java sang chuỗi json
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(apiResponse));//->hiện ở body
        response.flushBuffer();//->đẩy hết dữ liệu trong ApiResponse ra không dữ lại
    }
}
