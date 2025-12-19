package com.example.the_autumn.service;

import com.example.the_autumn.entity.MauSac;
import com.example.the_autumn.model.request.MauSacRequest;
import com.example.the_autumn.model.response.MauSacResponse;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.repository.MauSacRepository;
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
public class MauSacService {

    @Autowired
    private MauSacRepository mauSacRepo;

    // ============ CRUD CƠ BẢN ============

    public List<MauSacResponse> findAll() {
        return mauSacRepo.findAll()
                .stream()
                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
                .map(MauSacResponse::new)
                .collect(Collectors.toList());
    }

    public void add(MauSacRequest request) {
        MauSac ms = MapperUtils.map(request, MauSac.class);
        ms.setNgayTao(new Date());
        ms.setTrangThai(true);
        mauSacRepo.save(ms);
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        MauSac ms = mauSacRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy màu sắc với ID: " + id));

        ms.setTrangThai(trangThai);
        ms.setNgaySua(new Date());

        mauSacRepo.save(ms);
        System.out.println("✅ Đã cập nhật trạng thái màu sắc ID=" + id + " thành: " + trangThai);
    }

    // ============ TÌM KIẾM CƠ BẢN ============

    public List<MauSacResponse> findByName(String name) {
        return mauSacRepo.findByTenMauSacContainingIgnoreCase(name)
                .stream()
                .map(MauSacResponse::new)
                .collect(Collectors.toList());
    }

    // ============ FILTER VỚI PAGINATION ============

    public PageableObject<MauSacResponse> filterMauSacWithPaging(
            Integer pageNo,
            Integer pageSize,
            String searchText,
            String maMauSac,
            String tenMauSac,
            Date ngayTao,
            Boolean trangThai) {

        System.out.println("🔍 Service: filterMauSacWithPaging");
        System.out.println("  - pageNo: " + pageNo + ", pageSize: " + pageSize);
        System.out.println("  - searchText: " + searchText);
        System.out.println("  - maMauSac: " + maMauSac);
        System.out.println("  - tenMauSac: " + tenMauSac);
        System.out.println("  - ngayTao: " + ngayTao);
        System.out.println("  - trangThai: " + trangThai);

        // Bước 1: Lấy danh sách đã filter
        List<MauSacResponse> filteredList = filterMauSac(
                searchText, maMauSac, tenMauSac, ngayTao, trangThai
        );

        System.out.println("✅ Tổng số màu sắc sau filter: " + filteredList.size());

        // Bước 2: Tính toán phân trang
        int totalItems = filteredList.size();
        int fromIndex = pageNo * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, totalItems);

        // Bước 3: Lấy data cho trang hiện tại
        List<MauSacResponse> pageData = fromIndex < totalItems
                ? filteredList.subList(fromIndex, toIndex)
                : List.of();

        System.out.println("📄 Trả về trang " + (pageNo + 1) + ": " + pageData.size() + " màu sắc");

        // Bước 4: Tạo Page object
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<MauSacResponse> page = new PageImpl<>(
                pageData,
                pageable,
                totalItems
        );

        return new PageableObject<>(page);
    }

    // Method filter logic
    private List<MauSacResponse> filterMauSac(
            String searchText,
            String maMauSac,
            String tenMauSac,
            Date ngayTao,
            Boolean trangThai) {

        List<MauSac> list = mauSacRepo.findAll();

        return list.stream()
                // SearchText: tìm kiếm chung
                .filter(ms -> searchText == null || searchText.isEmpty() ||
                        (ms.getMaMauSac() != null && ms.getMaMauSac().toLowerCase().contains(searchText.toLowerCase())) ||
                        (ms.getTenMauSac() != null && ms.getTenMauSac().toLowerCase().contains(searchText.toLowerCase())))

                // Filter theo mã màu sắc
                .filter(ms -> maMauSac == null || maMauSac.isEmpty() ||
                        (ms.getMaMauSac() != null && ms.getMaMauSac().toLowerCase().contains(maMauSac.toLowerCase())))

                // Filter theo tên màu sắc
                .filter(ms -> tenMauSac == null || tenMauSac.isEmpty() ||
                        (ms.getTenMauSac() != null && ms.getTenMauSac().toLowerCase().contains(tenMauSac.toLowerCase())))

                // Filter theo ngày tạo
                .filter(ms -> ngayTao == null ||
                        (ms.getNgayTao() != null && !ms.getNgayTao().before(ngayTao)))

                // Filter theo trạng thái
                .filter(ms -> trangThai == null ||
                        (ms.getTrangThai() != null && ms.getTrangThai().equals(trangThai)))

                // Sắp xếp giảm dần theo ngày tạo
                .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))

                // Map sang response
                .map(MauSacResponse::new)
                .collect(Collectors.toList());
    }

    // ============ SEARCH CŨ (BACKWARD COMPATIBLE) ============

    public PageableObject<MauSacResponse> searchMauSacWithPaging(
            Integer pageNo,
            Integer pageSize,
            String keyword,
            Boolean trangThai) {

        System.out.println("🔍 Service: searchMauSacWithPaging");
        System.out.println("  - keyword: " + keyword);
        System.out.println("  - trangThai: " + trangThai);

        // Sử dụng filter method với các param tương ứng
        return filterMauSacWithPaging(
                pageNo, pageSize,
                keyword,    // searchText = keyword
                null,       // maMauSac = null
                keyword,    // tenMauSac = keyword
                null,       // ngayTao = null
                trangThai
        );
    }
}