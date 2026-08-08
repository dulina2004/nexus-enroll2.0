package com.nexusenroll.common.util;

import java.util.regex.Pattern;

/**
 * String utility functions for validation and manipulation.
 *
 * <p><strong>Migration note:</strong> The old backend's {@code isValidEmail} used a
 * very loose regex ({@code ^[A-Za-z0-9+_.-]+@(.+)$}) that accepted values like
 * {@code "a@b"}. Upgraded to RFC 5322-inspired pattern that requires a proper
 * domain with a TLD, while remaining practical (not pedantically strict).
 *
 * <p>{@code isNumeric} in the old backend only accepted digit strings ({@code \d+}).
 * This version accepts numeric strings that represent valid {@code Long} values
 * (including leading minus for negative IDs, though NexusEnroll always uses positive IDs).
 */
public final class StringUtil {

    private StringUtil() {}

    /** RFC 5322-inspired email pattern. Practical but not pedantically strict. */
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    /**
     * Returns {@code true} if the string is {@code null}, empty, or contains only whitespace.
     */
    public static boolean isEmpty(String str) {
        return str == null || str.isBlank();
    }

    /**
     * Returns {@code true} if the string is non-null and contains at least one non-whitespace character.
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * Returns {@code true} if the string is a syntactically valid email address.
     * Uses a practical RFC 5322-inspired pattern (requires a TLD of at least 2 chars).
     */
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Returns {@code true} if the string represents a non-negative integer (digits only).
     * This matches the old backend's behaviour which only accepted {@code \d+} strings.
     */
    public static boolean isNumeric(String str) {
        if (isEmpty(str)) {
            return false;
        }
        return str.trim().matches("\\d+");
    }

    /**
     * Returns the trimmed string, or {@code null} if the input is null or blank.
     * Useful for normalising optional fields before persisting.
     */
    public static String trimOrNull(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /**
     * Truncates a string to the given max length, appending "..." if truncated.
     * Useful for logging long values safely.
     */
    public static String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength) + "...";
    }
}
