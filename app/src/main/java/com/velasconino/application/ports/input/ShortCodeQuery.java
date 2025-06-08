package com.velasconino.application.ports.input;

import com.velasconino.application.exceptions.InvalidShortCodeException;

/**
 * Query object for retrieving original URLs by short code.
 * Validates the short code format before passing it to the use case.
 */
public record ShortCodeQuery(String shortCode) {
    
    /**
     * Creates a new ShortCodeQuery, validating the short code.
     * 
     * @param shortCode The short code to validate
     * @throws InvalidShortCodeException if the short code is null, empty, or invalid
     */
    public ShortCodeQuery {
        if (shortCode == null || shortCode.trim().isEmpty()) {
            throw new InvalidShortCodeException("Short code cannot be null or empty");
        }
    }
} 