package com.nexusenroll.common.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

/**
 * Standard API response envelope for all NexusEnroll microservices.
 *
 * <p>Matches the old backend's JSON format exactly to preserve frontend compatibility:
 * <pre>
 * {
 *   "statusCode": 200,
 *   "message":    "Success",
 *   "data":       { ... },
 *   "errors":     null        (omitted when null by @JsonInclude)
 * }
 * </pre>
 *
 * <p><strong>Migration note:</strong> The old backend had this in package
 * {@code com.nexusenroll.common.http} and serialized it manually via
 * {@code JsonUtil.apiResponseToJson()}. Jackson (built into Spring Boot's
 * {@code spring-boot-starter-web}) handles serialization automatically when
 * returned from a {@code @RestController} method.
 *
 * <p>The old {@code errors} field was {@code Map<String, String>} (field→message).
 * Changed to {@code List<String>} so that Bean Validation messages from
 * {@code @Valid} can be collected and passed directly without mapping.
 *
 * @param <T> the type of the {@code data} payload
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ApiResponse<T> {

    private final int statusCode;
    private final String message;
    private final T data;
    private final List<String> errors;

    private ApiResponse(int statusCode, String message, T data, List<String> errors) {
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
        this.errors = errors;
    }

    // -----------------------------------------------------------------------
    // Factory methods
    // -----------------------------------------------------------------------

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Success", data, null);
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(200, message, data, null);
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(201, "Created", data, null);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return new ApiResponse<>(201, message, data, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message) {
        return new ApiResponse<>(statusCode, message, null, null);
    }

    public static <T> ApiResponse<T> error(int statusCode, String message, List<String> errors) {
        return new ApiResponse<>(statusCode, message, null, errors);
    }

    // -----------------------------------------------------------------------
    // Accessors
    // -----------------------------------------------------------------------

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public List<String> getErrors() {
        return errors;
    }
}
