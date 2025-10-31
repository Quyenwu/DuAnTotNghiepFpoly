package com.example.the_autumn.service;

import com.example.the_autumn.entity.*;
import com.example.the_autumn.model.request.SanPhamRequest;
import com.example.the_autumn.model.request.UpdateSanPhamRequest;
import com.example.the_autumn.model.response.PageableObject;
import com.example.the_autumn.model.response.SanPhamResponse;
import com.example.the_autumn.repository.*;
import com.example.the_autumn.util.MapperUtils;
import jakarta.transaction.Transactional;
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

    @Transactional
    public SanPhamResponse updateSanPham(Integer id, UpdateSanPhamRequest request) {
        System.out.println("🔄 Service: Update sản phẩm ID=" + id);

        SanPham sanPham = spRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + id));

        NhaSanXuat nhaSanXuat = nsxRepo.findById(request.getIdNhaSanXuat())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhà sản xuất với ID: " + request.getIdNhaSanXuat()));

        XuatXu xuatXu = xxRepo.findById(request.getIdXuatXu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy xuất xứ với ID: " + request.getIdXuatXu()));

        ChatLieu chatLieu = clRepo.findById(request.getIdChatLieu())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chất liệu với ID: " + request.getIdChatLieu()));

        KieuDang kieuDang = kdRepo.findById(request.getIdKieuDang())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy kiểu dáng với ID: " + request.getIdKieuDang()));

        CoAo coAo = spRepo.findById(request.getIdCoAo())
                .map(sp -> sp.getCoAo())
                .orElseGet(() -> {
                    throw new RuntimeException("Không tìm thấy cổ áo với ID: " + request.getIdCoAo());
                });

        TayAo tayAo = spRepo.findById(request.getIdTayAo())
                .map(sp -> sp.getTayAo())
                .orElseGet(() -> {
                    throw new RuntimeException("Không tìm thấy tay áo với ID: " + request.getIdTayAo());
                });

        sanPham.setTenSanPham(request.getTenSanPham());
        sanPham.setNhaSanXuat(nhaSanXuat);
        sanPham.setXuatXu(xuatXu);
        sanPham.setChatLieu(chatLieu);
        sanPham.setKieuDang(kieuDang);
        sanPham.setCoAo(coAo);
        sanPham.setTayAo(tayAo);
        sanPham.setTrongLuong(request.getTrongLuong());

        if (request.getTrangThai() != null) {
            sanPham.setTrangThai(request.getTrangThai());
        }

        sanPham.setNgaySua(new Date());

        SanPham saved = spRepo.save(sanPham);

        System.out.println("✅ Service: Đã cập nhật sản phẩm ID=" + id);

        return new SanPhamResponse(saved);
    }
}
