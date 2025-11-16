package com.example.the_autumn.dto;

import com.example.the_autumn.entity.GiamGiaKhachHang;
import lombok.Data;

@Data
public class MaGiamGiaResponse {

    private Integer id;              // ID của GiamGiaKhachHang (không dùng)
    private Integer phieuGiamGiaId;  // ⭐ ID của PhieuGiamGia (GỬI KHI ĐẶT HÀNG)
    private String tenChuongTrinh;   // Tên hiển thị trong dropdown

    public MaGiamGiaResponse(GiamGiaKhachHang giamGiaKhachHang) {
        // ID của GiamGiaKhachHang (bảng trung gian)
        this.id = giamGiaKhachHang.getId();

        // ⭐ ĐÚNG: Lấy ID từ PhieuGiamGia
        this.phieuGiamGiaId = giamGiaKhachHang.getPhieuGiamGia().getId();

        // Tên chương trình
        this.tenChuongTrinh = giamGiaKhachHang.getPhieuGiamGia().getTenChuongTrinh();
    }
}
