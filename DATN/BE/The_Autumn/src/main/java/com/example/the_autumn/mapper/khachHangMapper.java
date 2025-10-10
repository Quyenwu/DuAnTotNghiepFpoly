package com.example.the_autumn.mapper;

import com.example.the_autumn.model.request.CreateKhachHang;
import com.example.the_autumn.model.request.UpdateKhachHang;
import com.example.the_autumn.model.response.KhachHangDTO;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.CreateKhachHang;
import com.example.the_autumn.model.request.UpdateKhachHang;
import com.example.the_autumn.model.response.KhachHangDTO;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface khachHangMapper {

    // Dùng khi tạo mới khách hàng
    KhachHang toEntity(CreateKhachHang dto);

    // Dùng khi muốn trả ngược lại cho client sau khi tạo
    CreateKhachHang toCreateResponse(KhachHang entity);

    // Dùng khi cập nhật (chỉ map những field có giá trị khác null)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget KhachHang entity, UpdateKhachHang dto);

    // Dùng để trả dữ liệu về client (ví dụ khi getAll hoặc getById)
    KhachHangDTO toDTO(KhachHang entity);

}

