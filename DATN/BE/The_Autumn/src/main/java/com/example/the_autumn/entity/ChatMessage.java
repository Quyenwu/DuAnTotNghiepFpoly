package com.example.the_autumn.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "phong_id", nullable = false)
    private String phongId;  // room ID

    @Column(name = "nguoi_gui", nullable = false)
    private String nguoiGui; // khách, AI, hoặc nhân viên

    @Column(name = "role", nullable = false)
    private String role;     // "customer", "admin", "ai"

    @Column(name = "noi_dung", nullable = false)
    private String noiDung;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
