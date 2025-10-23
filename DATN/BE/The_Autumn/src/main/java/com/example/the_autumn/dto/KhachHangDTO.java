package com.example.the_autumn.dto;

import com.example.the_autumn.entity.KhachHang;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KhachHangDTO {
    private Integer id;
    private String hoTen;          // "Nguyễn Minh Khang"
    private Boolean gioiTinh;      // Nam/Nu
    private String sdt;            // "090111222"
    private String email;          // "nguyen31200@gmail.com"
    // tenTaiKhoan: Skip (sensitive, nếu cần thêm: private String tenTaiKhoan;)
    // matKhau: Skip hoàn toàn
    private Date ngayTao;          // Date tạo
    private Date ngaySua;          // Date sửa
    private Boolean trangThai;     // true/false

    // ⭐ Constructor mapping ĐẦY ĐỦ từ entity (selective: skip sensitive/lists)
    public KhachHangDTO(KhachHang khachHang) {
        if (khachHang == null) {
            return;  // Null-safe: DTO rỗng nếu entity null
        }
        this.id = khachHang.getId();
        this.hoTen = khachHang.getHoTen();
        this.gioiTinh = khachHang.getGioiTinh();
        this.sdt = khachHang.getSdt();
        this.email = khachHang.getEmail();
        // Skip tenTaiKhoan/matKhau (sensitive)
        this.ngayTao = khachHang.getNgayTao();
        this.ngaySua = khachHang.getNgaySua();
        this.trangThai = khachHang.getTrangThai();
        // Skip lists (diaChi, hoaDons, etc.) – nếu cần map diaChi, thêm field List<DiaChiDTO> và map tương tự
    }
}