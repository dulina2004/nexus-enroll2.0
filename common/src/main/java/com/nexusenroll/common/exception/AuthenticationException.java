package com.nexusenroll.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when credentials are invalid or a token is missing/expired (HTTP 401).
 *
 * <p>errorCode changed from {@code "AUTHENTICATION_ERROR"} to {@code "UNAUTHORIZED"}
 * for alignment with HTTP standard terminology. The numeric statusCode (401) is
 * unchanged, which is the only value the frontend checks.
 *
 * <p><strong>Note:</strong> Do not throw this for authorization failures (wrong role) —
 * use {@code AccessDeniedException} from Spring Security for 403 cases.
 */
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class AuthenticationException extends ServiceException {

    public AuthenticationException(String message) {
        super(message, "UNAUTHORIZED", 401);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause, "UNAUTHORIZED", 401);
    }
}
