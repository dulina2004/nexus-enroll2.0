package com.nexusenroll.common.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the exception hierarchy.
 * Verifies error codes, HTTP status codes, and inheritance chain.
 */
class ExceptionHierarchyTest {

    @Test
    void serviceException_isRuntimeException() {
        ServiceException ex = new ServiceException("test", "TEST_CODE", 500);
        assertInstanceOf(RuntimeException.class, ex);
    }

    @Test
    void validationException_hasCorrectStatusAndCode() {
        ValidationException ex = new ValidationException("bad input");
        assertEquals(400, ex.getStatusCode());
        assertEquals("VALIDATION_ERROR", ex.getErrorCode());
    }

    @Test
    void resourceNotFoundException_hasCorrectStatusAndCode() {
        ResourceNotFoundException ex = new ResourceNotFoundException("not found");
        assertEquals(404, ex.getStatusCode());
        assertEquals("NOT_FOUND", ex.getErrorCode());
    }

    @Test
    void resourceNotFoundException_withResourceTypeAndId_buildsMessage() {
        ResourceNotFoundException ex = new ResourceNotFoundException("Student", 42L);
        assertTrue(ex.getMessage().contains("42"));
        assertTrue(ex.getMessage().contains("Student"));
    }

    @Test
    void authenticationException_hasCorrectStatusAndCode() {
        AuthenticationException ex = new AuthenticationException("bad token");
        assertEquals(401, ex.getStatusCode());
        assertEquals("UNAUTHORIZED", ex.getErrorCode());
    }

    @Test
    void allExceptionsExtendServiceException() {
        assertInstanceOf(ServiceException.class, new ValidationException("x"));
        assertInstanceOf(ServiceException.class, new ResourceNotFoundException("x"));
        assertInstanceOf(ServiceException.class, new AuthenticationException("x"));
    }
}
