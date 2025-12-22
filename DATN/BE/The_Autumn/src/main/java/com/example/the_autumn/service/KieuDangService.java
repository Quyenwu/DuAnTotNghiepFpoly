package com.example.the_autumn.service;

import com.example.the_autumn.entity.KieuDang;
import com.example.the_autumn.model.request.KieuDangRequest;
import com.example.the_autumn.model.response.KieuDangResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.KieuDangRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class KieuDangService {

    @Autowired
    private KieuDangRepository kieuDangRepo;

    public List<KieuDangResponse> findAll() {
        return kieuDangRepo.findAll()
                .stream()
                .map(KieuDangResponse::new)
                .toList();
    }

    public void add(KieuDangRequest request) {
        KieuDang kieuDang = MapperUtils.map(request, KieuDang.class);
        kieuDang.setTrangThai(true);
        kieuDang.setNgayTao(new Date());
        kieuDangRepo.save(kieuDang);
    }

    public List<KieuDangResponse> findByName(String name) {
        return kieuDangRepo.findByTenKieuDangContainingIgnoreCase(name)
                .stream()
                .map(KieuDangResponse::new)
                .collect(Collectors.toList());
    }

    public List<KieuDangResponse> findByName2(String name) {
        return kieuDangRepo.findByNameContaining(name)
                .stream()
                .map(KieuDangResponse::new)
                .collect(Collectors.toList());
    }

    public PageableObject<KieuDangResponse> filterKieuDangWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maKieuDang,
            String tenKieuDang,
            Date ngayTao,
            Boolean trangThai) {

        List<KieuDangResponse> filteredList = filterKieuDang(
                searchText, maKieuDang, tenKieuDang, ngayTao, trangThai
        );

        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<KieuDangResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<KieuDangResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    private List<KieuDangResponse> filterKieuDang(
            String searchText,
            String maKieuDang,
            String tenKieuDang,
            Date ngayTao,
            Boolean trangThai) {

        List<KieuDang> list = kieuDangRepo.findAll();

        return list.stream()
                .filter(kd -> searchText == null || searchText.isEmpty() ||
                        (kd.getMaKieuDang() != null && kd.getMaKieuDang().toLowerCase().contains(searchText.toLowerCase())) ||
                        (kd.getTenKieuDang() != null && kd.getTenKieuDang().toLowerCase().contains(searchText.toLowerCase())))

                .filter(kd -> maKieuDang == null || maKieuDang.isEmpty() ||
                        (kd.getMaKieuDang() != null && kd.getMaKieuDang().toLowerCase().contains(maKieuDang.toLowerCase())))

                .filter(kd -> tenKieuDang == null || tenKieuDang.isEmpty() ||
                        (kd.getTenKieuDang() != null && kd.getTenKieuDang().toLowerCase().contains(tenKieuDang.toLowerCase())))

                .filter(kd -> ngayTao == null ||
                        (kd.getNgayTao() != null && !kd.getNgayTao().before(ngayTao)))

                .filter(kd -> trangThai == null ||
                        (kd.getTrangThai() != null && kd.getTrangThai().equals(trangThai)))

                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(KieuDangResponse::new)

                .collect(Collectors.toList());
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        KieuDang kd = kieuDangRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kiểu dáng với ID: " + id));

        kd.setTrangThai(trangThai);
        kd.setNgaySua(new Date());

        kieuDangRepo.save(kd);
    }
}
