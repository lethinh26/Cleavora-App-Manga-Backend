package com.clevora.clevora.dto.manga;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RejectRequest {
    @NotBlank(message = "Lý do từ chối không được để trống")
    private String rejectReason;
}
