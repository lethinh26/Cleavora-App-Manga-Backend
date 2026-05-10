package com.clevora.clevora.exception;

import org.springframework.http.HttpStatus;

/**
 * 400 - Request không hợp lệ.
 *
 * Ví dụ sử dụng:
 *   throw new BadRequestException("Email đã tồn tại trong hệ thống");
 */
public class BadRequestException extends ApiException {
    public BadRequestException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
