package com.example.the_autumn.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "USERNAME_KhongDeTrong")
    String tenTaiKhoan;

    @NotBlank(message = "PASSWORD_KhongDeTrong")
    @Size(min = 6, max = 20, message = "PASSWORD_ToiThieu")
    String matKhau;

}
