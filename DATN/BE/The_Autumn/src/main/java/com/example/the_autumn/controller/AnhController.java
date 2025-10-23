package com.example.the_autumn.controller;

import com.example.the_autumn.entity.Anh;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.repository.AnhRepository;
import com.example.the_autumn.service.AnhService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anh")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class AnhController {

    @Autowired
    private AnhService anhService;

    @Autowired
    private AnhRepository anhRepository;

    @PostMapping("/{idChiTietSanPham}/single")
    public ResponseObject<?> themAnhDonChoVariant(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody Map<String, String> requestBody) {

        try {
            String imageUrl = requestBody.get("imageUrl");

            if (imageUrl == null || imageUrl.trim().isEmpty()) {
                return new ResponseObject<>("400", "URL ảnh không được để trống");
            }

            Anh savedImage = anhService.themAnhDonChoBienThe(idChiTietSanPham, imageUrl);

            return new ResponseObject<>(savedImage, "Đã thêm ảnh cho biến thể thành công");

        } catch (Exception e) {
            return new ResponseObject<>("500", "Lỗi khi thêm ảnh: " + e.getMessage());
        }
    }

    @PostMapping("/{idChiTietSanPham}")
    public ResponseObject<?> themAnh(
            @PathVariable Integer idChiTietSanPham,
            @RequestBody List<String> imageUrls) {

        try {
            List<Anh> savedImages = anhService.themAnhChoBienThe(idChiTietSanPham, imageUrls);

            return new ResponseObject<>(savedImages, "Đã thêm " + savedImages.size() + " ảnh thành công");

        } catch (Exception e) {
            return new ResponseObject<>("500", "Lỗi khi thêm ảnh: " + e.getMessage());
        }
    }

    @GetMapping("/bien-the/{idChiTietSanPham}")
    public ResponseObject<?> getAnhByBienThe(@PathVariable Integer idChiTietSanPham) {
        try {
            List<Anh> images = anhRepository.findByChiTietSanPham_Id(idChiTietSanPham);

            return new ResponseObject<>(images);

        } catch (Exception e) {
            return new ResponseObject<>("500", "Lỗi khi lấy ảnh: " + e.getMessage());
        }
    }

    @DeleteMapping("/{idAnh}")
    public ResponseObject<?> xoaAnh(@PathVariable Integer idAnh) {
        try {
            anhRepository.deleteById(idAnh);
            return new ResponseObject<>(null, "Đã xóa ảnh thành công");
        } catch (Exception e) {
            return new ResponseObject<>("500", "Lỗi khi xóa ảnh: " + e.getMessage());
        }
    }
}