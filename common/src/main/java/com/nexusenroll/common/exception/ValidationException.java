package com.nexusenroll.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when request data fails validation (HTTP 400).
 *
 * <p>errorCode preserved from old backend: {@code "VALIDATION_ERROR"}
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends ServiceException {

    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR", 400);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause, "VALIDATION_ERROR", 400);
    }
}
