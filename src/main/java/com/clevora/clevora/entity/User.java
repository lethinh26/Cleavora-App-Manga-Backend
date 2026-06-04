package com.clevora.clevora.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Entity mẫu - Bảng users.
 *
 * Mapping trực tiếp với bảng `users` trong database.
 * Sử dụng Lombok để giảm boilerplate code.
 *
 * Cách tạo entity mới:
 *   1. Tạo class trong package entity/
 *   2. Đánh dấu @Entity, @Table(name = "tên_bảng")
 *   3. Định nghĩa các field tương ứng với cột trong DB
 *   4. Dùng @Id + @GeneratedValue cho primary key
 *   5. Dùng @Enumerated cho cột ENUM
 *   6. Dùng @Column để tùy chỉnh mapping
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Column(name = "is_active", nullable = false)
    private Boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum Role {
        USER, ADMIN, SUPERADMIN
    }

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<ReadingHistory> readingHistories;
}
