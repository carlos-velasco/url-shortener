package com.velasconino.application.exceptions;

/**
 * Exception thrown when a URL has an invalid format.
 */
public class InvalidUrlException extends RuntimeException {
    
    public InvalidUrlException(String message) {
        super(message);
    }
    
    public InvalidUrlException(String message, Throwable cause) {
        super(message, cause);
    }
} 