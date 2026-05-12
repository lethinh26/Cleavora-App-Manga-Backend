package com.clevora.clevora.dto.user;

import jakarta.persistence.PrePersist;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserProfileRequest {
    @NotBlank(message = "Ảnh không được để trống")
    private String avatarUrl;
    @NotBlank(message = "Tên hiển thị không được để trống")
    private String displayName;
}
