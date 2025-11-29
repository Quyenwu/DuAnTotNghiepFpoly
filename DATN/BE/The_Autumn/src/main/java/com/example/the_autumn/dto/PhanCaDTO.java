package com.example.the_autumn.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhanCaDTO {
    private Integer id;

    private String maPhanCa;          // server tự sinh, không cần validate từ client

    @NotNull(message = "Nhân viên không được để trống")
    @Positive(message = "Mã nhân viên phải là số dương")
    private Integer idNhanVien;

    private String hoTenNhanVien;     // trả ra cho FE, không bắt client gửi

    @NotNull(message = "Ca làm việc không được để trống")
    @Positive(message = "Mã ca làm việc phải là số dương")
    private Integer idCaLamViec;

    private String tenCa;

    private String gioBatDau;

    private String gioKetThuc;

    @NotBlank(message = "Ngày phân ca không được để trống")
    @Pattern(
            regexp = "\\d{4}-\\d{2}-\\d{2}",
            message = "Ngày phân ca phải đúng định dạng yyyy-MM-dd"
    )
    private String ngayPhanCa;

    @Size(max = 500, message = "Ghi chú không được vượt quá 500 ký tự")
    private String ghiChu;

    // Cho phép null để BE default = true nếu null
    private Boolean trangThai;
}
