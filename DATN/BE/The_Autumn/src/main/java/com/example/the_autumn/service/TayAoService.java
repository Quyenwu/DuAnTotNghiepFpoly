package com.example.the_autumn.service;

import com.example.the_autumn.entity.TayAo;
import com.example.the_autumn.model.request.TayAoRequest;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.TayAoResponse;
import com.example.the_autumn.repository.TayAoRepository;
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
public class TayAoService {

    @Autowired
    private TayAoRepository tayAoRepo;

    public List<TayAoResponse> findAll() {
        return tayAoRepo.findAll()
                .stream()
                .map(TayAoResponse::new)
                .toList();
    }

    public void add(TayAoRequest request) {
        TayAo tayAo = MapperUtils.map(request, TayAo.class);
        tayAo.setTrangThai(true);
        tayAo.setNgayTao(new Date());
        tayAoRepo.save(tayAo);
    }

    public List<TayAoResponse> findByName(String name) {
        return tayAoRepo.findByTenTayAoContainingIgnoreCase(name)
                .stream()
                .map(TayAoResponse::new)
                .collect(Collectors.toList());
    }

    public List<TayAoResponse> findByName2(String name) {
        return tayAoRepo.findByNameContaining(name)
                .stream()
                .map(TayAoResponse::new)
                .collect(Collectors.toList());
    }

    public PageableObject<TayAoResponse> filterTayAoWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maTayAo,
            String tenTayAo,
            Date ngayTao,
            Boolean trangThai) {

        List<TayAoResponse> filteredList = filterTayAo(
                searchText, maTayAo, tenTayAo, ngayTao, trangThai
        );

        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        List<TayAoResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();

        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<TayAoResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    private List<TayAoResponse> filterTayAo(
            String searchText,
            String maTayAo,
            String tenTayAo,
            Date ngayTao,
            Boolean trangThai) {

        List<TayAo> list = tayAoRepo.findAll();

        return list.stream()
                .filter(ta -> searchText == null || searchText.isEmpty() ||
                        (ta.getMaTayAo() != null && ta.getMaTayAo().toLowerCase().contains(searchText.toLowerCase())) ||
                        (ta.getTenTayAo() != null && ta.getTenTayAo().toLowerCase().contains(searchText.toLowerCase())))

                .filter(ta -> maTayAo == null || maTayAo.isEmpty() ||
                        (ta.getMaTayAo() != null && ta.getMaTayAo().toLowerCase().contains(maTayAo.toLowerCase())))

                .filter(ta -> tenTayAo == null || tenTayAo.isEmpty() ||
                        (ta.getTenTayAo() != null && ta.getTenTayAo().toLowerCase().contains(tenTayAo.toLowerCase())))

                .filter(ta -> ngayTao == null ||
                        (ta.getNgayTao() != null && !ta.getNgayTao().before(ngayTao)))

                .filter(ta -> trangThai == null ||
                        (ta.getTrangThai() != null && ta.getTrangThai().equals(trangThai)))

                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(TayAoResponse::new)

                .collect(Collectors.toList());
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        TayAo ta = tayAoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tay áo với ID: " + id));

        ta.setTrangThai(trangThai);
        ta.setNgaySua(new Date());

        tayAoRepo.save(ta);
    }
}
