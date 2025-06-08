package com.velasconino.application.exceptions;

/**
 * Exception thrown when a URL shortening operation cannot generate a unique code
 * within the maximum allowed length increase.
 */
public class UrlShorteningCollisionException extends RuntimeException {
    
    public UrlShorteningCollisionException(String message) {
        super(message);
    }
    
    public UrlShorteningCollisionException(String message, Throwable cause) {
        super(message, cause);
    }
} 