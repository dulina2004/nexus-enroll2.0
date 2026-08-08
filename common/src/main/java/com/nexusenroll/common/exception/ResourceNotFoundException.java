package com.nexusenroll.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a requested resource cannot be found (HTTP 404).
 *
 * <p>errorCode changed from {@code "RESOURCE_NOT_FOUND"} to {@code "NOT_FOUND"}
 * for consistency with HTTP terminology. Both services and the frontend only
 * check {@code statusCode == 404}, not the errorCode string.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends ServiceException {

    public ResourceNotFoundException(String message) {
        super(message, "NOT_FOUND", 404);
    }

    public ResourceNotFoundException(String resourceType, Long id) {
        super(resourceType + " with id " + id + " was not found", "NOT_FOUND", 404);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause, "NOT_FOUND", 404);
    }
}
