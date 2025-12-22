package com.example.the_autumn.service;

import com.example.the_autumn.entity.XuatXu;
import com.example.the_autumn.model.request.XuatXuRequest;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.XuatXuResponse;
import com.example.the_autumn.repository.XuatXuRepository;
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
public class XuatXuService {

    @Autowired
    private XuatXuRepository xxRepo;

    public List<XuatXuResponse> findAll() {
        return xxRepo.findAll()
                .stream()
                .map(XuatXuResponse::new)
                .toList();
    }

    public void add(XuatXuRequest request) {
        XuatXu xuatXu = MapperUtils.map(request, XuatXu.class);
        xuatXu.setTrangThai(true);
        xuatXu.setNgayTao(new Date());
        xxRepo.save(xuatXu);
    }

    public List<XuatXuResponse> findByName(String name) {
        return xxRepo.findByTenXuatXuContainingIgnoreCase(name)
                .stream()
                .map(XuatXuResponse::new)
                .collect(Collectors.toList());
    }

    public List<XuatXuResponse> findByName2(String name) {
        return xxRepo.findByNameContaining(name)
                .stream()
                .map(XuatXuResponse::new)
                .collect(Collectors.toList());
    }

    public PageableObject<XuatXuResponse> filterXuatXuWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maXuatXu,
            String tenXuatXu,
            Date ngayTao,
            Boolean trangThai) {

        List<XuatXuResponse> filteredList = filterXuatXu(
                searchText, maXuatXu, tenXuatXu, ngayTao, trangThai
        );

        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<XuatXuResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<XuatXuResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    private List<XuatXuResponse> filterXuatXu(
            String searchText,
            String maXuatXu,
            String tenXuatXu,
            Date ngayTao,
            Boolean trangThai) {

        List<XuatXu> list = xxRepo.findAll();

        return list.stream()
                .filter(xx -> searchText == null || searchText.isEmpty() ||
                        (xx.getMaXuatXu() != null && xx.getMaXuatXu().toLowerCase().contains(searchText.toLowerCase())) ||
                        (xx.getTenXuatXu() != null && xx.getTenXuatXu().toLowerCase().contains(searchText.toLowerCase())))

                .filter(xx -> maXuatXu == null || maXuatXu.isEmpty() ||
                        (xx.getMaXuatXu() != null && xx.getMaXuatXu().toLowerCase().contains(maXuatXu.toLowerCase())))

                .filter(xx -> tenXuatXu == null || tenXuatXu.isEmpty() ||
                        (xx.getTenXuatXu() != null && xx.getTenXuatXu().toLowerCase().contains(tenXuatXu.toLowerCase())))

                .filter(xx -> ngayTao == null ||
                        (xx.getNgayTao() != null && !xx.getNgayTao().before(ngayTao)))

                .filter(xx -> trangThai == null ||
                        (xx.getTrangThai() != null && xx.getTrangThai().equals(trangThai)))

                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(XuatXuResponse::new)

                .collect(Collectors.toList());
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        XuatXu xx = xxRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xuất xứ với ID: " + id));

        xx.setTrangThai(trangThai);
        xx.setNgaySua(new Date());

        xxRepo.save(xx);
    }
}
