package com.velasconino.application.ports.input;

import java.util.Optional;

/**
 * Use case interface for resolving a short code to its original URL.
 */
public interface ResolveShortUrlUseCase {
    
    /**
     * Resolves a short code to its original URL.
     * 
     * @param query The validated short code query
     * @return An Optional containing the original URL if found, or empty if not found
     */
    Optional<String> resolveShortUrl(ShortCodeQuery query);
} 