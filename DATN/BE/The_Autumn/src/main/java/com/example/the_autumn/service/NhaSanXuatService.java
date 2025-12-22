package com.example.the_autumn.service;

import com.example.the_autumn.entity.NhaSanXuat;
import com.example.the_autumn.model.request.NhaSanXuatRequest;
import com.example.the_autumn.model.response.NhaSanXuatResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.NhaSanXuatRepository;
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
public class NhaSanXuatService {

    @Autowired
    private NhaSanXuatRepository nsxRepo;

    public List<NhaSanXuatResponse> findAll() {return nsxRepo.findAll().stream().map(NhaSanXuatResponse::new).toList();}

    public void add(NhaSanXuatRequest request) {
        NhaSanXuat nsx = MapperUtils.map(request,NhaSanXuat.class);
        nsx.setTrangThai(true);
        nsx.setNgayTao(new Date());
        nsxRepo.save(nsx);
    }

    public List<NhaSanXuatResponse> findByName(String name) {
        return nsxRepo.findByTenNhaSanXuatContainingIgnoreCase(name)
                .stream()
                .map(NhaSanXuatResponse::new)
                .collect(Collectors.toList());
    }

    public List<NhaSanXuatResponse> findByName2(String name) {
         return nsxRepo.findByNameContaining(name)
                 .stream()
                 .map(NhaSanXuatResponse::new)
                 .collect(Collectors.toList());
    }

    public PageableObject<NhaSanXuatResponse> filterNhaSanXuatWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maNhaSanXuat,
            String tenNhaSanXuat,
            Date ngayTao,
            Boolean trangThai) {

        List<NhaSanXuatResponse> filteredList = filterNhaSanXuat(
                searchText, maNhaSanXuat, tenNhaSanXuat, ngayTao, trangThai
        );

        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<NhaSanXuatResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<NhaSanXuatResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    private List<NhaSanXuatResponse> filterNhaSanXuat(
            String searchText,
            String maNhaSanXuat,
            String tenNhaSanXuat,
            Date ngayTao,
            Boolean trangThai) {

        List<NhaSanXuat> list = nsxRepo.findAll();

        return list.stream()
                .filter(nsx -> searchText == null || searchText.isEmpty() ||
                        (nsx.getMaNhaSanXuat() != null && nsx.getMaNhaSanXuat().toLowerCase().contains(searchText.toLowerCase())) ||
                        (nsx.getTenNhaSanXuat() != null && nsx.getTenNhaSanXuat().toLowerCase().contains(searchText.toLowerCase())))

                .filter(nsx -> maNhaSanXuat == null || maNhaSanXuat.isEmpty() ||
                        (nsx.getMaNhaSanXuat() != null && nsx.getMaNhaSanXuat().toLowerCase().contains(maNhaSanXuat.toLowerCase())))

                .filter(nsx -> tenNhaSanXuat == null || tenNhaSanXuat.isEmpty() ||
                        (nsx.getTenNhaSanXuat() != null && nsx.getTenNhaSanXuat().toLowerCase().contains(tenNhaSanXuat.toLowerCase())))

                .filter(nsx -> ngayTao == null ||
                        (nsx.getNgayTao() != null && !nsx.getNgayTao().before(ngayTao)))

                .filter(nsx -> trangThai == null ||
                        (nsx.getTrangThai() != null && nsx.getTrangThai().equals(trangThai)))

                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(NhaSanXuatResponse::new)

                .collect(Collectors.toList());
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        NhaSanXuat nsx = nsxRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà sản xuất với ID: " + id));

        nsx.setTrangThai(trangThai);
        nsx.setNgaySua(new Date());

        nsxRepo.save(nsx);
    }
}
