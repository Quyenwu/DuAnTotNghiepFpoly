package com.example.the_autumn.entity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.the_autumn.entity.PhongChat;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "tin_nhan")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TinNhan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_phong")
    @JsonIgnore
    private PhongChat phongChat;

    @Column(name = "gui_tu")
    private Integer guiTu; // 0: khách, 1: nhân viên, 2: AI

    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "thoi_gian")
    private LocalDateTime thoiGian = LocalDateTime.now();
}
