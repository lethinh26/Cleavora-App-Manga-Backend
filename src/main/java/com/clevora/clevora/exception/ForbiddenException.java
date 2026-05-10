package com.clevora.clevora.exception;

import org.springframework.http.HttpStatus;

/**
 * 403 - Không có quyền truy cập.
 *
 * Ví dụ sử dụng:
 *   throw new ForbiddenException("Chỉ SUPERADMIN mới có quyền thay đổi role");
 */
public class ForbiddenException extends ApiException {
    public ForbiddenException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
}
