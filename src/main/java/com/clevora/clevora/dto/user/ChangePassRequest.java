package com.clevora.clevora.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ChangePassRequest {
    @NotBlank(message = "Mật khẩu cũ không được để trống")
    @Size(min = 6,message = "Mật khẩu cũ phải tối thiểu 6 ký tự")
    private String oldPassword;

    @NotBlank(message = "Mật khẩu mới không được để trống")
    @Size(min = 6,message = "Mật khẩu mới phải tối thiểu 6 ký tự")
    private String newPassword;
}
