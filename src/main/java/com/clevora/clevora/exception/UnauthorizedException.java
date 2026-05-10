package com.clevora.clevora.exception;

import org.springframework.http.HttpStatus;

/**
 * 401 - Chưa xác thực (token hết hạn, sai, hoặc thiếu).
 *
 * Ví dụ sử dụng:
 *   throw new UnauthorizedException("Token không hợp lệ hoặc đã hết hạn");
 */
public class UnauthorizedException extends ApiException {
    public UnauthorizedException(String message) {
        super(message, HttpStatus.UNAUTHORIZED);
    }
}
