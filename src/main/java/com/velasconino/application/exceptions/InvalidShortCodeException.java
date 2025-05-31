package com.velasconino.application.exceptions;

/**
 * Exception thrown when a short code is invalid.
 */
public class InvalidShortCodeException extends RuntimeException {
    
    public InvalidShortCodeException(String message) {
        super(message);
    }
    
    public InvalidShortCodeException(String message, Throwable cause) {
        super(message, cause);
    }
} 