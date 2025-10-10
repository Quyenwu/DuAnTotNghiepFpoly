package com.example.the_autumn.mapper;

import com.example.the_autumn.model.request.CreateNhanVien;
import com.example.the_autumn.model.request.UpdateNhanVien;
import com.example.the_autumn.model.response.NhanVienDTO;
import com.example.the_autumn.entity.NhanVien;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface nhanVienMapper {
    NhanVien toUser(CreateNhanVien dto);
    CreateNhanVien toUserResponse(NhanVien nv);
    void updateUser(@MappingTarget NhanVien nv , UpdateNhanVien u);
    NhanVienDTO toDTO(NhanVien entity);
}
