package com.example.the_autumn.controller;

import com.example.the_autumn.entity.KhachHang;
import com.example.the_autumn.entity.NhanVien;
import com.example.the_autumn.entity.PhongChat;
import com.example.the_autumn.entity.TinNhan;
import com.example.the_autumn.model.request.ChatRequest;
import com.example.the_autumn.repository.KhachHangRepository;
import com.example.the_autumn.repository.NhanVienRepository;
import com.example.the_autumn.repository.PhongChatRepository;
import com.example.the_autumn.repository.TinNhanRepository;
import com.example.the_autumn.service.AIService;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
public class ChatBotController {

    private final AIService aiService;
    private final TinNhanRepository tinNhanRepo;
    private final PhongChatRepository phongRepo;
    private final KhachHangRepository khachHangRepo;
    private final NhanVienRepository nhanVienRepo;
    private final SimpMessagingTemplate simp;

    public ChatBotController(AIService aiService,
                             TinNhanRepository tinNhanRepo,
                             PhongChatRepository phongRepo,
                             KhachHangRepository khachHangRepo,
                             NhanVienRepository nhanVienRepo,
                             SimpMessagingTemplate simp) {
        this.aiService = aiService;
        this.tinNhanRepo = tinNhanRepo;
        this.phongRepo = phongRepo;
        this.khachHangRepo = khachHangRepo;
        this.nhanVienRepo = nhanVienRepo;
        this.simp = simp;
    }

    // Tạo phòng chat mới
    @PostMapping("/rooms")
    public ResponseEntity<?> createRoom(@RequestParam(required = false) Integer idKhachHang) {
        KhachHang kh = null;
        if (idKhachHang != null) {
            kh = khachHangRepo.findById(idKhachHang)
                    .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));
        }

        PhongChat room = PhongChat.builder()
                .khachHang(kh)
                .loai(0) // khách-AI
                .trangThai(1)
                .ngayTao(java.time.LocalDateTime.now())
                .build();

        PhongChat saved = phongRepo.save(room);
        return ResponseEntity.ok(Map.of("roomId", saved.getId()));
    }

    // Lấy lịch sử chat
    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<TinNhan>> history(@PathVariable Integer roomId) {
        return ResponseEntity.ok(tinNhanRepo.findByPhongChatIdOrderByThoiGianAsc(roomId));
    }

    // Gửi tin nhắn tới AI (REST)
    @PostMapping("/ask")
    public ResponseEntity<?> askAI(@RequestBody ChatRequest req) {
        if (req.getSenderType() != null && req.getSenderType() == 0) { // chỉ KH mới gọi AI
            PhongChat room = phongRepo.findById(req.getRoomId()).orElseThrow();
            String answer = aiService.ask(req.getMessage(), req.getRoomId());
            TinNhan aiMsg = TinNhan.builder().phongChat(room).guiTu(2).noiDung(answer).build();
            tinNhanRepo.save(aiMsg);

            simp.convertAndSend("/topic/chat/" + req.getRoomId(), Map.of(
                    "guiTu", 2, "noiDung", answer, "thoiGian", aiMsg.getThoiGian()
            ));

            return ResponseEntity.ok(Map.of("reply", answer));
        } else {
            return ResponseEntity.ok(Map.of("reply", "Đã gửi tin nhắn nhân viên thành công"));
        }
    }
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody ChatRequest req) {
        PhongChat room = phongRepo.findById(req.getRoomId())
                .orElseThrow(() -> new RuntimeException("Room không tồn tại"));

        TinNhan msg = TinNhan.builder()
                .phongChat(room)
                .guiTu(1) // nhân viên
                .noiDung(req.getMessage())
                .thoiGian(LocalDateTime.now())
                .build();
        tinNhanRepo.save(msg);

        simp.convertAndSend("/topic/chat/" + room.getId(), Map.of(
                "guiTu", 1,
                "noiDung", req.getMessage(),
                "thoiGian", msg.getThoiGian()
        ));

        return ResponseEntity.ok(Map.of("message", "Đã gửi tin nhắn nhân viên thành công"));
    }
    @GetMapping("/rooms")
    public ResponseEntity<?> getAllRooms() {
        List<PhongChat> rooms = phongRepo.findAll();
        List<Map<String, Object>> roomList = rooms.stream().map(r -> {
            Map<String, Object> map = new HashMap<>();
            map.put("roomId", r.getId());
            map.put("khachHang", r.getKhachHang() != null ? r.getKhachHang().getHoTen() : "Guest");
            map.put("loai", r.getLoai());
            return map;
        }).toList();
        return ResponseEntity.ok(Map.of("rooms", roomList));
    }

    @PostMapping("/rooms/auto")
    public ResponseEntity<?> createRoomForRegistered(@RequestParam Integer idKhachHang) {
        // Bắt buộc phải có idKhachHang
        KhachHang kh = khachHangRepo.findById(idKhachHang)
                .orElseThrow(() -> new RuntimeException("Khách hàng không tồn tại"));

        // Kiểm tra xem khách hàng đã có phòng chưa
        PhongChat room = phongRepo.findAll().stream()
                .filter(p -> p.getKhachHang() != null && p.getKhachHang().getId().equals(kh.getId()))
                .findFirst()
                .orElse(null);

        if (room == null) {
            // Nếu chưa có → tạo phòng mới
            room = PhongChat.builder()
                    .khachHang(kh)
                    .loai(0) // khách-AI
                    .trangThai(1)
                    .ngayTao(LocalDateTime.now())
                    .build();
            room = phongRepo.save(room);
        }

        return ResponseEntity.ok(Map.of(
                "roomId", room.getId(),
                "khachHang", kh.getHoTen()
        ));
    }
    @PostMapping("/rooms/join")
    public ResponseEntity<?> joinRoomAsStaff(@RequestParam Integer roomId, @RequestParam Integer idNhanVien) {
        PhongChat room = phongRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng chat không tồn tại"));

        NhanVien nv = nhanVienRepo.findById(idNhanVien)
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));

        // Cập nhật phòng: gán nhân viên, chuyển loại sang khách-nhân viên
        room.setNhanVien(nv);
        room.setLoai(1); // khách-nhân viên
        phongRepo.save(room);

        return ResponseEntity.ok(Map.of(
                "roomId", room.getId(),
                "khachHang", room.getKhachHang().getHoTen(),
                "nhanVien", nv.getHoTen(),
                "loai", room.getLoai()
        ));
    }
    @PostMapping("/rooms/{roomId}/changeType")
    public ResponseEntity<?> changeRoomType(@PathVariable Integer roomId, @RequestParam Integer type) {
        // type = 0 (AI), 1 (Nhân viên)
        PhongChat room = phongRepo.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng chat không tồn tại"));

        room.setLoai(type);
        phongRepo.save(room);

        // Nếu chuyển sang nhân viên → gửi thông báo cho nhân viên
        if (type == 1) {
            simp.convertAndSend("/topic/staff/notifications", Map.of(
                    "roomId", room.getId(),
                    "khachHang", room.getKhachHang().getHoTen(),
                    "message", "Khách cần hỗ trợ"
            ));
        }

        return ResponseEntity.ok(Map.of(
                "roomId", room.getId(),
                "loai", room.getLoai()
        ));
    }


}
