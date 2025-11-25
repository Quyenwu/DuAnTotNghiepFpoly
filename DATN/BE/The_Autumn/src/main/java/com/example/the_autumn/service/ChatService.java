package com.example.the_autumn.service;

import com.example.the_autumn.dto.ChatPayload;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhongChat;
import com.example.the_autumn.entity.TinNhan;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhongChatRepository;
import com.example.the_autumn.repository.TinNhanRepository;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Map;

@Service
public class ChatService {

    private final PhongChatRepository phongRepo;
    private final TinNhanRepository tinNhanRepo;
    private final SimpMessagingTemplate simp;
    private final AIService aiService;
    private final NhanVienRepository nhanVienRepo;

    public ChatService(PhongChatRepository phongRepo,
                       TinNhanRepository tinNhanRepo,
                       SimpMessagingTemplate simp,
                       NhanVienRepository nhanVienRepo,
                       AIService aiService) {
        this.phongRepo = phongRepo;
        this.tinNhanRepo = tinNhanRepo;
        this.simp = simp;
        this.aiService = aiService;
        this.nhanVienRepo= nhanVienRepo;
    }
    @Transactional
    public void handleIncoming(ChatPayload payload) {
        if (payload.getRoomId() == null) throw new RuntimeException("RoomId không được null");

        LocalDateTime now = LocalDateTime.now();

        // Lấy phòng chat, nếu không có thì tạo mới
        PhongChat room = phongRepo.findById(payload.getRoomId())
                .orElseGet(() -> {
                    PhongChat p = PhongChat.builder()
                            .loai(0) // AI
                            .trangThai(1)
                            .ngayTao(now)
                            .build();
                    return phongRepo.save(p);
                });

        // Nhân viên join phòng trước
        if (payload.getNhanVienId() != null) {
            NhanVien nv = room.getNhanVien();
            if (nv == null) {
                nv = nhanVienRepo.findById(payload.getNhanVienId())
                        .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
                room.setNhanVien(nv);
                room.setLoai(1); // chuyển sang khách-nhân viên
                phongRepo.save(room);

                simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                        "guiTu", 2,
                        "noiDung", "Nhân viên " + nv.getHoTen() + " đã vào hỗ trợ bạn",
                        "thoiGian", now
                ));
            }
        }

        // Lưu tin nhắn KH hoặc NV
        if (payload.getNoiDung() != null && payload.getGuiTu() != null) {
            TinNhan t = TinNhan.builder()
                    .phongChat(room)
                    .guiTu(payload.getGuiTu())
                    .noiDung(payload.getNoiDung())
                    .thoiGian(now)
                    .build();
            tinNhanRepo.save(t);

            simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                    "guiTu", payload.getGuiTu(),
                    "noiDung", payload.getNoiDung(),
                    "thoiGian", now
            ));
        }

        // Chỉ xử lý AI khi phòng vẫn là AI và KH gửi
        if (payload.getGuiTu() != null && payload.getGuiTu() == 0 && room.getLoai() == 0) {
            String answer = aiService.ask(payload.getNoiDung(), room.getId());

            TinNhan aiMsg = TinNhan.builder()
                    .phongChat(room)
                    .guiTu(2) // AI
                    .noiDung(answer)
                    .thoiGian(now)
                    .build();
            tinNhanRepo.save(aiMsg);

            simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                    "guiTu", 2,
                    "noiDung", answer,
                    "thoiGian", now
            ));

            // Nếu AI không trả lời → thông báo NV cần hỗ trợ
            if (answer.contains("Xin lỗi, tôi không hiểu") || answer.contains("không tìm thấy")) {
                room.setLoai(1); // chuyển sang NV
                phongRepo.save(room);

                simp.convertAndSend("/topic/staff/notifications", Map.of(
                        "roomId", room.getId(),
                        "khachHang", room.getKhachHang() != null ? room.getKhachHang().getHoTen() : "Guest",
                        "message", "Khách cần hỗ trợ"
                ));

                simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                        "guiTu", 2,
                        "noiDung", "AI không thể trả lời, nhân viên sẽ hỗ trợ bạn ngay.",
                        "thoiGian", LocalDateTime.now()
                ));
            }
        }

        // Nhân viên rời phòng → trả lại AI
        if (payload.getChangeType() != null && payload.getChangeType() == 2 && payload.getNhanVienId() != null) {
            NhanVien nv = room.getNhanVien();
            if (nv != null && nv.getId().equals(payload.getNhanVienId())) {
                room.setLoai(0); // AI
                room.setNhanVien(null);
                phongRepo.save(room);

                simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                        "guiTu", 2,
                        "noiDung", "Nhân viên đã rời phòng, AI sẽ tiếp tục hỗ trợ bạn.",
                        "thoiGian", LocalDateTime.now()
                ));
            }
        }
    }

}
