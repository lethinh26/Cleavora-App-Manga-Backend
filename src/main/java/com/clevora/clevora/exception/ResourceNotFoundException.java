package com.clevora.clevora.exception;

import org.springframework.http.HttpStatus;

/**
 * 404 - Resource không tồn tại.
 *
 * Ví dụ sử dụng:
 *   throw new ResourceNotFoundException("Không tìm thấy truyện với id: " + id);
 */
public class ResourceNotFoundException extends ApiException {
    public ResourceNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
}
