package com.example.the_autumn.service;

import com.example.the_autumn.entity.CoAo;
import com.example.the_autumn.model.request.CoAoRequest;
import com.example.the_autumn.model.response.CoAoResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.CoAoRepository;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CoAoService {

    @Autowired
    private CoAoRepository coAoRepo;

    public List<CoAoResponse> findAll() {
        return coAoRepo.findAll()
                .stream()
                .map(CoAoResponse::new)
                .toList();
    }

    public void add(CoAoRequest request) {
        CoAo coAo = MapperUtils.map(request, CoAo.class);
        coAo.setTrangThai(true);
        coAo.setNgayTao(new Date());
        coAoRepo.save(coAo);
    }

    public List<CoAoResponse> findByName(String name) {
        return coAoRepo.findByTenCoAoContainingIgnoreCase(name)
                .stream()
                .map(CoAoResponse::new)
                .collect(Collectors.toList());
    }

    public List<CoAoResponse> findByName2(String name) {
        return coAoRepo.findByNameContaining(name)
                .stream()
                .map(CoAoResponse::new)
                .collect(Collectors.toList());
    }

    public PageableObject<CoAoResponse> filterCoAoWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maCoAo,
            String tenCoAo,
            Date ngayTao,
            Boolean trangThai) {

        List<CoAoResponse> filteredList = filterCoAo(
                searchText, maCoAo, tenCoAo, ngayTao, trangThai
        );

        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<CoAoResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<CoAoResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    private List<CoAoResponse> filterCoAo(
            String searchText,
            String maCoAo,
            String tenCoAo,
            Date ngayTao,
            Boolean trangThai) {

        List<CoAo> list = coAoRepo.findAll();

        return list.stream()
                .filter(ca -> searchText == null || searchText.isEmpty() ||
                        (ca.getMaCoAo() != null && ca.getMaCoAo().toLowerCase().contains(searchText.toLowerCase())) ||
                        (ca.getTenCoAo() != null && ca.getTenCoAo().toLowerCase().contains(searchText.toLowerCase())))

                .filter(ca -> maCoAo == null || maCoAo.isEmpty() ||
                        (ca.getMaCoAo() != null && ca.getMaCoAo().toLowerCase().contains(maCoAo.toLowerCase())))

                .filter(ca -> tenCoAo == null || tenCoAo.isEmpty() ||
                        (ca.getTenCoAo() != null && ca.getTenCoAo().toLowerCase().contains(tenCoAo.toLowerCase())))

                .filter(ca -> ngayTao == null ||
                        (ca.getNgayTao() != null && !ca.getNgayTao().before(ngayTao)))

                .filter(ca -> trangThai == null ||
                        (ca.getTrangThai() != null && ca.getTrangThai().equals(trangThai)))

                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(CoAoResponse::new)

                .collect(Collectors.toList());
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        CoAo ca = coAoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy cổ áo với ID: " + id));

        ca.setTrangThai(trangThai);
        ca.setNgaySua(new Date());

        coAoRepo.save(ca);
    }
}
