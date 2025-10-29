package com.example.the_autumn.service;

import com.example.the_autumn.entity.DiaChi;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.model.request.AddKhachHangRequest;
import com.example.the_autumn.model.request.DiaChiRequest;
import com.example.the_autumn.model.request.UpdateKhachHangRequest;
import com.example.the_autumn.model.response.DiaChiResponse;
import com.example.the_autumn.model.response.KhachHangResponse;
import com.example.the_autumn.repository.DiaChiRepository;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.LichSuHoaDonRepository;
import com.example.the_autumn.repository.QuanHuyenRepository;
import com.example.the_autumn.repository.TinhThanhRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class KhachHangService {

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private LichSuHoaDonRepository lichSuHoaDonRepository;

    @Autowired
    private DiaChiRepository diaChiRepository;

    @Autowired
    private TinhThanhRepository tinhThanhRepository;

    @Autowired
    private QuanHuyenRepository quanHuyenRepository;

    @Autowired
    private EmailService mailService;

    public List<KhachHangResponse> getAllKhachHang() {
        List<KhachHang> khachHangs = khachHangRepository.findAll();

        Map<Integer, Object[]> statsMap = lichSuHoaDonRepository.getSoLanVaNgayMuaGanNhatCuaKhachHang()
                .stream()
                .collect(Collectors.toMap(r -> (Integer) r[0], r -> r));

        List<KhachHangResponse> result = new ArrayList<>();

        for (KhachHang k : khachHangs) {
            Object[] stats = statsMap.get(k.getId());
            Long soLanMuaLong = stats != null ? (Long) stats[1] : 0L;
            Integer soLanMua = soLanMuaLong != null ? soLanMuaLong.intValue() : 0;
            Date ngayMuaGanNhat = stats != null ? (Date) stats[2] : null;

            result.add(new KhachHangResponse(k, soLanMua, ngayMuaGanNhat));
        }
        return result;
    }

    public List<KhachHangResponse> getData() {
        return khachHangRepository.findAll()
                .stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public KhachHangResponse createKhachHang(AddKhachHangRequest request) {
        if (khachHangRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email " + request.getEmail() + " đã được sử dụng!");
        }

        KhachHang khachHang = new KhachHang();
        khachHang.setHoTen(request.getHoTen());
        khachHang.setEmail(request.getEmail());
        khachHang.setSdt(request.getSdt());
        String randomPassword = UUID.randomUUID().toString().substring(0, 8);
        khachHang.setMatKhau(randomPassword);
        khachHang.setGioiTinh(request.getGioiTinh());
        khachHang.setTrangThai(true);
        khachHang.setNgaySinh(request.getNgaySinh());
        khachHang.setNgayTao(new Date());

        KhachHang savedKhachHang = khachHangRepository.save(khachHang);

        List<DiaChiRequest> diaChiRequests = request.getDiaChi();
        if (diaChiRequests != null && !diaChiRequests.isEmpty()) {
            boolean hasDefault = diaChiRequests.stream().anyMatch(DiaChiRequest::getTrangThai);
            for (int i = 0; i < diaChiRequests.size(); i++) {
                DiaChiRequest d = diaChiRequests.get(i);
                DiaChi dc = new DiaChi();
                dc.setKhachHang(savedKhachHang);
                dc.setTenDiaChi(d.getTenDiaChi());
                dc.setDiaChiCuThe(d.getDiaChiCuThe());
                dc.setTinhThanh(tinhThanhRepository.findById(d.getTinhThanhId())
                        .orElseThrow(() -> new RuntimeException("Tỉnh không tồn tại")));
                dc.setQuanHuyen(quanHuyenRepository.findById(d.getQuanHuyenId())
                        .orElseThrow(() -> new RuntimeException("Quận/Huyện không tồn tại")));
                dc.setTrangThai(hasDefault ? Boolean.TRUE.equals(d.getTrangThai()) : i == 0);
                diaChiRepository.save(dc);
            }
        }

        String subject = "Chào mừng đến với The Autumn - Đăng ký tài khoản thành công";

        String template = """
                    <div style="font-family:Arial, sans-serif; max-width:600px; margin:0 auto;">
                        <div style="background:linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                    padding:30px; text-align:center; color:white;">
                            <h1 style="margin:0; font-size:28px;">Chào mừng đến với The Autumn</h1>
                            <p style="margin:10px 0 0; font-size:16px; opacity:0.9;">
                                Đăng ký tài khoản thành công
                            </p>
                        </div>
                        
                        <div style="padding:30px; background:#f8f9fa;">
                            <h3>Xin chào {{NAME}},</h3>
                            <p>Cảm ơn bạn đã đăng ký tài khoản tại <b>The Autumn</b>.
                               Tài khoản của bạn đã được tạo thành công.</p>
                            
                            <div style="background:white; padding:20px; border-radius:10px;
                                        border-left:4px solid #667eea; margin:20px 0;">
                                <p style="margin:0 0 10px; color:#667eea; font-weight:bold;">
                                    Thông tin tài khoản của bạn:
                                </p>
                                <ul style="margin:0; padding-left:20px;">
                                    <li><b>Họ tên:</b> {{NAME}}</li>
                                    <li><b>Email:</b> {{EMAIL}}</li>
                                    <li><b>Số điện thoại:</b> {{PHONE}}</li>
                                    <li><b>Mật khẩu:</b> {{PASSWORD}}</li>
                                </ul>
                            </div>
                            
                            <div style="background:#fff3cd; padding:15px; border-radius:5px;
                                        border:1px solid #ffeaa7; margin:20px 0;">
                                <p style="margin:0; color:#856404;">
                                    <b>Lưu ý quan trọng:</b> Vì lý do bảo mật, vui lòng đổi mật khẩu sau khi đăng nhập lần đầu.
                                </p>
                            </div>
                            
                            <p>Bạn có thể bắt đầu mua sắm ngay bây giờ bằng cách đăng nhập vào tài khoản của mình.</p>
                            
                            <div style="text-align:center; margin:30px 0;">
                                <a href="http://localhost:3000"
                                   style="background:linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                                          color:white; padding:12px 30px; text-decoration:none;
                                          border-radius:5px; display:inline-block; font-weight:bold;">
                                    Đăng nhập ngay
                                </a>
                            </div>
                            
                            <p style="color:#666; font-size:14px;">
                                Nếu bạn có bất kỳ câu hỏi nào, vui lòng liên hệ với chúng tôi qua email hoặc số điện thoại hỗ trợ.
                            </p>
                            
                            <p style="color:#E67E22; margin-top:30px;">
                                Trân trọng,<br/>
                                <b>Đội ngũ The Autumn</b>
                            </p>
                        </div>
                        
                        <div style="background:#343a40; padding:20px; text-align:center;
                                    color:white; font-size:12px;">
                            <p style="margin:0;">© 2024 The Autumn. All rights reserved.</p>
                        </div>
                    </div>
                """;

        String body = template
                .replace("{{NAME}}", savedKhachHang.getHoTen() != null ? savedKhachHang.getHoTen() : "")
                .replace("{{EMAIL}}", savedKhachHang.getEmail() != null ? savedKhachHang.getEmail() : "")
                .replace("{{PHONE}}", savedKhachHang.getSdt() != null ? savedKhachHang.getSdt() : "")
                .replace("{{PASSWORD}}", randomPassword);

        // Gửi email bất đồng bộ
        mailService.sendMailKhachHang(savedKhachHang.getEmail(), subject, body);

        KhachHang savedKhachHangFull = khachHangRepository.findById(savedKhachHang.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khách hàng sau khi lưu"));

        return new KhachHangResponse(savedKhachHangFull);
    }

    public KhachHangResponse updateKhachHang(Integer id, UpdateKhachHangRequest request) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        kh.setHoTen(request.getHoTen());
        kh.setEmail(request.getEmail());
        kh.setSdt(request.getSdt());
        kh.setGioiTinh(request.getGioiTinh());
        if (request.getMatKhau() != null && !request.getMatKhau().isEmpty()) {
            kh.setMatKhau(request.getMatKhau());
        }
        kh.setTrangThai(request.getTrangThai());
        kh.setNgaySinh(request.getNgaySinh());
        kh.setNgaySua(new Date());

        List<DiaChiRequest> diaChiRequests = request.getDiaChi();

        if (diaChiRequests != null && !diaChiRequests.isEmpty()) {
            boolean hasDefault = diaChiRequests.stream().anyMatch(DiaChiRequest::getTrangThai);

            List<Integer> requestIds = diaChiRequests.stream()
                    .map(DiaChiRequest::getId)
                    .filter(Objects::nonNull)
                    .toList();


            List<DiaChi> existing = diaChiRepository.findByKhachHangId(kh.getId());
            for (DiaChi old : existing) {
                if (!requestIds.contains(old.getId())) {
                    diaChiRepository.delete(old);
                }
            }

            for (int i = 0; i < diaChiRequests.size(); i++) {
                DiaChiRequest d = diaChiRequests.get(i);
                DiaChi dc;
                if (d.getId() != null) {
                    dc = diaChiRepository.findById(d.getId())
                            .orElseThrow(() -> new RuntimeException("Địa chỉ không tồn tại"));
                } else {
                    dc = new DiaChi();
                    dc.setKhachHang(kh);
                }

                dc.setTenDiaChi(d.getTenDiaChi());
                dc.setDiaChiCuThe(d.getDiaChiCuThe());


                if (d.getTinhThanhId() != null) {
                    dc.setTinhThanh(tinhThanhRepository.findById(d.getTinhThanhId())
                            .orElseThrow(() -> new RuntimeException("Tỉnh không tồn tại")));
                } else {
                    dc.setTinhThanh(null);
                }

                if (d.getQuanHuyenId() != null) {
                    dc.setQuanHuyen(quanHuyenRepository.findById(d.getQuanHuyenId())
                            .orElseThrow(() -> new RuntimeException("Quận/Huyện không tồn tại")));
                } else {
                    dc.setQuanHuyen(null);
                }

                dc.setTrangThai(hasDefault ? Boolean.TRUE.equals(d.getTrangThai()) : i == 0);
                diaChiRepository.save(dc);
            }
        } else {

            List<DiaChi> existing = diaChiRepository.findByKhachHangId(kh.getId());
            if (!existing.isEmpty()) {
                diaChiRepository.deleteAll(existing);
                diaChiRepository.flush();
            }
        }


        KhachHang savedKh = khachHangRepository.save(kh);
        return new KhachHangResponse(savedKh);
    }

    public void deleteKhachHang(Integer id) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Xóa thành công"));
        khachHangRepository.delete(kh);
    }

    public KhachHangResponse detailKhachHang(Integer id) {
        KhachHang kh = khachHangRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        KhachHangResponse response = new KhachHangResponse(kh);

        if (kh.getDiaChi() != null) {
            response.setDiaChi(
                    kh.getDiaChi().stream()
                            .map(DiaChiResponse::new)
                            .collect(Collectors.toList())
            );
        }

        response.setSoLanMua(kh.getHoaDons() != null ? kh.getHoaDons().size() : 0);
        response.setNgayMuaGanNhat(
                kh.getHoaDons() != null && !kh.getHoaDons().isEmpty()
                        ? kh.getHoaDons().get(kh.getHoaDons().size() - 1).getNgayTao()
                        : null
        );
        return response;
    }

    public List<KhachHangResponse> searchKhachHang(String keyword) {
        List<KhachHang> list = khachHangRepository.searchByKeyword(keyword);
        return list.stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public List<KhachHangResponse> filterKhachHang(Boolean gioiTinh, Boolean trangThai) {
        List<KhachHang> list = khachHangRepository.filterByGenderAndStatus(gioiTinh, trangThai);
        return list.stream()
                .map(KhachHangResponse::new)
                .collect(Collectors.toList());
    }

    public boolean checkExistsByEmailAndSdt(String email, String sdt) {
        if (email != null && sdt != null) {
            return khachHangRepository.existsByEmailAndSdt(email, sdt);
        } else if (email != null) {
            return khachHangRepository.existsByEmail(email);
        } else if (sdt != null) {
            return khachHangRepository.existsBySdt(sdt);
        }
        return false;
    }

}
