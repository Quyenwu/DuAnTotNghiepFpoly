package com.example.the_autumn.controller;

import com.example.the_autumn.entity.HoaDon;
import com.example.the_autumn.model.response.ResponseObject;
import com.example.the_autumn.service.BanHangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ban-hang")
@CrossOrigin(origins = {"http://localhost:5173","http://localhost:5174/" , "http://localhost:3000"})
public class BanHangController {

    @Autowired
    private BanHangService banHangService;

    @PostMapping("/tao-hoa-don")
    public ResponseObject<HoaDon> taoHoaDonMoi(@RequestParam(required = false) Integer idNhanVien) {
        try {
            HoaDon hoaDon = banHangService.taoHoaDonMoi(idNhanVien);
            return new ResponseObject<>(hoaDon, "Tạo hóa đơn thành công");
        } catch (Exception e) {
            return ResponseObject.error("Tạo hóa đơn thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/them-san-pham")
    public ResponseObject<HoaDon> themSanPhamVaoHoaDon(
            @RequestParam Integer idHoaDon,
            @RequestParam Integer idChiTietSanPham,
            @RequestParam(defaultValue = "1") Integer soLuong) {
        try {
            HoaDon hoaDon = banHangService.themSanPhamVaoHoaDon(idHoaDon, idChiTietSanPham, soLuong);
            return new ResponseObject<>(hoaDon, "Thêm sản phẩm thành công");
        } catch (Exception e) {
            return ResponseObject.error("Thêm sản phẩm thất bại: " + e.getMessage());
        }
    }

    @DeleteMapping("/xoa-san-pham/{idHoaDonChiTiet}")
    public ResponseObject<String> xoaSanPhamKhoiHoaDon(@PathVariable Integer idHoaDonChiTiet) {
        try {
            banHangService.xoaSanPhamKhoiHoaDon(idHoaDonChiTiet);
            return new ResponseObject<>("Đã xóa sản phẩm khỏi hóa đơn", "Xóa thành công");
        } catch (Exception e) {
            return ResponseObject.error("Xóa sản phẩm thất bại: " + e.getMessage());
        }
    }

    @PostMapping("/thanh-toan")
    public ResponseObject<HoaDon> thanhToanHoaDon(
            @RequestParam Integer idHoaDon,
            @RequestParam(required = false) String tenKhachHang,
            @RequestParam(required = false) String sdt) {
        try {
            HoaDon hoaDon = banHangService.thanhToanHoaDon(idHoaDon, tenKhachHang, sdt);
            return new ResponseObject<>(hoaDon, "Thanh toán thành công");
        } catch (Exception e) {
            return ResponseObject.error("Thanh toán thất bại: " + e.getMessage());
        }
    }

    @GetMapping("/hoa-don/{id}")
    public ResponseObject<HoaDon> getHoaDonById(@PathVariable Integer id) {
        try {
            HoaDon hoaDon = banHangService.getHoaDonById(id);
            return new ResponseObject<>(hoaDon, "Lấy hóa đơn thành công");
        } catch (Exception e) {
            return ResponseObject.error("Không tìm thấy hóa đơn: " + e.getMessage());
        }
    }
}
