package com.clevora.clevora.dto.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO Response chuẩn cho mọi API thành công.
 *
 * Ví dụ response:
 * {
 *   "success": true,
 *   "message": "Đăng ký thành công",
 *   "data": { ... }
 * }
 *
 * Cách sử dụng trong Controller:
 *   return ResponseEntity.ok(new ApiResponse<>(true, "Thành công", data));
 *   return ResponseEntity.ok(ApiResponse.success("Thành công", data));
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;


    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }


    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }


    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
