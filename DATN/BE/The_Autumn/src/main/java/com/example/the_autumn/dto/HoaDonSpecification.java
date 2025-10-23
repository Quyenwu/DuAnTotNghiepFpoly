package com.example.the_autumn.dto;

import com.example.the_autumn.entity.HoaDon;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class HoaDonSpecification {
    public static Specification<HoaDon> filterBy(
            String maHoaDon,
            String tenKhachHang,
            String tenNhanVien,
            Boolean loaiHoaDon,
            Integer trangThai,
            Date ngayTao,
            Double minPrice,
            Double maxPrice
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Tìm theo mã hóa đơn
            if (maHoaDon != null && !maHoaDon.isEmpty()) {
                predicates.add(criteriaBuilder.like(
                        root.get("maHoaDon"), "%" + maHoaDon + "%"
                ));
            }

            // Tìm theo tên khách hàng
            if (tenKhachHang != null && !tenKhachHang.isEmpty()) {
                Join<Object, Object> khachHangJoin = root.join("khachHang", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        khachHangJoin.get("hoTen"), "%" + tenKhachHang + "%"
                ));
            }



            // Tìm theo tên nhân viên
            if (tenNhanVien != null && !tenNhanVien.isEmpty()) {
                Join<Object, Object> nhanVienJoin = root.join("nhanVien", JoinType.LEFT);
                predicates.add(criteriaBuilder.like(
                        nhanVienJoin.get("hoTen"), "%" + tenNhanVien + "%"
                ));
            }

            // Lọc theo loại hóa đơn
            if (loaiHoaDon != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("loaiHoaDon "), loaiHoaDon
                ));
            }



            // Lọc theo trạng thái
            if (trangThai != null) {
                predicates.add(criteriaBuilder.equal(
                        root.get("trangThai"), trangThai
                ));
            }

            // Lọc theo ngày tạo
            if (ngayTao != null) {
                predicates.add(criteriaBuilder.equal(
                        criteriaBuilder.function("DATE", LocalDateTime.class, root.get("ngayTao")),
                        criteriaBuilder.function("DATE", LocalDateTime.class, criteriaBuilder.literal(ngayTao))
                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };


    }
}
