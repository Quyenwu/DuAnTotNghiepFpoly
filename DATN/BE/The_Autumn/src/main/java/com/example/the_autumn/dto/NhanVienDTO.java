package com.example.the_autumn.dto;

import com.example.the_autumn.entity.NhanVien;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NhanVienDTO {
    private Integer id;
    private Integer idChucVu;      // Map id từ ChucVu (nếu không có ChucVuDTO)
    // private String tenChucVu;   // Nếu có ChucVuDTO, map tên
    private String maNhanVien;     // "NV001"
    private String hoTen;          // "Trần Văn Nguyễn"
    private Boolean gioiTinh;      // Nam/Nu
    private Date ngaySinh;         // Sinh nhật
    private String email;          // "nguyen31200@gmail.com"
    private String sdt;            // Nếu có trong payload
    private String diaChi;         // "Địa chỉ"
    private String hinhAnh;        // URL ảnh (nếu public)
    // taiKhoan/matKhau: Skip (sensitive)
    private Date ngayTao;          // Date tạo
    private Date ngaySua;          // Date sửa
    private Boolean trangThai;     // true/false

    // ⭐ Constructor mapping ĐẦY ĐỦ từ entity (selective: skip sensitive/lists)
    public NhanVienDTO(NhanVien nhanVien) {
        if (nhanVien == null) {
            return;  // Null-safe
        }
        this.id = nhanVien.getId();
        // Map ChucVu: Chỉ id (đơn giản), hoặc full nếu có ChucVuDTO
        if (nhanVien.getChucVu() != null) {
            this.idChucVu = nhanVien.getChucVu().getId();
            // Nếu có ChucVuDTO: this.chucVu = new ChucVuDTO(nhanVien.getChucVu());
        }
        this.maNhanVien = nhanVien.getMaNhanVien();
        this.hoTen = nhanVien.getHoTen();
        this.gioiTinh = nhanVien.getGioiTinh();
        this.ngaySinh = nhanVien.getNgaySinh();
        this.email = nhanVien.getEmail();
        this.sdt = nhanVien.getSdt();
        this.diaChi = nhanVien.getDiaChi();
        this.hinhAnh = nhanVien.getHinhAnh();
        // Skip taiKhoan/matKhau (sensitive)
        this.ngayTao = nhanVien.getNgayTao();
        this.ngaySua = nhanVien.getNgaySua();
        this.trangThai = nhanVien.getTrangThai();
        // Skip lists (hoaDons, lichSuHoaDons) – nếu cần, thêm field countHoaDon = nhanVien.getHoaDons().size();
    }
}