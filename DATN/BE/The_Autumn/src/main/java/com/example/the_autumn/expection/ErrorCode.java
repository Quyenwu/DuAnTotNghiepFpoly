package com.example.the_autumn.expection;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // === SYSTEM ===
    System_Exception(2000, "Lỗi hệ thống", HttpStatus.INTERNAL_SERVER_ERROR), // 500

    // === USER ERROR ===
    USER_KhongTonTai(1001, "USER không tồn tại", HttpStatus.NOT_FOUND), // 404
    USER_TonTai(1002, "USER đã tồn tại", HttpStatus.BAD_REQUEST), // 400
    USERNAME_ToiThieu(1003, "USERNAME tối thiểu 6 ký tự", HttpStatus.BAD_REQUEST),
    PASSWORD_ToiThieu(1004, "PASSWORD tối thiểu 6 ký tự", HttpStatus.BAD_REQUEST),
    USERNAME_KhongDeTrong(1010, "USERNAME không được để trống", HttpStatus.BAD_REQUEST),
    PASSWORD_KhongDeTrong(1011, "PASSWORD không được để trống", HttpStatus.BAD_REQUEST),
    FULLNAME_KhongDeTrong(1012, "FULLNAME không được để trống", HttpStatus.BAD_REQUEST),
    EMAIL_KhongHopLe(1013, "Email không hợp lệ", HttpStatus.BAD_REQUEST),
    USER_UNAUTHENTICATED(1009, "USER không có quyền sửa userID", HttpStatus.BAD_REQUEST),

    // === AUTH ===
    UNAUTHENTICATED(1005, "Unauthenticated", HttpStatus.UNAUTHORIZED), // token không hợp lệ
    UNAUTHORIZED(1006, "Xin lỗi! Bạn không có quyền truy cập", HttpStatus.FORBIDDEN), // 403

    // === TASK ERROR ===
    TASK_KhongTonTai(1007, "TASK không tồn tại", HttpStatus.NOT_FOUND),
    TASK_TonTai(1008, "TASK đã tồn tại", HttpStatus.BAD_REQUEST),
    TITLE_KhongDeTrong(2001, "Tiêu đề không được để trống", HttpStatus.BAD_REQUEST),
    NOIDUNG_KhongDeTrong(2002, "Nội dung không được để trống", HttpStatus.BAD_REQUEST),
    TRANGTHAI_KhongDeTrong(2003, "Trạng thái không được để trống", HttpStatus.BAD_REQUEST),
    HANCUOI_KhongDeTrong(2004, "Hạn cuối không được để trống", HttpStatus.BAD_REQUEST),
    HANCUOI_KhongDung(2005, "Hạn cuối phải là một ngày trong tương lai", HttpStatus.BAD_REQUEST),
    USERID_KhongDeTrong(2006, "UserID không được để trống", HttpStatus.BAD_REQUEST);

    private int code;
    private String message;
    private HttpStatusCode statusCode;

    ErrorCode(int code, String message,HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode=statusCode;
    }

}
