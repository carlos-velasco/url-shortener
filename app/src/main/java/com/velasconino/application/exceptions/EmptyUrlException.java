package com.velasconino.application.exceptions;

/**
 * Exception thrown when a URL is null, empty, or blank.
 */
public class EmptyUrlException extends RuntimeException {
    
    public EmptyUrlException(String message) {
        super(message);
    }
    
    public EmptyUrlException(String message, Throwable cause) {
        super(message, cause);
    }
} 