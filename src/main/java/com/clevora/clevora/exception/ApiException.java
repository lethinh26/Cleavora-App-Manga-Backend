package com.clevora.clevora.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

/**
 * Base exception cho toàn bộ app.
 * Tất cả custom exception kế thừa class này.
 */
@Getter
public class ApiException extends RuntimeException {

    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }
}
