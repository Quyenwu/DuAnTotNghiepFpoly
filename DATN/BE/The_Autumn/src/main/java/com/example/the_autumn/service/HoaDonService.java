package com.example.the_autumn.service;

import com.example.the_autumn.dto.HoaDonDTO;
import com.example.the_autumn.dto.HoaDonDetailDTO;
import com.example.the_autumn.dto.PageResponseDTO;
import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.model.request.UpdateHoaDonRequest;
import com.example.the_autumn.model.response.UpdateHoaDonResponse;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface HoaDonService {

    PageResponseDTO<HoaDonDTO> getAll(Pageable pageable);
    Optional<HoaDon> getById(Integer id);

    byte[] printInvoices(List<Integer> invoiceIds) throws Exception;

    PageResponseDTO<HoaDonDTO> timkiemVaLoc(
            String maHoaDon,
            String tenKhachHang,
            String tenNhanVien,
            Boolean loaiHoaDon,
            Integer trangThai,
            LocalDate ngayTao,
            String priceRange,
            int page,
            int size
    );

    HoaDonDetailDTO getHoaDonDetail(Integer id);

    UpdateHoaDonResponse updateHoaDon(Integer idHoaDon, UpdateHoaDonRequest request);

    boolean canEdit(Integer idHoaDon);

    Optional<HoaDon> findById(Integer id);
    HoaDon save(HoaDon hoaDon);



//    void sendInvoiceEmail(Integer idHoaDon, String email) throws MessagingException;
}

//
