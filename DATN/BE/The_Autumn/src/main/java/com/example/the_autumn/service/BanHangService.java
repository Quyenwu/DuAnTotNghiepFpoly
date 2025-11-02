package com.example.the_autumn.service;

import com.example.the_autumn.entity.*;
import com.example.the_autumn.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;


@Service
public class BanHangService {

    @Autowired private HoaDonRepository hoaDonRepository;
    @Autowired private HoaDonChiTietRepository hoaDonChiTietRepository;
    @Autowired private ChiTietSanPhamRepository chiTietSanPhamRepository;
    @Autowired private KhachHangRepository khachHangRepository;
    @Autowired private NhanVienRepository nhanVienRepository;

    public HoaDon taoHoaDonMoi(Integer idNhanVien) {
        HoaDon hoaDon = new HoaDon();
        hoaDon.setTrangThai(0);
        hoaDon.setNgayTao(new Date());

        if (idNhanVien != null) {
            NhanVien nhanVien = nhanVienRepository.findById(idNhanVien)
                    .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
            hoaDon.setNhanVien(nhanVien);
        }

        hoaDonRepository.save(hoaDon);
        return getHoaDonById(hoaDon.getId());
    }

    public HoaDon themSanPhamVaoHoaDon(Integer idHoaDon, Integer idCTSP, Integer soLuong) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        ChiTietSanPham ctsp = chiTietSanPhamRepository.findById(idCTSP)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));

        if (ctsp.getSoLuongTon() < soLuong)
            throw new RuntimeException("Không đủ tồn kho");

        ctsp.setSoLuongTon(ctsp.getSoLuongTon() - soLuong);
        chiTietSanPhamRepository.save(ctsp);

        HoaDonChiTiet hdct = new HoaDonChiTiet();
        hdct.setHoaDon(hoaDon);
        hdct.setChiTietSanPham(ctsp);
        hdct.setSoLuong(soLuong);
        hdct.setGiaBan(ctsp.getGiaBan());
        hdct.setThanhTien(ctsp.getGiaBan().multiply(BigDecimal.valueOf(soLuong)));
        hoaDonChiTietRepository.save(hdct);

        return getHoaDonById(hoaDon.getId());
    }

    public void xoaSanPhamKhoiHoaDon(Integer idHoaDonChiTiet) {
        HoaDonChiTiet hdct = hoaDonChiTietRepository.findById(idHoaDonChiTiet)
                .orElseThrow(() -> new RuntimeException("Chi tiết hóa đơn không tồn tại"));

        ChiTietSanPham ctsp = hdct.getChiTietSanPham();
        ctsp.setSoLuongTon(ctsp.getSoLuongTon() + hdct.getSoLuong());
        chiTietSanPhamRepository.save(ctsp);

        hoaDonChiTietRepository.delete(hdct);
    }

    public HoaDon thanhToanHoaDon(Integer idHoaDon, String tenKH, String sdt) {
        HoaDon hoaDon = hoaDonRepository.findById(idHoaDon)
                .orElseThrow(() -> new RuntimeException("Hóa đơn không tồn tại"));

        hoaDon.setTrangThai(3);

        if (tenKH != null && !tenKH.isEmpty()) {
            KhachHang kh = new KhachHang();
            kh.setHoTen(tenKH);
            kh.setSdt(sdt);
            khachHangRepository.save(kh);
            hoaDon.setKhachHang(kh);
        }

        hoaDonRepository.save(hoaDon);
        return getHoaDonById(hoaDon.getId());
    }

    public HoaDon getHoaDonById(Integer id) {
        HoaDon hoaDon = hoaDonRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        hoaDon.getHoaDonChiTiets().size();
        return hoaDon;
    }
}
