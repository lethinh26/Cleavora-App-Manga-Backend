package com.clevora.clevora.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChangeRoleRequest {
    @NotBlank(message = "Role không được để trống")
    private String role; // "USER", "ADMIN", "SUPERADMIN"
}
