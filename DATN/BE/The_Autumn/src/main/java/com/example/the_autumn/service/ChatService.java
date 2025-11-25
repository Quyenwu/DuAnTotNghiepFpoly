package com.example.the_autumn.service;

import com.example.the_autumn.dto.ChatPayload;
import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhongChat;
import com.example.the_autumn.entity.TinNhan;
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

    public ChatService(PhongChatRepository phongRepo,
                       TinNhanRepository tinNhanRepo,
                       SimpMessagingTemplate simp,
                       AIService aiService) {
        this.phongRepo = phongRepo;
        this.tinNhanRepo = tinNhanRepo;
        this.simp = simp;
        this.aiService = aiService;
    }

    @Transactional
    public void handleIncoming(ChatPayload payload) {

        if (payload.getRoomId() == null) {
            throw new RuntimeException("RoomId không được null");
        }

        // Lấy phòng chat, nếu không tồn tại thì tạo mới
        PhongChat room = phongRepo.findById(payload.getRoomId())
                .orElseGet(() -> {
                    PhongChat p = PhongChat.builder()
                            .loai(0) // mặc định AI
                            .trangThai(1)
                            .ngayTao(LocalDateTime.now())
                            .build();
                    return phongRepo.save(p);
                });

        // Nếu có tin nhắn từ khách hoặc nhân viên
        if (payload.getNoiDung() != null) {
            TinNhan t = TinNhan.builder()
                    .phongChat(room)
                    .guiTu(payload.getGuiTu()) // 0=KH, 1=NV, 2=AI
                    .noiDung(payload.getNoiDung())
                    .thoiGian(LocalDateTime.now())
                    .build();
            tinNhanRepo.save(t);

            // Broadcast tin nhắn
            simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                    "guiTu", payload.getGuiTu(),
                    "noiDung", payload.getNoiDung(),
                    "thoiGian", t.getThoiGian()
            ));
        }

        // Nếu khách đổi loại phòng (AI hoặc NV)
        if (payload.getChangeType() != null) {
            room.setLoai(payload.getChangeType());
            phongRepo.save(room);

            if (payload.getChangeType() == 0) {
                // Chọn AI
                simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                        "guiTu", 2,
                        "noiDung", "AI đã sẵn sàng hỗ trợ bạn",
                        "thoiGian", LocalDateTime.now()
                ));
            } else if (payload.getChangeType() == 1) {
                // Chọn Nhân viên
                simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                        "guiTu", 2,
                        "noiDung", "AI tạm nghỉ, nhân viên sẽ hỗ trợ bạn",
                        "thoiGian", LocalDateTime.now()
                ));
                simp.convertAndSend("/topic/staff/notifications", Map.of(
                        "roomId", room.getId(),
                        "khachHang", room.getKhachHang().getHoTen(),
                        "message", "Khách cần hỗ trợ"
                ));
            }
        }

        // Nếu phòng đang AI và khách gửi tin nhắn → AI trả lời
        if (room.getLoai() == 0 && payload.getGuiTu() != null && payload.getGuiTu() == 0) {
            String answer = aiService.ask(payload.getNoiDung(), room.getId());
            TinNhan aiMsg = TinNhan.builder()
                    .phongChat(room)
                    .guiTu(2)
                    .noiDung(answer)
                    .thoiGian(LocalDateTime.now())
                    .build();
            tinNhanRepo.save(aiMsg);

            simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                    "guiTu", 2,
                    "noiDung", answer,
                    "thoiGian", aiMsg.getThoiGian()
            ));
        }

        // Nếu nhân viên join phòng
        if (payload.getNhanVienId() != null) {
            NhanVien nv = room.getNhanVien(); // hoặc lấy từ repo nếu cần
            if (nv == null) {
                // Gán nhân viên
                // nv = nhanVienRepo.findById(payload.getNhanVienId()).orElseThrow();
                // room.setNhanVien(nv);
                // phongRepo.save(room);
            }
            simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                    "guiTu", 2,
                    "noiDung", "Nhân viên đã vào hỗ trợ bạn",
                    "thoiGian", LocalDateTime.now()
            ));
        }
    }
}
