package com.nexusenroll.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Base exception for all NexusEnroll service errors.
 *
 * <p><strong>Migration note:</strong> The old backend extended {@code Exception}
 * (checked exception), which forced every caller to catch or declare it —
 * an anti-pattern for service-layer exceptions. In Spring Boot the convention
 * is {@code RuntimeException}: Spring's {@code @Transactional} only rolls back
 * on unchecked exceptions by default, and {@code @ControllerAdvice} handlers
 * work with unchecked exceptions without needing {@code throws} declarations.
 *
 * <p>All old {@code errorCode} and {@code statusCode} fields are preserved to
 * maintain backward compatibility with the frontend JSON envelope format.
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class ServiceException extends RuntimeException {

    private final String errorCode;
    private final int statusCode;

    public ServiceException(String message, String errorCode, int statusCode) {
        super(message);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    public ServiceException(String message, Throwable cause, String errorCode, int statusCode) {
        super(message, cause);
        this.errorCode = errorCode;
        this.statusCode = statusCode;
    }

    /** Convenience constructor for generic 500 errors. */
    public ServiceException(String message) {
        this(message, "INTERNAL_ERROR", 500);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
