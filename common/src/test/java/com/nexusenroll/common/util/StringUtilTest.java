package com.nexusenroll.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for {@link StringUtil}.
 */
class StringUtilTest {

    // --- isEmpty ---

    @Test
    void isEmpty_null_returnsTrue() {
        assertTrue(StringUtil.isEmpty(null));
    }

    @Test
    void isEmpty_emptyString_returnsTrue() {
        assertTrue(StringUtil.isEmpty(""));
    }

    @Test
    void isEmpty_whitespaceOnly_returnsTrue() {
        assertTrue(StringUtil.isEmpty("   "));
    }

    @Test
    void isEmpty_nonEmpty_returnsFalse() {
        assertFalse(StringUtil.isEmpty("hello"));
    }

    // --- isValidEmail ---

    @Test
    void isValidEmail_validAddress_returnsTrue() {
        assertTrue(StringUtil.isValidEmail("alice@example.com"));
    }

    @Test
    void isValidEmail_plusAddress_returnsTrue() {
        assertTrue(StringUtil.isValidEmail("alice+tag@university.edu"));
    }

    @Test
    void isValidEmail_missingTld_returnsFalse() {
        // Old regex accepted "a@b" — new stricter regex requires TLD
        assertFalse(StringUtil.isValidEmail("alice@b"));
    }

    @Test
    void isValidEmail_missingAt_returnsFalse() {
        assertFalse(StringUtil.isValidEmail("aliceexample.com"));
    }

    @Test
    void isValidEmail_null_returnsFalse() {
        assertFalse(StringUtil.isValidEmail(null));
    }

    // --- isNumeric ---

    @Test
    void isNumeric_digits_returnsTrue() {
        assertTrue(StringUtil.isNumeric("12345"));
    }

    @Test
    void isNumeric_withLetters_returnsFalse() {
        assertFalse(StringUtil.isNumeric("123abc"));
    }

    @Test
    void isNumeric_empty_returnsFalse() {
        assertFalse(StringUtil.isNumeric(""));
    }

    // --- trimOrNull ---

    @Test
    void trimOrNull_blankString_returnsNull() {
        assertNull(StringUtil.trimOrNull("   "));
    }

    @Test
    void trimOrNull_paddedString_returnsTrimmed() {
        assertEquals("hello", StringUtil.trimOrNull("  hello  "));
    }

    // --- truncate ---

    @Test
    void truncate_longString_appendsEllipsis() {
        assertEquals("hel...", StringUtil.truncate("hello world", 3));
    }

    @Test
    void truncate_shortString_unchanged() {
        assertEquals("hi", StringUtil.truncate("hi", 10));
    }
}
