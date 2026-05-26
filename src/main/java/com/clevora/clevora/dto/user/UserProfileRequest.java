package com.clevora.clevora.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserProfileRequest {
    private String avatarUrl;
    @NotBlank(message = "Tên hiển thị không được để trống")
    private String displayName;
}
