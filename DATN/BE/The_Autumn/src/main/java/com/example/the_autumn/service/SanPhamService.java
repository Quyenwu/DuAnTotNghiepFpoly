package com.example.the_autumn.service;

import com.example.the_autumn.entity.*;
import com.example.the_autumn.model.request.SanPhamRequest;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.SanPhamResponse;
import com.example.the_autumn.repository.*;
import com.example.the_autumn.util.MapperUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SanPhamService {

    @Autowired
    private SanPhamRepository spRepo;

    @Autowired
    private ChiTietSanPhamRepository ctspRepo;

    @Autowired
    private AnhRepository anhRepo;

    @Autowired
    private NhaSanXuatRepository nsxRepo;

    @Autowired
    private XuatXuRepository xxRepo;

    @Autowired
    private ChatLieuRepository clRepo;

    @Autowired
    private KieuDangRepository kdRepo;

    public List<SanPhamResponse> findAll(){return spRepo.findAll().stream()
            .sorted((a, b) -> b.getNgayTao().compareTo(a.getNgayTao()))
            .map(SanPhamResponse::new)
            .collect(Collectors.toList());}

    public PageableObject<SanPhamResponse> phanTrang(Integer pageNo, Integer pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<SanPham> pageSp = spRepo.findAll(pageable);
        Page<SanPhamResponse>spRes = pageSp.map(SanPhamResponse::new);
        return new PageableObject<>(spRes);
    }

    public List<SanPhamResponse> filterSanPham(
            String searchText,
            String maSanPham,
            String tenSanPham,
            String tenNhaSanXuat,
            String tenChatLieu,
            String tenKieuDang,
            String tenXuatXu,
            Date ngayTao,
            Boolean trangThai
    ){
        List<SanPham> list = spRepo.findAll();
        List<ChiTietSanPham> list1 = ctspRepo.findAll();

        return list.stream()
                .filter(sp -> searchText == null || searchText.isEmpty() ||
                        sp.getMaSanPham().toLowerCase().contains(searchText.toLowerCase()) ||
                        sp.getTenSanPham().toLowerCase().contains(searchText.toLowerCase()) ||
                        (sp.getNhaSanXuat() != null &&
                                sp.getNhaSanXuat().getTenNhaSanXuat().toLowerCase().contains(searchText.toLowerCase())) ||
                        (sp.getChatLieu() != null &&
                                sp.getChatLieu().getTenChatLieu().toLowerCase().contains(searchText.toLowerCase())) ||
                        (sp.getKieuDang() != null &&
                                sp.getKieuDang().getTenKieuDang().toLowerCase().contains(searchText.toLowerCase())) ||
                        (sp.getXuatXu() != null &&
                                sp.getXuatXu().getTenXuatXu().toLowerCase().contains(searchText.toLowerCase())))
                .filter(sp -> maSanPham == null || maSanPham.isEmpty()
                        || sp.getMaSanPham().toLowerCase().contains(maSanPham.toLowerCase()))
                .filter(sp -> tenSanPham == null || tenSanPham.isEmpty()
                        || sp.getTenSanPham().toLowerCase().contains(tenSanPham.toLowerCase()))
                .filter(sp -> tenNhaSanXuat == null || tenNhaSanXuat.isEmpty()
                        || (sp.getNhaSanXuat() != null &&
                        tenNhaSanXuat.equals(sp.getNhaSanXuat().getTenNhaSanXuat())))
                .filter(sp -> tenChatLieu == null || tenChatLieu.isEmpty()
                        || (sp.getChatLieu() != null &&
                        tenChatLieu.equals(sp.getChatLieu().getTenChatLieu())))
                .filter(sp -> tenKieuDang == null || tenKieuDang.isEmpty()
                        || (sp.getKieuDang() != null &&
                        tenKieuDang.equals(sp.getKieuDang().getTenKieuDang())))
                .filter(sp -> tenXuatXu == null || tenXuatXu.isEmpty()
                        || (sp.getXuatXu() != null &&
                        tenXuatXu.equals(sp.getXuatXu().getTenXuatXu())))
                .filter(sp -> ngayTao == null ||
                        (sp.getNgayTao() != null &&
                                sp.getNgayTao().compareTo(ngayTao) >= 0))
                .filter(sp -> trangThai == null || sp.getTrangThai().equals(trangThai))
                .map(SanPhamResponse::new)
                .collect(Collectors.toList());
    }

    public void add(SanPhamRequest request){
        SanPham sp = MapperUtils.map(request,SanPham.class);
        NhaSanXuat nsx = nsxRepo.findById(request.getIdNhaSanXuat()).orElse(null);
        sp.setNhaSanXuat(nsx);
        XuatXu xx = xxRepo.findById(request.getIdXuatXu()).orElse(null);
        sp.setXuatXu(xx);
        ChatLieu cl = clRepo.findById(request.getIdChatLieu()).orElse(null);
        sp.setChatLieu(cl);
        KieuDang kd = kdRepo.findById(request.getIdKieuDang()).orElse(null);
        sp.setKieuDang(kd);
        sp.setNgayTao(new Date());
        sp.setTrangThai(true);
        spRepo.save(sp);
    }

    public void delete(Integer id){spRepo.deleteById(id);}

    public SanPhamResponse getSanPhamDetailWithVariants(Integer idSanPham) {
        System.out.println("🔍 SanPhamService.getSanPhamDetailWithVariants() - ID: " + idSanPham);

        try {
            SanPham sanPham = spRepo.findById(idSanPham)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + idSanPham));

            SanPhamResponse response = new SanPhamResponse(sanPham);

            System.out.println("✅ Đã lấy chi tiết sản phẩm với " +
                    (response.getChiTietSanPhams() != null ? response.getChiTietSanPhams().size() : 0) + " biến thể");

            return response;

        } catch (Exception e) {
            System.err.println("❌ Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage());
            throw new RuntimeException("Lỗi khi lấy chi tiết sản phẩm: " + e.getMessage());
        }
    }

    public void updateTrangThai(Integer id, Boolean trangThai) {
        SanPham sp = spRepo.findById(id).get();
        sp.setTrangThai(trangThai);
        spRepo.save(sp);
    }
}
