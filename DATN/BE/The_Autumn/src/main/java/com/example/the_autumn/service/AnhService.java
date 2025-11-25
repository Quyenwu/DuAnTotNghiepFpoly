package com.example.the_autumn.service;

import com.example.the_autumn.entity.Anh;
import com.example.the_autumn.entity.ChiTietSanPham;
import com.example.the_autumn.repository.AnhRepository;
import com.example.the_autumn.repository.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AnhService {

    @Autowired
    private AnhRepository anhRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @Transactional
    public Anh themAnhDonChoBienThe(Integer idChiTietSanPham, String imageUrl) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idChiTietSanPham));

        List<Anh> oldImages = anhRepository.findByChiTietSanPham_Id(idChiTietSanPham);
        if (!oldImages.isEmpty()) {
            anhRepository.deleteAll(oldImages);
        }

        Anh anh = new Anh();
        anh.setChiTietSanPham(chiTietSanPham);
        anh.setDuongDanAnh(imageUrl);
        anh.setTrangThai(true);

        return anhRepository.save(anh);
    }

    @Transactional
    public List<Anh> themAnhChoBienThe(Integer idChiTietSanPham, List<String> imageUrls) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idChiTietSanPham));

        List<Anh> danhSachAnh = new ArrayList<>();

        for (String url : imageUrls) {
            Anh anh = new Anh();
            anh.setChiTietSanPham(chiTietSanPham);
            anh.setDuongDanAnh(url);
            anh.setMaAnh("ANH_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
            anh.setTrangThai(true);

            danhSachAnh.add(anh);
        }

        return anhRepository.saveAll(danhSachAnh);
    }

    @Transactional
    public Anh capNhatAnh(Integer idAnh, String imageUrl) {
        Anh anh = anhRepository.findById(idAnh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID: " + idAnh));

        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            throw new RuntimeException("URL ảnh không được để trống");
        }

        anh.setDuongDanAnh(imageUrl);
        return anhRepository.save(anh);
    }

    @Transactional
    public void xoaAnh(Integer idAnh) {
        Anh anh = anhRepository.findById(idAnh)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy ảnh với ID: " + idAnh));

        anhRepository.delete(anh);
    }

    @Transactional
    public List<Anh> capNhatNhieuAnh(Integer idChiTietSanPham, List<String> imageUrls) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(idChiTietSanPham)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể với ID: " + idChiTietSanPham));

        List<Anh> oldImages = anhRepository.findByChiTietSanPham_Id(idChiTietSanPham);
        anhRepository.deleteAll(oldImages);

        return themAnhChoBienThe(idChiTietSanPham, imageUrls);
    }

    public List<Anh> getAnhByBienThe(Integer idChiTietSanPham) {
        return anhRepository.findByChiTietSanPham_Id(idChiTietSanPham);
    }
}