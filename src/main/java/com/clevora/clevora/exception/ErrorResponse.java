package com.clevora.clevora.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO chuẩn cho mọi error response trả về client.
 *
 * Ví dụ response:
 * {
 *   "timestamp": "2026-05-10T10:00:00",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Không tìm thấy truyện với id: 99",
 *   "path": "/api/v1/mangas/99"
 * }
 */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
