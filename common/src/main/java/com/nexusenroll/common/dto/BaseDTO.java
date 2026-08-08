package com.nexusenroll.common.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Base class for all NexusEnroll Data Transfer Objects.
 *
 * <p>Provides common fields present on every domain object: {@code id},
 * {@code createdAt}, and {@code updatedAt}.
 *
 * <p><strong>Migration note:</strong> The old backend used {@code Long} epoch
 * milliseconds for timestamps (e.g. {@code System.currentTimeMillis()}).
 * This version uses {@code java.time.Instant}, which:
 * <ul>
 *   <li>Is timezone-unambiguous (always UTC)</li>
 *   <li>Serializes as ISO-8601 string by default via Jackson (e.g. {@code "2025-09-01T10:00:00Z"})</li>
 *   <li>Maps cleanly to JPA entities that use {@code LocalDateTime} or {@code Instant}</li>
 * </ul>
 *
 * <p>The {@code @JsonFormat} annotation ensures the frontend always receives an
 * ISO-8601 string regardless of JVM timezone settings. The frontend currently
 * does not parse these timestamps for display; they are used only for sort ordering.
 *
 * <p><strong>Constraint:</strong> This class is intentionally kept abstract.
 * Do NOT add JPA annotations ({@code @Entity}, {@code @MappedSuperclass}) here —
 * entity auditing is a per-service concern. Services that need entity-level
 * auditing should use Spring Data's {@code @EntityListeners(AuditingEntityListener.class)}
 * with {@code @CreatedDate} / {@code @LastModifiedDate} on their own base entity.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class BaseDTO {

    private Long id;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
    private Instant updatedAt;

    protected BaseDTO(Long id) {
        this.id = id;
    }
}
